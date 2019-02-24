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

package org.mytoptag.controller;

import lombok.extern.slf4j.Slf4j;
import org.mytoptag.model.dto.ListResponseEntity;
import org.mytoptag.model.dto.request.CategorizedTagRequest;
import org.mytoptag.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Category controller.
 */
@RestController
@CrossOrigin
@RequestMapping(
    value = "/category",
    produces = {"application/json"}
    )
@Slf4j
public class CategoryController {

  private CategoryService categoryService;

  /**
   * Ctor.
   *
   * @param categoryService {@link CategoryService}
   */
  @Autowired
  public CategoryController(final CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  /**
   * Save category entries.
   *
   * @param categorizedTags list of categorized tags.
   */
  @RequestMapping(
      value = "/add",
      method = RequestMethod.POST
    )
  public void add(@RequestBody final CategorizedTagRequest categorizedTags) {
    categoryService.save(categorizedTags.getTags());
  }

  /**
   * Clear categories mapping.
   */
  @RequestMapping(
      value = "/clear",
      method = RequestMethod.GET
    )
  public void clear() {
    categoryService.clear();
  }

  /**
   * List categories.
   *
   * @param title title of a category
   * @return list of tags in category
   */
  @RequestMapping(
      value = "/{title}",
      method = RequestMethod.GET
    )
  public ListResponseEntity listCategories(@PathVariable("title") final String title) {
    return new ListResponseEntity(categoryService.getCategoryTags(title));
  }

}
