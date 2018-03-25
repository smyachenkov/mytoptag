package org.mytoptag.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.mytoptag.model.InstagramPost;
import org.mytoptag.repository.InstagramTagRepository;

import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class)
public class InstagramProfileServiceTest {

  private InstagramTagRepository tagRepository = mock(InstagramTagRepository.class);

  private InstagramTagService tagService = new InstagramTagService(tagRepository);

  private InstagramProfileService profileService = spy(new InstagramProfileService(tagRepository, tagService));

  @Test
  public void getLastTagsRemovesDuplicates() throws IOException {
    List<InstagramPost> posts = Arrays.asList(
        new InstagramPost(1L, "#travel, #photo"),
        new InstagramPost(2L, "#travel, #test")
    );
    when(profileService.getLastPosts("user")).thenReturn(posts);
    Assert.assertEquals("Can't process all tags in user posts",
        3, profileService.getLastTags("user").size());
  }

  @Test
  public void getLastTagsAllowsEmptyTexts() throws IOException {
    List<InstagramPost> posts = Collections.singletonList(new InstagramPost(1L, ""));
    when(profileService.getLastPosts("user")).thenReturn(posts);
    Assert.assertEquals("Can't process post with empty text",
        0, profileService.getLastTags("user").size());
  }

  @Test
  public void getLastTagsParsesOnlyTags() throws IOException {
    List<InstagramPost> posts = Arrays.asList(
        new InstagramPost(1L, "hello world #travel #photo"),
        new InstagramPost(2L, "this text has no tags")
    );
    when(profileService.getLastPosts("user")).thenReturn(posts);
    Assert.assertEquals("Can't process text without tags",
        2, profileService.getLastTags("user").size());
  }

}
