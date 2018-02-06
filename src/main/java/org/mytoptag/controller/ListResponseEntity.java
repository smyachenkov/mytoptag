package org.mytoptag.controller;

import lombok.Data;

import java.util.List;

@Data
public class ListResponseEntity {
  private List data;

  public ListResponseEntity(List data) {
    this.data = data;
  }
}
