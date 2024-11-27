package fr.uge.utilities;

import java.util.Objects;

public record HistoryDTO(String classText, String llmResponse, String compilerResponse, String history,
                         String timestamp) {

  public HistoryDTO {
    Objects.requireNonNull(classText);
    Objects.requireNonNull(llmResponse);
    Objects.requireNonNull(compilerResponse);
    Objects.requireNonNull(history);
    Objects.requireNonNull(timestamp);
  }
}

