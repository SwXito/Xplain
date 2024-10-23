package fr.uge.xplain;

import java.io.*;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.*;

public class Parser {

  public static Optional<String> parse(String input) throws IOException {
    Objects.requireNonNull(input);
    var className = getClassName(input);
    if(className != null){
      return Optional.of(input.replaceAll("\\b" + Pattern.quote(className) + "\\b", "Test"));
    }
    return Optional.empty();
  }

  private static String getClassName(String javaFile) throws IOException {
    Pattern pattern = Pattern.compile("\\bclass\\s+(\\w+)");
    Matcher matcher = pattern.matcher(javaFile);
    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return null;
    }
  }
}
