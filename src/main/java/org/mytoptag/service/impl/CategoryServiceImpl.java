/*
 * Copyright (c) 2019 Stanislav Myachenkov
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
import org.mytoptag.model.Category;
import org.mytoptag.model.CategoryTag;
import org.mytoptag.model.CategoryTagKey;
import org.mytoptag.model.InstagramTag;
import org.mytoptag.model.dto.request.CategorizedTag;
import org.mytoptag.repository.CategoryRepository;
import org.mytoptag.repository.CategoryTagRepository;
import org.mytoptag.repository.InstagramTagRepository;
import org.mytoptag.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link CategoryService} implementation.
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

  private CategoryRepository categoryRepository;

  private InstagramTagRepository tagRepository;

  private CategoryTagRepository categoryTagRepository;

  /**
   * Ctor.
   *
   * @param categoryRepository {@link CategoryRepository}
   * @param categoryTagRepository {@link CategoryTagRepository}
   * @param instagramTagRepository {@link InstagramTagRepository}
   */
  @Autowired
  public CategoryServiceImpl(final CategoryRepository categoryRepository,
                             final InstagramTagRepository instagramTagRepository,
                             final CategoryTagRepository categoryTagRepository) {
    this.categoryRepository = categoryRepository;
    this.tagRepository = instagramTagRepository;
    this.categoryTagRepository = categoryTagRepository;
  }

  @Override
  public void save(final List<CategorizedTag> categorizedTags) {
    final List<Category> categories = categorizedTags.stream()
        .map(CategorizedTag::getCategory)
        .distinct()
        .map(title -> {
          final Category category = categoryRepository.findByTitle(title);
          return Optional.ofNullable(category).orElseGet(() -> {
            log.info("Creating new category {}", title);
            return categoryRepository.save(
                new Category(
                    null,
                    title,
                    Collections.emptyList()
                )
            );
          });
        })
        .collect(Collectors.toList());
    categorizedTags.forEach(
        categorizedTag -> {
          final InstagramTag tag = tagRepository.findByTitle(categorizedTag.getTagTitle());
          if (tag != null) {
            final Category category = categories.stream()
                .filter(c -> c.getTitle().equals(categorizedTag.getCategory()))
                .findFirst()
                .get();
            categoryTagRepository.save(
                new CategoryTag(
                    new CategoryTagKey(tag.getId(), category.getId()),
                    category,
                    tag,
                    categorizedTag.getWeight()
                )
            );
          } else {
            log.info("Tag #{} is not found in repository", categorizedTag.getTagTitle());
          }
        }
    );

  }

  @Override
  public void clear() {
    categoryTagRepository.clear();
    categoryRepository.clear();
  }
}
