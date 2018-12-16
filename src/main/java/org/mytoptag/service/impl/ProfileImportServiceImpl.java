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
import org.mytoptag.model.dto.response.ImportProfileResponse;
import org.mytoptag.service.InstagramProfileService;
import org.mytoptag.service.ProfileImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * {@link ProfileImportService} implementation.
 */
@Service
@Slf4j
public class ProfileImportServiceImpl implements ProfileImportService {

  private static final int CHUNK_SIZE = 20;

  private final Set<String> importQueue = new LinkedHashSet<>();

  private final List<String> imported = new ArrayList<>();

  private final List<String> failed = new ArrayList<>();

  private final ReentrantLock lock = new ReentrantLock();

  private InstagramProfileService profileService;

  /**
   * Ctor.
   *
   * @param profileService InstagramProfileService
   */
  @Autowired
  public ProfileImportServiceImpl(final InstagramProfileService profileService) {
    this.profileService = profileService;
  }

  @Override
  public ImportProfileResponse getCurrentQueue() {
    return new ImportProfileResponse(
        new ArrayList<>(this.importQueue),
        this.imported,
        this.failed
    );
  }

  @Override
  public ImportProfileResponse add(final Set<String> profiles) {
    this.lock.lock();
    try {
      Optional.ofNullable(profiles).ifPresent(this.importQueue::addAll);
    } finally {
      this.lock.unlock();
    }
    return new ImportProfileResponse(
        new ArrayList<>(this.importQueue),
        this.imported,
        this.failed
    );
  }

  /**
   * Process import for profile import queue.
   */
  @Scheduled(cron = "${cron.profileimport}")
  public void processImport() {
    this.lock.lock();
    try {
      log.info("Processing profile import, current queue size: {}", this.importQueue.size());
      final List<String> chunk = this.importQueue.stream()
          .limit(CHUNK_SIZE)
          .collect(Collectors.toList());
      chunk.forEach(
          profile -> {
            try {
              profileService.importLastPosts(profile);
              this.imported.add(profile);
            } catch (final IOException ex) {
              log.info("Error processing import for profile {}", profile, ex);
              this.failed.add(profile);
            }
          }
      );
      this.importQueue.removeAll(chunk);
    } finally {
      this.lock.unlock();
    }
  }

  @Override
  @Scheduled(cron = "0 0 1 * * SUN")
  public ImportProfileResponse removeProcessed() {
    this.lock.lock();
    try {
      this.failed.clear();
      this.imported.clear();
    } finally {
      this.lock.unlock();
    }
    return new ImportProfileResponse(
        new ArrayList<>(this.importQueue),
        this.imported,
        this.failed
    );
  }

}
