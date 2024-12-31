package fr.uge.utilities;

import java.util.Objects;

/**
 * The SimpleBoxer record is used to encapsulate simple content descriptions along with their respective content.
 * This can be used for structuring content and its description in various scenarios, such as message passing or response handling.
 */
public record SimpleBoxer(String contentDescription, String content) {

  /**
   * Constructor that validates both fields to ensure they are not null.
   *
   * @param contentDescription A description or label for the content.
   * @param content            The actual content associated with the description.
   */
  public SimpleBoxer {
    Objects.requireNonNull(contentDescription, "contentDescription must not be null");
    Objects.requireNonNull(content, "content must not be null");
  }
}
