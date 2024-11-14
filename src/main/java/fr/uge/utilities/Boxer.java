package fr.uge.utilities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public record Boxer(String ContentDescription, String content) {
  public Boxer {
    Objects.requireNonNull(ContentDescription);
    Objects.requireNonNull(content);
  }
}