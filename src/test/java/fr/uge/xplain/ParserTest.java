package fr.uge.xplain;

import fr.uge.utilities.Parser;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {

  @Test
  void withValidClass() {
    String javaCode = "public class MyClass { }";
    Optional<String> className = Parser.getClassName(javaCode);
    assertEquals(Optional.of("MyClass"), className);
  }

  @Test
  void withInterface() {
    String javaCode = "public interface MyInterface { }";
    Optional<String> className = Parser.getClassName(javaCode);
    assertEquals(Optional.of("MyInterface"), className);
  }

  @Test
  void withEnum() {
    String javaCode = "public enum MyEnum { VALUE1, VALUE2 }";
    Optional<String> className = Parser.getClassName(javaCode);
    assertEquals(Optional.of("MyEnum"), className);
  }

  @Test
  void withRecord() {
    String javaCode = "public record MyRecord(String name, int age) { }";
    Optional<String> className = Parser.getClassName(javaCode);
    assertEquals(Optional.of("MyRecord"), className);
  }

  @Test
  void withClassAndInterface() {
    String javaCode = """
          public class OuterClass { }
          public interface MyInterface { }
      """;
    Optional<String> className = Parser.getClassName(javaCode);
    assertEquals(Optional.of("OuterClass"), className, "Should return the first encountered declaration.");
  }

  @Test
  void withEnumAndClass() {
    String javaCode = """
          public enum MyEnum { VALUE1, VALUE2 }
          public class MyClass { }
      """;
    Optional<String> className = Parser.getClassName(javaCode);
    assertEquals(Optional.of("MyEnum"), className, "Should return the first encountered declaration.");
  }

  @Test
  void withRecordAndEnum() {
    String javaCode = """
          public record MyRecord(String name, int age) { }
          public enum MyEnum { VALUE1, VALUE2 }
      """;
    Optional<String> className = Parser.getClassName(javaCode);
    assertEquals(Optional.of("MyRecord"), className, "Should return the first encountered declaration.");
  }

  @Test
  void withClassKeywordInComment() {
    String javaCode = """
          // This is a class comment
          /* class CommentedClass { } */
          public class ValidClass { }
      """;
    Optional<String> className = Parser.getClassName(javaCode);
    assertEquals(Optional.of("ValidClass"), className, "Should ignore class keyword in comments.");
  }

  @Test
  void withEmptyString() {
    String javaCode = "";
    Optional<String> className = Parser.getClassName(javaCode);
    assertEquals(Optional.empty(), className, "Should return empty when no class, interface, enum, or record is defined.");
  }
}
