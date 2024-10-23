package fr.uge.xplain;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

  // Test 1: Basic class replacement with no methods or fields
  @Test
  public void correctReplaceBasicClass() throws IOException {
    assertEquals("public class Test {}", Parser.parse("public class Animal {}").get());
  }

  // Test 2: Correct replacement for a class with methods and fields
  @Test
  public void correctReplaceWithMethodsAndFields() throws IOException {
    assertEquals(
      "public class Test {\n" +
        "  private final String name;\n" +
        "  public Test(String name) {\n" +
        "    this.name = name;\n" +
        "  }\n" +
        "}\n",
      Parser.parse(
        "public class Animal {\n" +
          "  private final String name;\n" +
          "  public Animal(String name) {\n" +
          "    this.name = name;\n" +
          "  }\n" +
          "}\n").get());
  }

  // Test 3: Class with inner classes
  @Test
  public void correctReplaceWithInnerClass() throws IOException {
    assertEquals(
      "public class Test {\n" +
      "  class InnerAnimal {\n" +
      "    private final String name;\n" +
      "  }\n" +
      "}\n",
      Parser.parse(
        "public class Animal {\n" +
          "  class InnerAnimal {\n" +
          "    private final String name;\n" +
          "  }\n" +
          "}\n").get());
  }

  // Test 4: No class declaration in the input (returns empty)
  @Test
  public void noClassDeclaration() throws IOException {
    assertEquals(Optional.empty(), Parser.parse("public interface Animal {}"));
  }

  // Test 5: Multiple class declarations (only replaces the first class name)
  @Test
  public void multipleClassDeclarations() throws IOException {
    assertEquals("public class Test {}\n" +
        "public class Plant {}",
      Parser.parse(
        "public class Animal {}\n" +
          "public class Plant {}").get());
  }

  // Test 6: Invalid input (empty string)
  @Test
  public void emptyInput() throws IOException {
    assertEquals("", Parser.parse("").orElse(""));
  }

  // Test 7: Class with a generic type
  @Test
  public void classWithGenericType() throws IOException {
    assertEquals(
      "public class Test<T> {\n" +
        "  private T type;\n" +
        "  public Test(T type) {\n" +
        "    this.type = type;\n" +
        "  }\n" +
        "}\n",
      Parser.parse(
        "public class Animal<T> {\n" +
          "  private T type;\n" +
          "  public Animal(T type) {\n" +
          "    this.type = type;\n" +
          "  }\n" +
          "}\n").get());
  }

  // Test 8: Class with comments (ensure comments changed)
  @Test
  public void classWithComments() throws IOException {
    assertEquals(
      "/* Test class */\n" +
        "public class Test {\n" +
        "  // Some fields and methods\n" +
        "  private final String name;\n" +
        "}\n",
      Parser.parse(
        "/* Animal class */\n" +
          "public class Animal {\n" +
          "  // Some fields and methods\n" +
          "  private final String name;\n" +
          "}\n").get());
  }

  // Test 9: Class name with numbers
  @Test
  public void classWithNumberInName() throws IOException {
    assertEquals(
      "public class Test {\n" +
      "  private final String name;\n" +
      "}\n",
      Parser.parse(
        "public class Animal123 {\n" +
          "  private final String name;\n" +
          "}\n").get());
  }

  // Test 10: Class keyword in a string (should not replace within strings)
  @Test
  public void classKeywordInString() throws IOException {
    assertEquals(
      "public class Test {\n" +
        "  String description = \"This is a class example.\";\n" +
        "}\n",
      Parser.parse(
        "public class Animal {\n" +
          "  String description = \"This is a class example.\";\n" +
          "}\n").get());
  }

  // Test 11: Null input should throw NullPointerException
  @Test
  public void nullInputShouldThrowException() {
    assertThrows(NullPointerException.class, () -> Parser.parse(null));
  }

}
