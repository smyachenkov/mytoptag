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
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.LinkedList;

@Document(collection = "tags")
@Data
@NoArgsConstructor
public class InstagramTag {
  @Id
  private String id;
  private String name;
  private Long ig_id;
  private LinkedList<InstagramTagHistory> history;

  public InstagramTag(String name, Long count, Long ig_id) {
    this.name = name.toLowerCase();
    this.ig_id = ig_id;
    LinkedList<InstagramTagHistory> tagHistory = new LinkedList();
    tagHistory.add(new InstagramTagHistory(new Date(), count));
    this.history = tagHistory;
  }

  public InstagramTag(String name, Long ig_id) {
    this.name = name.toLowerCase();
    this.ig_id = ig_id;
  }

  public InstagramTagHistory getLastHistoryEntry() {
    return history.getLast();
  }
}
