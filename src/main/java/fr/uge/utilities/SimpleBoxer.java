package fr.uge.utilities;

import java.util.Objects;

public record SimpleBoxer(String contentDescription, String content) {
  public SimpleBoxer {
    Objects.requireNonNull(contentDescription);
    Objects.requireNonNull(content);
  }
}