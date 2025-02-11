package fr.uge.xplain;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * This class provides a utility to compile Java source code dynamically.
 * It uses the Java Compiler API to compile a given source code string and returns the compilation result.
 */
public final class Compiler {

  /**
   * A simple JavaFileObject implementation to represent a source code string.
   * This is used to provide in-memory source code to the Java Compiler API.
   */
  private static class AnonymousSource extends SimpleJavaFileObject {
    private final String code;

    /**
     * Constructor to initialize the source code.
     *
     * @param code The Java source code to be compiled.
     */
    private AnonymousSource(String code) {
      super(URI.create("string:///AnonymousSource.java"), Kind.SOURCE);
      this.code = code;
    }

    /**
     * Returns the source code as a CharSequence.
     *
     * @param ignoreEncodingErrors This parameter is ignored.
     * @return The source code as a CharSequence.
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return code;
    }
  }

  /**
   * A custom file manager to handle the compilation output. This file manager ignores the `.class` file generation
   * and prevents the actual class files from being created on disk.
   */
  private static final class EmptyFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    /**
     * Constructor to initialize the file manager.
     *
     * @param fileManager The delegate JavaFileManager.
     */
    private EmptyFileManager(JavaFileManager fileManager) {
      super(fileManager);
    }

    /**
     * Returns a dummy JavaFileObject for output that does not write to disk.
     * The OutputStream for the generated class files does nothing.
     *
     * @param location The location of the output.
     * @param className The class name.
     * @param kind The kind of the file (in this case, `.class`).
     * @param sibling A sibling file object, which we don't use here.
     * @return A JavaFileObject that doesn't write any output.
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
      return new SimpleJavaFileObject(URI.create("string:///" + className + kind.extension), kind) {
        @Override
        public OutputStream openOutputStream() {
          // Return an OutputStream that does nothing in order to ignore .class files
          return new OutputStream() {
            @Override
            public void write(int b) {
              // No-op to ignore writing the compiled class file
            }
          };
        }
      };
    }
  }

  /**
   * Compiles the given Java source code string and returns the result.
   * The method uses the Java Compiler API to compile the code in memory and provides
   * the compilation result as a string, indicating success or failure.
   *
   * @param input The Java source code to be compiled.
   * @return A string representing the compilation result (success or failure).
   * @throws IOException If an error occurs during compilation.
   */
  public static String compile(String input) throws IOException {
    Objects.requireNonNull(input, "Input code must not be null");

    var compiler = ToolProvider.getSystemJavaCompiler();
    var outputWriter = new StringWriter();
    var errorWriter = new StringWriter();

    // Remove any extra whitespace between the access modifier and the class keyword
    var pattern = Pattern.compile("(?m)^\\s*public\\s+(class|interface|enum|record)\\s+");
    var matcher = pattern.matcher(input);
    if (matcher.find()) {
      input = matcher.replaceFirst(matcher.group(1) + " ");
    }

    var standardFileManager = compiler.getStandardFileManager(null, null, null);

    // Use EmptyFileManager to avoid creating class files on disk
    try (var emptyFileManager = new EmptyFileManager(standardFileManager)) {
      var sourceObject = new AnonymousSource(input);

      // Perform the compilation task
      var success = compiler.getTask(new PrintWriter(outputWriter), emptyFileManager, diagnostic -> {
        errorWriter.write(diagnostic.toString() + "\n");
      }, null, null, List.of(sourceObject)).call();

      // Return the result based on whether the compilation succeeded or failed
      return constructCompilerMessage(outputWriter.toString(), errorWriter.toString(), success);
    }
  }

  /**
   * Constructs a compiler message indicating success or failure based on the compilation outcome.
   *
   * @param output The output of the compiler (if successful).
   * @param error The error messages generated by the compiler (if failed).
   * @param success A boolean indicating whether the compilation was successful.
   * @return A string containing the compiler result message.
   */
  private static String constructCompilerMessage(String output, String error, boolean success) {
    if (success) {
      return "Compilation successful\n" + output;
    }
    return "Compilation failed\n" + error.replace("/AnonymousSource.java:", "") + output;
  }

}
