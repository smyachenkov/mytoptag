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

import org.mytoptag.model.InstagramPost;

import java.io.IOException;
import java.util.List;


/**
 * Instagram profile service.
 */
public interface InstagramProfileService {

  /**
   * Import last posts of user.
   *
   * @param username Instagram account username
   * @return Lists of InstagramPost
   * @throws IOException If can't parse json response
   */
  List<InstagramPost> importLastPosts(final String username) throws IOException;

  /**
   * Retrieve existing posts from repo.
   *
   * @param shortCodes codes of instagram post
   * @return list of posts
   */
  List<InstagramPost> findPosts(final List<String> shortCodes);

  /**
   * Get last posts of user.
   *
   * @param username Instagram account username
   * @return Lists of InstagramPost
   * @throws IOException If can't parse json response
   */
  List<InstagramPost> getLastPosts(final String username) throws IOException;

}
