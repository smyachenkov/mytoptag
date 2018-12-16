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

import org.mytoptag.model.InstagramPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * {@link InstagramPost} repository.
 */
public interface InstagramPostRepository extends JpaRepository<InstagramPost, String> {

  /**
   * Find posts by it's instagram id.
   *
   * @param igId instagram post id
   * @return InstagramPost
   */
  InstagramPost findByIgId(Long igId);

  /**
   * Find posts by short code.
   *
   * @param shortCodes list of instagram short codes
   * @return list of {@link InstagramPost}
   */
  List<InstagramPost> findByShortCodeIn(List<String> shortCodes);

}
