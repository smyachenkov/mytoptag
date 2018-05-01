package org.mytoptag.service;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.mytoptag.controller.ObjectNotFoundException;
import org.mytoptag.model.InstagramTag;
import org.mytoptag.repository.InstagramTagRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InstagramTagServiceTest {

  private InstagramTagRepository tagRepository = mock(InstagramTagRepository.class);

  private InstagramTagService tagService = spy(new InstagramTagService(tagRepository));

  @Test
  public void getExistingTagReturnsTagIfFound() {
    InstagramTag tag = mock(InstagramTag.class);
    when(tagRepository.findByName(any())).thenReturn(tag);
    InstagramTag result = tagService.getExistingTag(anyString());
    assertEquals("Can't return existing tag", result, tag);
  }

  @Test
  public void getExistingThrowsExceptionIfNotFound() {
    when(tagRepository.findByName(any())).thenReturn(null);
    Assertions.assertThrows(ObjectNotFoundException.class, () -> tagService.getExistingTag(anyString()),
        "Can't throw an ObjectNotFoundException when object is absent");
  }

  @Test
  public void addTagSavesSingleTag() {
    InstagramTag tag = mock(InstagramTag.class);
    when(tagRepository.findByName("tag")).thenReturn(null);
    when(tagService.getTagFromWeb("tag")).thenReturn(tag);
    when(tagRepository.save(tag)).thenReturn(tag);
    assertEquals("Can't save a single tag", tag, tagService.addTag("tag").get(0));
  }

  @Test
  public void addTagSavesListOfTags() {
    when(tagRepository.findByName(anyString())).thenReturn(null);
    when(tagService.getTagFromWeb(anyString())).thenReturn(mock(InstagramTag.class));
    List<String> tags = Arrays.asList("travel", "photo", "mood");
    assertEquals("Can't save list of tags", tags.size(), tagService.addTag(tags).size());
  }

  @Test
  public void addTagSavesOnlyNewTag() {
    InstagramTag existingTag = mock(InstagramTag.class);
    InstagramTag newTag = mock(InstagramTag.class);
    when(tagRepository.findByName("travel")).thenReturn(existingTag);
    when(tagRepository.findByName("photo")).thenReturn(existingTag);
    when(tagRepository.findByName("mood")).thenReturn(null);
    when(tagService.getTagFromWeb("mood")).thenReturn(newTag);
    when(tagRepository.save(newTag)).thenReturn(newTag);
    List<InstagramTag> tags = tagService.addTag(Arrays.asList("travel", "photo", "mood"));
    assertEquals("Can't process all tags", 3, tags.size());
    assertEquals("Can't process existing tags", 2, tags.stream().filter(existingTag::equals).count());
    assertEquals("Can't process new tags", 1, tags.stream().filter(newTag::equals).count());
  }

  @Test
  public void addTagIgnoresTagsAbsentOnInstagram() {
    when(tagRepository.findByName(anyString())).thenReturn(null);
    when(tagService.getTagFromWeb(anyString())).thenReturn(null);
    assertEquals("Doesn't ignore tags that are absent on Instagram",
        0, tagService.addTag(Arrays.asList("travel", "photo")).size());
  }

  @Test
  public void getTagRetrievesExistingTag() {
    InstagramTag existingTag = mock(InstagramTag.class);
    when(tagRepository.findByName(anyString())).thenReturn(existingTag);
    assertEquals("Can't retrieve existing tag from repository",
        existingTag, tagService.getTag(anyString()));
  }

  @Test
  public void getTagRetrievesSavesNewTag() {
    InstagramTag newTag = mock(InstagramTag.class);
    when(tagRepository.findByName(anyString())).thenReturn(null);
    when(tagService.getTagFromWeb(anyString())).thenReturn(newTag);
    when(tagRepository.save(newTag)).thenReturn(newTag);
    assertEquals("Can't save new tag if tag is present on Instagram",
        newTag, tagService.getTag(anyString()));
  }

  @Test
  public void getTagProducesExceptionWhenCantRetrieveTagFromInstagram() {
    when(tagRepository.findByName(anyString())).thenReturn(null);
    when(tagService.getTagFromWeb(anyString())).thenReturn(null);
    Assertions.assertThrows(ObjectNotFoundException.class, () -> tagService.getTag(anyString()),
        "Can't throw an ObjectNotFoundException when tag is absent in repository and Instagram");
  }

}
