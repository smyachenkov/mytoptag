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

package org.mytoptag.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.mytoptag.model.InstagramTag;
import org.mytoptag.repository.InstagramTagRepository;
import org.mytoptag.service.InstagramTagService;
import org.mytoptag.service.TagCountUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;


/**
 * {@link TagCountUpdateService} implementation.
 */
@Service
@Slf4j
public class TagCountUpdateServiceImpl implements TagCountUpdateService {

  private static final int CHUNK_SIZE = 100;

  private InstagramTagRepository tagRepository;

  private InstagramTagService tagService;

  /**
   * Ctor.
   *
   * @param instagramTagRepository {@link InstagramTagRepository}
   * @param instagramTagService {@link InstagramTagService}
   */
  @Autowired
  public TagCountUpdateServiceImpl(
      final InstagramTagRepository instagramTagRepository,
      final InstagramTagService instagramTagService) {
    this.tagRepository = instagramTagRepository;
    this.tagService = instagramTagService;
  }

  @Override
  @Scheduled(cron = "${cron.tagcount.update}")
  @Transactional
  public void updateTagCount() {
    log.info("there are {} tags without actual count number", tagRepository.countNotUpdated());
    final List<Integer> tagIds = tagRepository.findNotUpdated(CHUNK_SIZE);
    final List<InstagramTag> tags = tagRepository.findAllById(tagIds);
    log.info("updating tag count for tags {}",
        tags.stream().map(InstagramTag::getTitle).collect(Collectors.toList()));
    tagService.updateTagHistory(tags);
    log.info("tag update complete");
  }
}
