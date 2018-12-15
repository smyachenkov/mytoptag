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

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 * Instagram Post model.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "POST")
public class InstagramPost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "IG_ID")
  private Long igId;

  @Column(name = "POST_TEXT")
  private String text;

  @Column(name = "POST_DATE")
  private Long date;

  @ManyToMany(cascade = {
      CascadeType.PERSIST,
      CascadeType.MERGE
      })
  @JoinTable(name = "TAGINPOST",
      joinColumns = @JoinColumn(name = "POST_ID"),
      inverseJoinColumns = @JoinColumn(name = "TAG_ID")
  )
  private Set<InstagramTag> tags = new HashSet<>();

  @Column(name = "PREVIEW_LINK")
  private String previewLink;

  @Column(name = "SHORT_CODE")
  private String shortCode;

  private int likes;

  /**
   * Ctor.
   *
   * @param igId        Instagram post id
   * @param text        Text of a post
   * @param likes       Number of likes
   * @param previewLink Link to small preview pic
   * @param shortCode   Code for post url
   * @param date        Date
   */
  public InstagramPost(final Long igId,
                       final String text,
                       final int likes,
                       final String previewLink,
                       final String shortCode,
                       final Long date) {
    this.igId = igId;
    this.text = text;
    this.likes = likes;
    this.previewLink = previewLink;
    this.shortCode = shortCode;
    this.date = date;
  }

}
