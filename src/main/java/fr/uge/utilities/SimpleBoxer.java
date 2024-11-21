package fr.uge.utilities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public record Boxer(String contentDescription, String content) {
  public Boxer {
    Objects.requireNonNull(contentDescription);
    Objects.requireNonNull(content);
  }
}