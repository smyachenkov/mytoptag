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

import org.mytoptag.model.InstagramPost;
import org.mytoptag.model.dto.ListResponseEntity;
import org.mytoptag.service.InstagramProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(
    value = "/profile",
    produces = {"application/json"},
    method = RequestMethod.GET
)
public class ProfileController {

  private InstagramProfileService instagramProfileService;

  @Autowired
  public ProfileController(InstagramProfileService instagramProfileService) {
    this.instagramProfileService = instagramProfileService;
  }

  /**
   * Import last posts of user.
   *
   * @param name Instagram account username
   * @return List of last 12 posts
   */
  @RequestMapping(
      value = "/import/{name}",
      produces = {"application/json"},
      method = RequestMethod.GET
  )
  public ListResponseEntity importLastPosts(@PathVariable("name") String name) {
    try {
      return new ListResponseEntity(instagramProfileService.importLastPosts(name));
    } catch (IOException ex) {
      throw new ObjectNotFoundException();
    }
  }

  /**
   * Get last posts of user.
   *
   * @param name Instagram account username
   * @return List of last 12 posts
   */
  @RequestMapping(
      value = "/view/{name}",
      produces = {"application/json"},
      method = RequestMethod.GET
  )
  public ListResponseEntity getLastPosts(@PathVariable("name") String name) {
    try {
      List<InstagramPost> posts = instagramProfileService.getLastPosts(name);
      return new ListResponseEntity(posts);
    } catch (IOException ex) {
      throw new ObjectNotFoundException();
    }
  }

}
