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

package org.mytoptag.service;

import lombok.extern.slf4j.Slf4j;
import org.mytoptag.model.Compatibility;
import org.mytoptag.model.CompatibilityKey;
import org.mytoptag.model.InstagramTag;
import org.mytoptag.repository.CompatibilityRepository;
import org.mytoptag.repository.InstagramPostRepository;
import org.mytoptag.repository.InstagramTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class SuggestionService {

  private InstagramTagRepository tagRepository;

  private InstagramPostRepository postRepository;

  private CompatibilityRepository compatibilityRepository;

  @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
  private Integer maxBatchSize;

  private static final Integer SCALE = 5;

  /**
   * Ctor.
   * @param tagRepository instagram tag repo.
   * @param postRepository instagram post repo.
   * @param compatibilityRepository compatibility matrix repo.
   */
  @Autowired
  public SuggestionService(
      InstagramTagRepository tagRepository,
      InstagramPostRepository postRepository,
      CompatibilityRepository compatibilityRepository
  ) {
    this.tagRepository = tagRepository;
    this.postRepository = postRepository;
    this.compatibilityRepository = compatibilityRepository;
  }

  /**
   * Update tag compatibility matrix.
   */
  @Async("processExecutor")
  public void updateCompatibilityMatrix() {
    compatibilityRepository.clearCompatibilityMatrix();
    final Integer[] tags = tagRepository.findAll().stream()
        .map(InstagramTag::getId)
        .toArray(Integer[]::new);
    final int matrixSize = tags.length;
    for (int i = 0; i < matrixSize; i++) {
      Integer tag = tags[i];
      Integer allPostsOccurrence = postRepository.countPostsWithTags(Collections.singletonList(tag));
      List<Compatibility> compatibilities = new ArrayList<>();
      for (int j = matrixSize - 1; j > i; j--) {
        Integer compatiblePosts = postRepository.countPostsWithTags(Arrays.asList(tag, tags[j]));
        BigDecimal compatibilityValue = BigDecimal.valueOf(compatiblePosts)
            .divide(BigDecimal.valueOf(allPostsOccurrence), SCALE, BigDecimal.ROUND_HALF_UP);
        compatibilities.add(
            new Compatibility(
                new CompatibilityKey(tag, tags[j]),
                compatibilityValue.doubleValue()
            )
        );
        log.info("tag: #{}, all posts: {}, compatible posts {}",
            tag,
            allPostsOccurrence,
            compatiblePosts);
      }
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

  // todo implement
  public Set<InstagramTag> getRecommendations(final Set<String> tagNames) {
    List<InstagramTag> originalTags = tagRepository.findByTitleIn(tagNames);
    return new HashSet<>(originalTags);
  }

}
