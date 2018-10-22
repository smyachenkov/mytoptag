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

package org.mytoptag.controller;

import org.mytoptag.model.dto.ListResponseEntity;
import org.mytoptag.service.InstagramTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping(
    value = "/tag",
    produces = {"application/json"}
)
public class TagController {

  private InstagramTagService instagramTagService;

  @Autowired
  public TagController(InstagramTagService instagramTagService) {
    this.instagramTagService = instagramTagService;
  }

  /**
   * Get existing tags.
   * @param titles tags titles
   * @return list of existing tags
   */
  @RequestMapping(
      value = "/{title}",
      produces = {"application/json"},
      method = RequestMethod.GET
  )
  public ListResponseEntity getTag(@PathVariable("title") Set<String> titles) {
    return new ListResponseEntity(instagramTagService.getTags(titles));
  }

  /**
   * Delete tags from db.
   * @param titles tags titles
   */
  @RequestMapping(
      value = "/{title}",
      produces = {"application/json"},
      method = RequestMethod.DELETE
  )
  public void deleteTag(@PathVariable("title") Set<String> titles) {
    instagramTagService.deleteTags(titles);
  }

  /**
   * Import new tags from instagram.
   *
   * @param tags set of tags titles
   * @return list of created tags
   */
  @RequestMapping(
      value = "/import/{title}",
      produces = {"application/json"},
      method = RequestMethod.GET
  )
  public ListResponseEntity addTag(@PathVariable("title") Set<String> tags) {
    return new ListResponseEntity(instagramTagService.addTag(tags));
  }

}
