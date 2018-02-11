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

import org.mytoptag.controller.ObjectNotFoundException;
import org.mytoptag.model.InstagramTag;
import org.mytoptag.repository.InstagramTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InstagramTagService {
  private static final String INSTAGRAM_SEARCH_URL = "https://www.instagram.com/web/search/topsearch/?query={tag}";
  private static final String HASHTAG_SEARCH_START_SYMBOL = "#";
  private static final String HASHTAGS_NODE = "hashtags";
  private static final String HASHTAG_NODE = "hashtag";
  private static final String HASHTAG_ID_FIELD = "id";
  private static final String HASHTAG_NAME_FIELD = "name";
  private static final String HASHTAG_COUNT_FIELD = "media_count";

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
        newTag = existingTag;
      } else {
        newTag = getTagFromWeb(tag);
        if (newTag != null) {
          instagramTagRepository.save(newTag);
        }
      }
    return newTag;
  }

  public void updateAllTagHistory() {
    instagramTagRepository.findAll().forEach(this::updateTagHistory);
  }

  public void updateTagHistory(InstagramTag currentTag) {
    InstagramTag newTag = getTagFromWeb(currentTag.getName());
    currentTag.getHistory().add(newTag.getHistory().get(0));
    instagramTagRepository.save(currentTag);
  }

  private InstagramTag getTagFromWeb(String name) {
    RestTemplate restTemplate = new RestTemplate();
    name = name.startsWith(HASHTAG_SEARCH_START_SYMBOL) ? name : HASHTAG_SEARCH_START_SYMBOL + name;

    String responseJson = restTemplate.getForObject(INSTAGRAM_SEARCH_URL, String.class, name);
    return getTopTagFromJson(responseJson);
  }

  private InstagramTag getTopTagFromJson(String json) {
    JsonParser parser = JsonParserFactory.getJsonParser();
    Map jsonMap = parser.parseMap(json);
    ArrayList hashttags = (ArrayList)jsonMap.get(HASHTAGS_NODE);
    if (hashttags.isEmpty()) {
      return null;
    }
    Map topTag = (Map)((Map)hashttags.get(0)).get(HASHTAG_NODE);
    String tagName = (String)topTag.get(HASHTAG_NAME_FIELD);
    Long tagIgId = (Long)topTag.get(HASHTAG_ID_FIELD);
    Long tagCount = Long.valueOf((Integer)topTag.get(HASHTAG_COUNT_FIELD));
    return new InstagramTag(tagName, tagCount, tagIgId);
  }
}
