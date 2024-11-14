package fr.uge.utilities;

import java.util.Optional;
import java.util.regex.*;

public class Parser {

  public static Optional<String> getClassName(String javaFile) {
    Pattern pattern = Pattern.compile("\\b(class|interface|enum|record)\\s+(\\w+)");
    Matcher matcher = pattern.matcher(javaFile);
    if (matcher.find()) {
      return Optional.of(matcher.group(2));
    } else {
      return Optional.empty();
    }
  }
}
