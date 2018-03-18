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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mytoptag.model.InstagramPost;
import org.mytoptag.model.InstagramProfile;
import org.mytoptag.model.dto.SimpleCountedTag;
import org.mytoptag.repository.InstagramTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class InstagramProfileService {

  private static final String INSTAGRAM_URL = "https://www.instagram.com/";
  private static final String JSON_KEY = "window._sharedData = ";

  private InstagramTagRepository instagramTagRepository;
  private InstagramTagService instagramTagService;

  @Autowired
  public InstagramProfileService(InstagramTagRepository instagramTagRepository,
                                 InstagramTagService instagramTagService) {
    this.instagramTagRepository = instagramTagRepository;
    this.instagramTagService = instagramTagService;
  }

  public List<InstagramPost> getLastPosts(String username) throws IOException {
    Document document = Jsoup.connect(INSTAGRAM_URL + username).get();
    String jsonData = document.body()
        .getElementsByTag("script")
        .first()
        .childNode(0)
        .toString();
    String jsonString = jsonData.substring(JSON_KEY.length(), jsonData.length() - 1);
    InstagramProfile profile = new ObjectMapper().readValue(jsonString, InstagramProfile.class);
    return profile.getPosts();
  }

  public Set<String> getLastTags(String username) throws IOException {
    List<InstagramPost> posts = getLastPosts(username);
    return posts.stream()
        .flatMap(post -> post.getTags().stream())
        .collect(Collectors.toCollection(TreeSet::new));
  }

  public Set<SimpleCountedTag> getLastTagsCounted(String username) throws IOException {
    Set<SimpleCountedTag> result = new HashSet<>();
    List<InstagramPost> posts = getLastPosts(username);
    Set<String> userTags = posts.stream()
        .flatMap(post -> post.getTags().stream())
        .collect(Collectors.toSet());

    Set<SimpleCountedTag> existingTags = instagramTagRepository.findByNameIn(userTags).stream()
        .map(SimpleCountedTag::new)
        .collect(Collectors.toSet());

    result.addAll(existingTags);
    userTags.removeAll(existingTags.stream().map(SimpleCountedTag::getTag).collect(Collectors.toSet()));

    userTags.stream()
        .map(name -> instagramTagService.addTag(name))
        .filter(tag -> tag != null)
        .map(SimpleCountedTag::new)
        .forEach(result::add);

    return result;
  }
}