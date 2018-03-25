package org.mytoptag.model;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InstagramPostTest {

  @Test
  public void createsListOfTagsFromText() {
    String text = "#hello world #travel #photo";
    assertEquals( 3, new InstagramPost(1L, text).getTags().size(),
        "Simple ctor can't convert text to tags");
    assertEquals(3, new InstagramPost(1L, text, 22, "link", "code").getTags().size(),
        "Full ctor can't convert text to tags");
  }

  @Test
  public void acceptsEmptyText() {
    String text = "";
    assertEquals( 0, new InstagramPost(1L, text).getTags().size(),
        "Simple ctor can't process empty text");
    assertEquals(0, new InstagramPost(1L, text, 22, "link", "code").getTags().size(),
        "Full ctor can't process empty text");
  }

  @Test
  public void acceptsNullText() {
    String text = null;
    assertEquals( 0, new InstagramPost(1L, text).getTags().size(),
        "Simple ctor can't process null text");
    assertEquals(0, new InstagramPost(1L, text, 22, "link", "code").getTags().size(),
        "Full ctor can't process null text");
  }

}
