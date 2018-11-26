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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProfileImportServiceImpl implements ProfileImportService {

  private final LinkedHashSet<String> importQueue = new LinkedHashSet<>();

  private final List<String> imported = new ArrayList<>();

  private final List<String> failed = new ArrayList<>();

  private InstagramProfileService profileService;

  private static final int CHUNK_SIZE = 10;

  /**
   * Ctor.
   *
   * @param profileService InstagramProfileService
   */
  @Autowired
  public ProfileImportServiceImpl(InstagramProfileService profileService) {
    this.profileService = profileService;
  }

  @Override
  public ImportProfileResponse getCurrentQueue() {
    return new ImportProfileResponse(
        this.importQueue.size(),
        new ArrayList<>(this.importQueue),
        this.imported,
        this.failed
    );
  }

  @Override
  public ImportProfileResponse add(Set<String> profiles) {
    Optional.ofNullable(profiles).ifPresent(this.importQueue::addAll);
    return new ImportProfileResponse(
        this.importQueue.size(),
        new ArrayList<>(this.importQueue),
        this.imported,
        this.failed
    );
  }

  /**
   * Process import for profile import queue.
   */
  @Scheduled(cron = "0 0/1 * * * *")
  public void processImport() {
    log.info("Processing profile import, current queue size: {}", this.importQueue.size());
    final List<String> chunk = this.importQueue.stream()
        .limit(CHUNK_SIZE)
        .collect(Collectors.toList());
    chunk.forEach(
        profile -> {
          try {
            profileService.importLastPosts(profile);
            this.imported.add(profile);
          } catch (Exception ex) {
            log.info("Error processing import for profile {}", profile, ex);
            this.failed.add(profile);
          }
        }
    );
    this.importQueue.removeAll(chunk);
  }

}
