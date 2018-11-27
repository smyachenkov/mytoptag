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

package org.mytoptag.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.mytoptag.model.InstagramPost;
import org.mytoptag.model.InstagramProfile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InstagramProfileDeserializer extends StdDeserializer<InstagramProfile> {

  protected InstagramProfileDeserializer() {
    this(null);
  }

  protected InstagramProfileDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public InstagramProfile deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    final JsonNode node = parser.getCodec().readTree(parser);
    final List<InstagramPost> posts = parsePosts(node);
    final String username = parseUserName(node);
    return new InstagramProfile(username, posts);
  }

  private String parseUserName(JsonNode node) {
    return node.get("entry_data")
        .get("ProfilePage")
        .get(0)
        .get("graphql")
        .get("user")
        .get("username")
        .asText();
  }

  private List<InstagramPost> parsePosts(JsonNode node) {
    final List<InstagramPost> posts = new ArrayList<>();
    JsonNode mediaNodes = node.get("entry_data")
        .get("ProfilePage")
        .get(0)
        .get("graphql")
        .get("user")
        .get("edge_owner_to_timeline_media")
        .get("edges");
    for (int i = 0; i < mediaNodes.size(); i++) {
      JsonNode mediaNode = mediaNodes.get(i).get("node");
      Long id = mediaNode.get("id").asLong();
      JsonNode edgesArray = mediaNode
          .get("edge_media_to_caption")
          .get("edges");
      final String text;
      if (edgesArray.isArray() && edgesArray.size() > 0) {
        text = edgesArray.get(0)
          .get("node")
          .get("text")
          .asText();
      } else {
        text = "";
      }
      Integer likedBy = mediaNode
            .get("edge_media_preview_like")
          .get("count")
          .asInt();
      String previewLink = mediaNode
          .get("display_url")
          .asText();
      String shortCode = mediaNode
          .get("shortcode")
          .asText();
      Long timeStamp = mediaNode
          .get("taken_at_timestamp")
          .asLong();
      posts.add(new InstagramPost(id, text, likedBy, previewLink, shortCode, timeStamp));

    }
    return posts;
  }
}
