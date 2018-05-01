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

package org.mytoptag.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.mytoptag.model.InstagramPost;

import java.util.List;

@Getter
@Setter
public class InstagramPostCounted {

  private Long id;

  private String text;

  private int likes;

  private String previewLink;

  private String shortCode;

  private List<InstagramTagCounted> tags;

  /**
   * Ctor.
   * @param post InstagramPost
   */
  public InstagramPostCounted(final InstagramPost post) {
    this.id = post.getId();
    this.text = post.getText();
    this.likes = post.getLikes();
    this.previewLink = post.getPreviewLink();
    this.shortCode = post.getShortCode();
  }

  /**
   * Ctor.
   * @param post InstagramPost
   * @param tags List of InstagramTagCounted
   */
  public InstagramPostCounted(final InstagramPost post,
                              final List<InstagramTagCounted> tags) {
    this.id = post.getId();
    this.text = post.getText();
    this.likes = post.getLikes();
    this.previewLink = post.getPreviewLink();
    this.shortCode = post.getShortCode();
    this.tags = tags;
  }

}