package fr.uge.xplain;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompilerTest {

  @Test
  public void testCompileValidCode() throws IOException {
    String validJavaCode = """
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

    String output = Compiler.compile(validJavaCode);
    System.out.println(output);

    assertTrue(output.contains("Compilation successful"));
  }

  @Test
  public void testCompileInvalidCode() throws IOException {
    String invalidJavaCode = """
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

    String output = Compiler.compile(invalidJavaCode);
    System.out.println(output);

    assertTrue(output.contains("Compilation failed"));
  }

  @Test
  public void testCompileCodeWithParser() throws IOException {
    String javaCodeWithClass = """
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

    String output = Compiler.compile(javaCodeWithClass);
    System.out.println(output);

    // Since the parser should rename the class to 'Test', check if the output is correct
    assertTrue(output.contains("Compilation successful"));
  }

  @Test
  public void testCompileEmptyCode() throws IOException {
    String emptyJavaCode = "";

    String output = Compiler.compile(emptyJavaCode);
    System.out.println(output);

    // Empty code should success compilation
    assertTrue(output.contains("Compilation successful"));
  }

  @Test
  public void testCompileWithSyntaxError() throws IOException {
    String codeWithSyntaxError = """
            public class Animal {
                private final String name
                public Animal(String name) { // Missing semicolon in the previous line
                    this.name = name;
                }
            }
        """;

    String output = Compiler.compile(codeWithSyntaxError);
    System.out.println(output);

    // Compilation should fail due to a syntax error
    assertTrue(output.contains("Compilation failed"));
    assertTrue(output.contains("';' expected"));
  }

}
