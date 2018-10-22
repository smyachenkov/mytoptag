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
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mytoptag.model.InstagramPost;
import org.mytoptag.model.InstagramProfile;
import org.mytoptag.model.InstagramTag;
import org.mytoptag.repository.InstagramPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InstagramProfileService {

  private static final String INSTAGRAM_URL = "https://www.instagram.com/";
  private static final String JSON_KEY = "window._sharedData = ";

  private InstagramTagService tagService;
  private InstagramPostRepository postRepository;

  @Autowired
  public InstagramProfileService(
      InstagramTagService instagramTagService,
      InstagramPostRepository postRepository) {
    this.tagService = instagramTagService;
    this.postRepository = postRepository;
  }

  /**
   * Import last posts of user.
   *
   * @param username Instagram account username
   * @return Lists of InstagramPost
   * @throws IOException If can't parse json response
   */
  public List<InstagramPost> importLastPosts(final String username) throws IOException {
    log.info("importing posts for account {}", username);
    final InstagramProfile profile = retrieveInstagramProfile(username);
    List<InstagramPost> posts = profile.getPosts();
    List<String> codes = posts.stream()
        .map(InstagramPost::getShortCode)
        .collect(Collectors.toList());
    List<String> existingCodes = postRepository.findByShortCodeIn(codes).stream()
        .map(InstagramPost::getShortCode)
        .collect(Collectors.toList());
    List<InstagramPost> result = posts.stream()
        .filter(p -> !existingCodes.contains(p.getShortCode()))
        .map(this::savePost).collect(Collectors.toList());
    log.info("import for account {} is complete! {} new posts have been added",
        username, result.size());
    return result;
  }

  private InstagramPost savePost(InstagramPost post) {
    log.info("saving posts {}", post.getShortCode());
    InstagramPost existing = postRepository.findByIgId(post.getIgId());
    if (existing != null) {
      log.info("post with a code {} is already imported", post.getShortCode());
      return existing;
    }
    log.info("saving new post {} to db", post.getShortCode());
    List<InstagramTag> tags = tagService.addTag(tagsFromText(post.getText()));
    post.setTags(new HashSet<>(tags));
    return postRepository.save(post);
  }

  public List<InstagramPost> findPosts(List<String> shortCodes) {
    return postRepository.findByShortCodeIn(shortCodes);
  }

  /**
   * Get last posts of user.
   *
   * @param username Instagram account username
   * @return Lists of InstagramPost
   * @throws IOException If can't parse json response
   */
  public List<InstagramPost> getLastPosts(final String username) throws IOException {
    final InstagramProfile profile = retrieveInstagramProfile(username);
    return profile.getPosts();
  }

  private InstagramProfile retrieveInstagramProfile(final String username) throws IOException {
    Document document = Jsoup.connect(INSTAGRAM_URL + username).get();
    String jsonData = document.body()
        .getElementsByTag("script")
        .first()
        .childNode(0)
        .toString();
    String jsonString = jsonData.substring(JSON_KEY.length(), jsonData.length() - 1);
    return new ObjectMapper().readValue(jsonString, InstagramProfile.class);
  }

  private Set<String> tagsFromText(String text) {
    return Optional.ofNullable(text).map(
        t -> Arrays.stream(text.split(" |\n"))
            .filter(word -> word.contains("#"))
            .map(word -> word.substring(word.indexOf('#'), word.length()))
            .map(String::toLowerCase)
            .map(tag -> tag.startsWith("#") ? tag.substring(1, tag.length()) : tag)
            .sorted()
            .collect(Collectors.toSet())
    ).orElse(Collections.EMPTY_SET);
  }

}
