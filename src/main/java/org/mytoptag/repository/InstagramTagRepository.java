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

import org.mytoptag.model.InstagramTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;

public interface InstagramTagRepository extends JpaRepository<InstagramTag, Integer> {

  InstagramTag findByTitle(String title);

  List<InstagramTag> findByTitleIn(Set<String> names);

  List<InstagramTag> findAll();

  @Query(
      nativeQuery = true,
      value = "select t.id from \n"
            + "  tag t\n"
            + "left join tagcount tc\n"
            + "  on tc.tag_id = t.id\n"
            + "where   \n"
            + " tc.count_date is null or tc.count_date < now() - interval '10 day'\n"
            + "order by tc.count_date desc\n"
            + "limit :size"
  )
  List<Integer> findNotUpdated(@Param("size") Integer size);

  @Query(
      nativeQuery = true,
      value = "select count(1) from \n"
            + "  tag t\n"
            + "left join tagcount tc\n"
            + "  on tc.tag_id = t.id\n"
            + "where   \n"
            + " tc.count_date is null or tc.count_date < now() - interval '10 day'"
  )
  Integer countNotUpdated();

  @Transactional
  void deleteByTitle(String title);
}
