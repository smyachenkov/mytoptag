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

package org.mytoptag.repository;

import org.mytoptag.model.Category;
import org.mytoptag.model.dto.query.TagCategorySuggestionQueryResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;


/**
 * {@link Category} repository.
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {

  /**
   * Find category by title.
   *
   * @param title category title
   * @return Category
   */
  Category findByTitle(String title);

  /**
   * Find category by title.
   *
   * @param title category title
   * @return Category
   */
  List<Category> findByTitleLike(String title);


  /**
   * Find relevant tags and categories.
   *
   * @param search search query for category title
   * @return list of {@link TagCategorySuggestionQueryResult}
   */
  @Query(
      value = "select \n"
          + " cat.title \"category\",\n"
          + " t.title \"tag\", \n"
          + " tc.count \"count\",\n"
          + " tic.sort_order \"sortOrder\"\n"
          + "from\n"
          + "category cat \n"
          + "join tagincategory tic\n"
          + " on tic.category_id = cat.id\n"
          + "join tag t\n"
          + " on tic.tag_id = t.id\n"
          + "left join tagcount tc\n"
          + " on tc.tag_id = t.id\n"
          + " and tc.count_date = (select max(count_date) from tagcount where tag_id = t.id)\n"
          + "where cat.title like %:search% \n"
          + "order by cat.title asc, tic.sort_order asc",
      nativeQuery = true
  )
  List<TagCategorySuggestionQueryResult> findRelevantTags(@Param("search") String search);

  /**
   * Clear category table.
   */
  @Modifying
  @Transactional
  @Query(value = "delete from category", nativeQuery = true)
  void clear();

}
