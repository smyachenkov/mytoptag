/*
 * Copyright (c) 2018 Stanislav Myachenkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.mytoptag.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.mytoptag.model.Compatibility;
import org.mytoptag.model.CompatibilityKey;
import org.mytoptag.model.PostsOfTag;
import org.mytoptag.model.dto.TagSuggestion;
import org.mytoptag.model.dto.query.TagCategorySuggestionQueryResult;
import org.mytoptag.repository.CategoryRepository;
import org.mytoptag.repository.CompatibilityRepository;
import org.mytoptag.repository.PostsOfTagRepository;
import org.mytoptag.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * {@link SuggestionService} implementation.
 */
@Service
@Slf4j
public class SuggestionServiceImpl implements SuggestionService {

  private static final Integer SCALE = 5;

  private static final Integer MAX_CATEGORIES = 10;

  private static final Integer MAX_TAGS_IN_POST = 30;

  private PostsOfTagRepository postsOfTagRepository;

  private CompatibilityRepository compatibilityRepository;

  private CategoryRepository categoryRepository;

  @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
  private Integer maxBatchSize;

  /**
   * Ctor.
   *
   * @param compatibilityRepository {@link CompatibilityRepository}
   * @param postsOfTagRepository    {@link PostsOfTagRepository}
   * @param categoryRepository      {@link CategoryRepository}
   */
  @Autowired
  public SuggestionServiceImpl(
      final CompatibilityRepository compatibilityRepository,
      final PostsOfTagRepository postsOfTagRepository,
      final CategoryRepository categoryRepository) {
    this.compatibilityRepository = compatibilityRepository;
    this.postsOfTagRepository = postsOfTagRepository;
    this.categoryRepository = categoryRepository;
  }

  /**
   * Update tag compatibility matrix.
   */
  @Async("processExecutor")
  public void updateCompatibilityMatrix() {
    compatibilityRepository.clearCompatibilityMatrix();
    final Map<Integer, List<Integer>> tagsMap = postsOfTagRepository.findAll()
        .stream()
        .collect(Collectors.toMap(PostsOfTag::getTag, v -> Arrays.asList(v.getPosts())));
    final Integer[] tags = tagsMap.keySet().toArray(new Integer[0]);
    final int matrixSize = tags.length;
    for (int i = 0; i < matrixSize; i++) {
      log.info("Calculating compatibility for tag {}", tags[i]);
      final Integer allPostsOccurrence = tagsMap.get(tags[i]).size();
      final List<Compatibility> compatibilities = new ArrayList<>();
      for (int j = matrixSize - 1; j > i; j--) {
        final long compatiblePosts = tagsMap.get(tags[i])
            .stream()
            .filter(tagsMap.get(tags[j])::contains)
            .count();
        if (compatiblePosts > 0) {
          final BigDecimal compatibilityValue = new BigDecimal(compatiblePosts).divide(
              BigDecimal.valueOf(allPostsOccurrence),
              SCALE,
              BigDecimal.ROUND_HALF_UP
          );
          compatibilities.add(
              new Compatibility(
                  new CompatibilityKey(tags[i], tags[j]),
                  compatibilityValue.doubleValue()
              )
          );
        }
      }
      log.info("Saving {} compatibility entries for tag {}", compatibilities.size(), tags[i]);
      saveInBatches(compatibilities);
    }
    log.info("compatibility matrix has been updated successfully, new entries amount: {}",
        compatibilityRepository.count());
  }

  private void saveInBatches(final List<Compatibility> compatibilities) {
    final List<Compatibility> batch = new ArrayList<>();
    for (int i = 0; i < compatibilities.size(); i++) {
      batch.add(compatibilities.get(i));
      if (batch.size() == maxBatchSize || i == compatibilities.size() - 1) {
        compatibilityRepository.saveAll(batch);
        batch.clear();
      }
    }
  }

  /**
   * Retrieves most relevant tags according to tag category relations.
   *
   * @param input set of users tags
   * @return List of {@link TagSuggestion}
   */
  public List<TagSuggestion> getRecommendations(final Set<String> input) {
    final Map<String, Map<String, List<TagSuggestion>>> inputResult = new HashMap<>();
    input.stream().limit(MAX_CATEGORIES).forEach(i -> {
      final List<TagCategorySuggestionQueryResult> queryResults = categoryRepository.findRelevantTags(i);
      final Map<String, List<TagSuggestion>> inputSearchResult =
          queryResults.stream()
              .collect(Collectors.groupingBy(TagCategorySuggestionQueryResult::getCategory))
              .entrySet().stream()
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      e -> e.getValue().stream().map(TagSuggestion::new).collect(Collectors.toList()),
                      (u, v) -> {
                        throw new IllegalStateException("Duplicate key");
                      },
                      LinkedHashMap::new
                  )
              );
      inputResult.put(i, inputSearchResult);
    });
    final List<TagSuggestion> suggestions = new LinkedList<>();
    inputResult.forEach((i, res) ->
        suggestions.addAll(
            res.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList()))
    );
    return suggestions.stream()
        .sorted(Comparator.comparing(TagSuggestion::getSortOrder))
        .limit(MAX_TAGS_IN_POST)
        .sorted(Comparator.comparing(TagSuggestion::getCategory))
        .collect(Collectors.toList());
  }

}
