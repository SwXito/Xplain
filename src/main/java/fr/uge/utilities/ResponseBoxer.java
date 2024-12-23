package fr.uge.utilities;

import java.util.Objects;

public record ResponseBoxer(String contentDescription, String classText, String compilerResponse, String llmResponse, boolean success) {
  public ResponseBoxer {
    Objects.requireNonNull(contentDescription);
    Objects.requireNonNull(classText);
    Objects.requireNonNull(compilerResponse);
    Objects.requireNonNull(llmResponse);
  }
}
