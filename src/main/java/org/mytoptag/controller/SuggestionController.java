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

import org.mytoptag.model.InstagramTag;
import org.mytoptag.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping(
    value = "/suggestion",
    produces = {"application/json"}
)
public class SuggestionController {

  private SuggestionService suggestionService;

  @Autowired
  public SuggestionController(SuggestionService suggestionService) {
    this.suggestionService = suggestionService;
  }

  /**
   * Get all tags compatibility matrix.
   * @return tag matrix
   */
  @RequestMapping(
      value = "/updateMatrix",
      produces = {"application/json"},
      method = RequestMethod.GET
  )
  public ResponseEntity<?> updateTagMatrix() {
    suggestionService.updateCompatibilityMatrix();
    return new ResponseEntity<>("Tag compatibility matrix update started", HttpStatus.OK);
  }

  /**
   * Get most relevant tags for current user's.
   * @return tag matrix
   */
  @RequestMapping(
      value = "/tags/",
      produces = {"application/json"},
      method = RequestMethod.GET
  )
  // todo return dto with latest count
  public Set<InstagramTag> getRecommendations(@PathVariable("title") Set<String> tags) {
    return suggestionService.getRecommendations(tags);
  }

}
