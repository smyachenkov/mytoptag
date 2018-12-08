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

import org.mytoptag.controller.ObjectNotFoundException;
import org.mytoptag.model.InstagramTag;
import org.mytoptag.model.InstagramTagCount;

import java.util.List;
import java.util.Set;


public interface InstagramTagService {

  /**
   * Gets existing tag from repository or from Instagram if absent.
   *
   * @param name Tag
   * @return InstagramTag entry
   * @throws ObjectNotFoundException If tag does not exist in repository or on Instagram
   */
  InstagramTag getTag(final String name) throws ObjectNotFoundException;

  /**
   * Gets existing tag from repository or from Instagram if absent.
   *
   * @param names Tags titles
   * @return InstagramTag entry
   * @throws ObjectNotFoundException If tag does not exist in repository or on Instagram
   */
  List<InstagramTag> getTags(final Set<String> names) throws ObjectNotFoundException;

  /**
   * Saves list of tags to repository.
   *
   * @param tags List of tag names
   * @return List of InstagramTag entries
   */
  List<InstagramTag> addTag(Set<String> tags);

  /**
   * Updates history for all tags in repository.
   */
  void updateAllTagHistory();

  /**
   * Updates existing tags history.
   *
   * @param tags InstagramTag list
   */
  List<InstagramTagCount> updateTagHistory(List<InstagramTag> tags);

  /**
   * Delete tag from repo.
   *
   * @param titles list of tags by titles
   */
  void deleteTags(Set<String> titles);

}
