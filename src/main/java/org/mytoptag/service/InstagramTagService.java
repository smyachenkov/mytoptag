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
import org.mytoptag.model.InstagramTagCount;
import org.mytoptag.model.dto.InstagramSearch;
import org.mytoptag.repository.InstagramTagCountRepository;
import org.mytoptag.repository.InstagramTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InstagramTagService {

  private static final String INSTAGRAM_SEARCH_URL = "https://www.instagram.com/web/search/topsearch/?query={tag}";

  private InstagramTagRepository instagramTagRepository;

  private InstagramTagCountRepository instagramTagCountRepository;

  @Autowired
  public InstagramTagService(
      InstagramTagRepository instagramTagRepository,
      InstagramTagCountRepository instagramTagCountRepository
  ) {
    this.instagramTagRepository = instagramTagRepository;
    this.instagramTagCountRepository = instagramTagCountRepository;
  }

  /**
   * Gets existing tag from repository or from Instagram if absent.
   *
   * @param name Tag
   * @return InstagramTag entry
   * @throws ObjectNotFoundException If tag does not exist in repository or on Instagram
   */
  public InstagramTag getTag(final String name) throws ObjectNotFoundException {
    log.info("retrieving tag {} from repository ", name);
    InstagramTag tag = instagramTagRepository.findByTitle(name);
    if (tag != null) {
      return tag;
    } else {
      log.info("tag {} is absent in repo, retrieving from web", name);
      tag = getTagFromWeb(name);
      if (tag == null) {
        log.error("failed to retrieve tag {} from instagram", name);
        throw new ObjectNotFoundException();
      } else {
        log.error("saving new tag {} to repo", name);
        return instagramTagRepository.save(tag);
      }
    }
  }

  /**
   * Gets existing tag from repository or from Instagram if absent.
   *
   * @param names Tags titles
   * @return InstagramTag entry
   * @throws ObjectNotFoundException If tag does not exist in repository or on Instagram
   */
  public List<InstagramTag> getTags(final Set<String> names) throws ObjectNotFoundException {
    return names.stream().map(this::getExistingTag).collect(Collectors.toList());
  }

  /**
   * Gets existing tag from repository.
   *
   * @param name Tag
   * @return InstagramTag entry
   * @throws ObjectNotFoundException If tag does not exist in repository
   */
  private InstagramTag getExistingTag(String name) throws ObjectNotFoundException {
    log.info("retrieving tag {} from repository ", name);
    InstagramTag tag = instagramTagRepository.findByTitle(name);
    if (tag == null) {
      log.error("failed to retrieve tag {} from repo", name);
      throw new ObjectNotFoundException();
    }
    return tag;
  }

  /**
   * Saves list of tags to repository.
   *
   * @param tags List of tag names
   * @return List of InstagramTag entries
   */
  public List<InstagramTag> addTag(Set<String> tags) {
    List<InstagramTag> newTags = new ArrayList<>();
    for (String tag : tags) {
      log.info("retrieving tag {} from repository ", tag);
      InstagramTag existingTag = instagramTagRepository.findByTitle(tag);
      if (existingTag != null) {
        newTags.add(existingTag);
      } else {
        log.info("tag {} is absent in repo, retrieving from web", tag);
        InstagramTag newTag = getTagFromWeb(tag);
        if (newTag != null) {
          log.info("saving new tag {}", newTag.getTitle());
          newTags.add(instagramTagRepository.save(newTag));
        }
      }
    }
    return newTags;
  }

  /**
   * Updates history for all tags in repository.
   */
  public void updateAllTagHistory() {
    log.info("updating all tags info in repo");
    instagramTagRepository.findAll().forEach(this::updateTagHistory);
    log.info("tags update complete!");
  }

  /**
   * Updates existing tags history.
   *
   * @param tags InstagramTag list
   */
  public List<InstagramTagCount> updateTagHistory(List<InstagramTag> tags) {
    return tags.stream().map(this::updateTagHistory).collect(Collectors.toList());
  }

  /**
   * Updates existing tag history.
   *
   * @param tag InstagramTag entry
   */
  private InstagramTagCount updateTagHistory(InstagramTag tag) {
    try {
      log.info("updating history for tag #{}", tag.getTitle());
      if (tag.getLastCount() != null && getCurrentDate().equals(tag.getLastCount().getDate())) {
        log.info("tag #{} already has count for today", tag.getTitle());
        return tag.getLastCount();
      }
      String responseJson = new RestTemplate().getForObject(
          INSTAGRAM_SEARCH_URL,
          String.class,
          tag.getTitle()
      );
      InstagramSearch search = new ObjectMapper().readValue(responseJson, InstagramSearch.class);
      InstagramTagCount count = search.getHashtags()
          .stream()
          .filter(t -> t.getTitle().equals(tag.getTitle()))
          .findFirst()
          .map(s -> new InstagramTagCount(tag, s.getCount()))
          .orElse(null);
      log.info("new #{} count is {}", tag.getTitle(), count.getCount());
      return instagramTagCountRepository.save(count);
    } catch (IOException exception) {
      log.error("Search failed for tag {}", tag);
      return null;
    }
  }

  private Date getCurrentDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * Parse and return tag from instagram website.
   *
   * @param title Tag name
   * @return InstagramTag
   */
  private InstagramTag getTagFromWeb(String title) {
    try {
      log.info("retrieving tag {} from web", title);
      RestTemplate restTemplate = new RestTemplate();
      String responseJson = restTemplate.getForObject(INSTAGRAM_SEARCH_URL, String.class, title);
      InstagramSearch search = new ObjectMapper().readValue(responseJson, InstagramSearch.class);
      return search.getHashtags()
          .stream()
          .filter(t -> t.getTitle().equals(title))
          .findFirst()
          .map(s -> new InstagramTag(s.getTitle(), s.getIgId()))
          .orElse(null);
    } catch (IOException exception) {
      log.error("Search failed for tag {}", title);
      return null;
    }
  }

  /**
   * Delete tag from repo.
   *
   * @param titles list of tags by titles
   */
  public void deleteTags(Set<String> titles) {
    titles.forEach(t -> {
          log.info("deleting tag #{} from repo", t);
          instagramTagRepository.deleteByTitle(t);
          log.info("tag #{} is deleted");
        }
    );
  }
}
