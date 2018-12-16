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

package org.mytoptag.repository;

import org.mytoptag.model.Compatibility;
import org.mytoptag.model.dto.query.TagSuggestionQueryResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import javax.transaction.Transactional;


/**
 * {@link Compatibility} repository.
 */
public interface CompatibilityRepository extends JpaRepository<Compatibility, Integer> {

  /**
   * Clear compatibility matrix.
   */
  @Modifying
  @Transactional
  @Query(value = "truncate table COMPATIBILITY", nativeQuery = true)
  void clearCompatibilityMatrix();


  /**
   * Get posts with max compatibility.
   *
   * @param tagIds list of tag id's
   * @return list of {@link TagSuggestionQueryResult}
   */
  @Query(value =
          "select\n"
          + "  t.title,\n"
          + "  c.compatibility,\n"
          + "  max(tc.count) count\n"
          + "from compatibility c\n"
          + "join tag t\n"
          + "  on t.id = c.tag_b\n"
          + "join tagcount tc\n"
          + "  on tc.tag_id = c.tag_b\n"
          + "where\n"
          + "  c.tag_a in :ids\n"
          + "group by t.title, c.compatibility\n"
          + "order by c.compatibility desc\n"
          + "limit 30",
      nativeQuery = true)
  List<TagSuggestionQueryResult> getCompatibleTags(@Param("ids") List<Integer> tagIds);

}
