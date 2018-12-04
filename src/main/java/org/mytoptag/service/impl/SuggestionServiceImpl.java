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
import org.mytoptag.model.InstagramTag;
import org.mytoptag.model.PostsOfTag;
import org.mytoptag.model.dto.TagSuggestion;
import org.mytoptag.model.dto.query.TagSuggestionQueryResult;
import org.mytoptag.repository.CompatibilityRepository;
import org.mytoptag.repository.InstagramTagRepository;
import org.mytoptag.repository.PostsOfTagRepository;
import org.mytoptag.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SuggestionServiceImpl implements SuggestionService {

  private InstagramTagRepository tagRepository;

  private PostsOfTagRepository postsOfTagRepository;

  private CompatibilityRepository compatibilityRepository;

  @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
  private Integer maxBatchSize;

  private static final Integer SCALE = 5;

  /**
   * Ctor.
   *
   * @param tagRepository           instagram tag repo.
   * @param compatibilityRepository compatibility matrix repo.
   * @param postsOfTagRepository    posts of tag repo.
   */
  @Autowired
  public SuggestionServiceImpl(
      InstagramTagRepository tagRepository,
      CompatibilityRepository compatibilityRepository,
      PostsOfTagRepository postsOfTagRepository
  ) {
    this.tagRepository = tagRepository;
    this.compatibilityRepository = compatibilityRepository;
    this.postsOfTagRepository = postsOfTagRepository;
  }

  /**
   * Update tag compatibility matrix.
   */
  @Async("processExecutor")
  public void updateCompatibilityMatrix() {
    compatibilityRepository.clearCompatibilityMatrix();
    Map<Integer, List<Integer>> tagsMap = postsOfTagRepository.findAll()
        .stream()
        .collect(Collectors.toMap(PostsOfTag::getTag, v -> Arrays.asList(v.getPosts())));
    final Integer[] tags = tagsMap.keySet().toArray(new Integer[0]);
    final int matrixSize = tags.length;
    for (int i = 0; i < matrixSize; i++) {
      log.info("Calculating compatibility for tag {}", tags[i]);
      Integer allPostsOccurrence = tagsMap.get(tags[i]).size();
      List<Compatibility> compatibilities = new ArrayList<>();
      for (int j = matrixSize - 1; j > i; j--) {
        long compatiblePosts = tagsMap.get(tags[i])
            .stream()
            .filter(tagsMap.get(tags[j])::contains)
            .count();
        if (compatiblePosts > 0) {
          BigDecimal compatibilityValue = new BigDecimal(compatiblePosts).divide(
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
  }

  private void saveInBatches(final List<Compatibility> compatibilities) {
    List<Compatibility> batch = new ArrayList<>();
    for (int i = 0; i < compatibilities.size(); i++) {
      batch.add(compatibilities.get(i));
      if (batch.size() == maxBatchSize || i == compatibilities.size() - 1) {
        compatibilityRepository.saveAll(batch);
        batch.clear();
      }
    }
  }

  /**
   * Retrieves most relevant tags according to compatibility matrix.
   *
   * @param tags set of users tags
   * @return List of {@link TagSuggestion}
   */
  public List<TagSuggestion> getRecommendations(final Set<String> tags) {
    final List<InstagramTag> originalTags = tagRepository.findByTitleIn(tags);
    final List<TagSuggestionQueryResult> compatibilities =
        compatibilityRepository.getCompatiblePosts(
            originalTags.stream().map(InstagramTag::getId).collect(Collectors.toList())
        );
    return compatibilities.stream().map(TagSuggestion::new).collect(Collectors.toList());
  }

}
