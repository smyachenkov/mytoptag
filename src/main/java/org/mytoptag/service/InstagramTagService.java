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
import org.mytoptag.controller.ObjectNotFoundException;
import org.mytoptag.model.InstagramTag;
import org.mytoptag.model.dto.InstagramSearch;
import org.mytoptag.repository.InstagramTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InstagramTagService {

  private static final String INSTAGRAM_SEARCH_URL = "https://www.instagram.com/web/search/topsearch/?query={tag}";

  private InstagramTagRepository instagramTagRepository;

  @Autowired
  public InstagramTagService(InstagramTagRepository instagramTagRepository) {
    this.instagramTagRepository = instagramTagRepository;
  }

  public InstagramTag getExistingTag(String name) throws ObjectNotFoundException {
    InstagramTag tag = instagramTagRepository.findByName(name);
    if (tag == null) {
      throw new ObjectNotFoundException();
    }
    return tag;
  }

  public List<InstagramTag> addTag(List<String> tags) {
    List<InstagramTag> newTags = new ArrayList<>();
    for (String tag : tags) {
      InstagramTag existingTag = instagramTagRepository.findByName(tag);
      if (existingTag != null) {
        newTags.add(existingTag);
      } else {
        InstagramTag newTag = getTagFromWeb(tag);
        if (newTag != null) {
          newTags.add(instagramTagRepository.save(newTag));
        }
      }
    }
    return newTags;
  }

  public InstagramTag addTag(String tag) {
    InstagramTag newTag;
    InstagramTag existingTag = instagramTagRepository.findByName(tag);
    if (existingTag != null) {
      return existingTag;
    }
    newTag = getTagFromWeb(tag);
    Optional.ofNullable(getTagFromWeb(tag)).ifPresent(t -> instagramTagRepository.save(t));
    return newTag;
  }

  public void updateAllTagHistory() {
    instagramTagRepository.findAll().forEach(this::updateTagHistory);
  }

  public void updateTagHistory(InstagramTag currentTag) {
    Optional.ofNullable(getTagFromWeb(currentTag.getName())).ifPresent(
        newTag -> {
          currentTag.getHistory().add(newTag.getHistory().get(0));
          instagramTagRepository.save(currentTag);
        }
    );
  }

  private InstagramTag getTagFromWeb(String name) {
    RestTemplate restTemplate = new RestTemplate();
    String responseJson = restTemplate.getForObject(INSTAGRAM_SEARCH_URL, String.class, name);
    return getTopTagFromJson(responseJson, name);
  }

  private InstagramTag getTopTagFromJson(String json, String tag) {
    try {
      InstagramSearch search = new ObjectMapper().readValue(json, InstagramSearch.class);
      return search.getHashtags().stream()
          .filter(t -> t.getName().equals(tag))
          .findFirst()
          .orElse(null);
    } catch (IOException e) {
      log.error("Search failed for tag {}", tag);
      return null;
    }
  }
}
