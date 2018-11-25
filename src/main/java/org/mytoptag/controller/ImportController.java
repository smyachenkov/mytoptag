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

import lombok.extern.slf4j.Slf4j;
import org.mytoptag.model.response.ImportProfileResponse;
import org.mytoptag.service.InstagramProfileService;
import org.mytoptag.service.ProfileImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping(
    value = "/import",
    produces = {"application/json"}
    )
@Slf4j
public class ImportController {

  private ProfileImportService profileImportService;

  private InstagramProfileService profileService;

  @Autowired
  public ImportController(ProfileImportService profileImportService,
                          InstagramProfileService profileService) {
    this.profileImportService = profileImportService;
    this.profileService = profileService;
  }

  /**
   * Add new set of profiles to import queue.
   *
   * @param profiles set of account usernames
   * @return ImportProfileResponse
   */
  @RequestMapping(
      value = "/",
      method = RequestMethod.POST
  )
  public ImportProfileResponse addToQueue(@RequestParam("profiles") Set<String> profiles) {
    return profileImportService.add(profiles);
  }


  /**
   * Get current queue status.
   *
   * @return ImportProfileResponse
   */
  @RequestMapping(
      value = "/",
      method = RequestMethod.GET
  )
  public ImportProfileResponse getQueueStatus() {
    return profileImportService.getCurrentQueue();
  }


  /**
   * Test
   *
   * @return ImportProfileResponse
   */
  @RequestMapping(
      value = "/profile/{profile}",
      method = RequestMethod.GET
  )
  public void test(@PathVariable("profile") String profile) throws IOException {
    profileService.importLastPosts(profile);
  }

}
