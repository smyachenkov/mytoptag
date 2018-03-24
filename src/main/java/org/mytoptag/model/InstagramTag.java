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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mytoptag.model.deserializer.InstagramSearchDeserializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedList;

@Document(collection = "tags")
@Data
@NoArgsConstructor
@JsonDeserialize(using = InstagramSearchDeserializer.class)
public class InstagramTag {

  @Id
  private String id;

  private String name;

  @Field("igId")
  private Long igId;

  private LinkedList<InstagramTagHistory> history;

  /**
   * Ctor.
   * @param name Tag name
   * @param count Total tag count
   * @param igId Instagram id of a tag
   */
  public InstagramTag(String name, Long count, Long igId) {
    this.name = name.toLowerCase();
    this.igId = igId;
    LinkedList<InstagramTagHistory> tagHistory = new LinkedList<>();
    tagHistory.add(new InstagramTagHistory(new Date(), count));
    this.history = tagHistory;
  }

  /**
   * Ctor.
   * @param name Tag name
   * @param igId Instagram id of a tag
   */
  public InstagramTag(String name, Long igId) {
    this.name = name.toLowerCase();
    this.igId = igId;
  }

  public InstagramTagHistory getLastHistoryEntry() {
    return history.getLast();
  }
}
