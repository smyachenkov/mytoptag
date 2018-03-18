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
package org.mytoptag.model;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class InstagramPost {
  private Long id;
  private String text;
  private List<String> tags;
  private int likes;

  public InstagramPost(Long id, String text) {
    this.id = id;
    this.text = text;
    this.tags = tagsFromText(text);
  }

  public InstagramPost(Long id, String text, Integer likes) {
    this.id = id;
    this.text = text;
    this.tags = tagsFromText(text);
    this.likes = likes;
  }

  private List<String> tagsFromText(String text) {
    return Arrays.stream(text.split(" |\n"))
        .filter(word -> word.contains("#"))
        .map(word -> word.substring(word.indexOf("#"), word.length()))
        .map(String::toLowerCase)
        .map(tag -> tag.startsWith("#") ? tag.substring(1, tag.length()) : tag)
        .sorted()
        .collect(Collectors.toList());
  }
}