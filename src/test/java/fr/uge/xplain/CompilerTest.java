package fr.uge.xplain;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CompilerTest {

  @Test
  public void testCompileValidCode() throws IOException {
    var validJavaCode = """
          public class Animal {
              private final String name;
              public Animal(String name) {
                  this.name = name;
              }
              public String getName() {
                  return name;
              }
          }
      """;

    var output = Compiler.compile(validJavaCode);
    assertTrue(output.contains("Compilation successful"));
  }

  @Test
  public void testCompileInvalidCode() throws IOException {
    var invalidJavaCode = """
          public class Animal {
              private final String name;
              public Animall(String name) { // Typo in constructor name
                  this.name = name;
              }
              public String getName() {
                  return name;
              }
          }
      """;

    var output = Compiler.compile(invalidJavaCode);
    assertTrue(output.contains("Compilation failed"));
  }

  @Test
  public void testCompileCodeWithParser() throws IOException {
    var javaCodeWithClass = """
          public class Animal123 {
              private final String name;
              public Animal123(String name) {
                  this.name = name;
              }
              public String getName() {
                  return name;
              }
          }
      """;

    var output = Compiler.compile(javaCodeWithClass);
    // Since the parser should rename the class to 'Test', check if the output is correct
    assertTrue(output.contains("Compilation successful"));
  }

  @Test
  public void testCompileEmptyCode() throws IOException {
    var emptyJavaCode = "";

    var output = Compiler.compile(emptyJavaCode);
    // Empty code should success compilation
    assertTrue(output.contains("Compilation successful"));
  }

  @Test
  public void testCompileWithSyntaxError() throws IOException {
    var codeWithSyntaxError = """
          public class Animal {
              private final String name
              public Animal(String name) { // Missing semicolon in the previous line
                  this.name = name;
              }
          }
      """;

    var output = Compiler.compile(codeWithSyntaxError);

    // Compilation should fail due to a syntax error
    assertTrue(output.contains("Compilation failed"));
    assertTrue(output.contains("';' expected"));
  }

}
