package fr.uge.xplain;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Compiler {

  private static class AnonymousSource extends SimpleJavaFileObject {
    private final String code;

    private AnonymousSource(String code) {
      super(URI.create("string:///AnonymousSource.java"), Kind.SOURCE);
      this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return code;
    }
  }

  private static final class EmptyFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private EmptyFileManager(JavaFileManager fileManager) {
      super(fileManager);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
      return new SimpleJavaFileObject(URI.create("string:///" + className + kind.extension), kind) {
        @Override
        public OutputStream openOutputStream() {
          // Return an OutputStream that does nothing in order to ignore .class files
          return new OutputStream() {
            @Override
            public void write(int b) {}
          };
        }
      };
    }
  }

  public static String compile(String input) throws IOException {
    Objects.requireNonNull(input);
    var compiler = ToolProvider.getSystemJavaCompiler();
    var outputWriter = new StringWriter();
    var errorWriter = new StringWriter();
    var pattern = Pattern.compile("(?m)^\\s*public\\s+(class|interface|enum|record)\\s+");
    var matcher = pattern.matcher(input);
    if (matcher.find()) {
      input = matcher.replaceFirst(matcher.group(1) + " ");
    }
    var standardFileManager = compiler.getStandardFileManager(null, null, null);
    try (var emptyFileManager = new EmptyFileManager(standardFileManager)) {
      var sourceObject = new AnonymousSource(input);
      var success = compiler.getTask(new PrintWriter(outputWriter), emptyFileManager, diagnostic -> {
        errorWriter.write(diagnostic.toString() + "\n");
      }, null, null, List.of(sourceObject)).call();
      return constructCompilerMessage(outputWriter.toString(), errorWriter.toString(), success);
    }
  }

  private static String constructCompilerMessage(String output, String error, boolean success) {
    if (success) {
      return "Compilation successful\n" + output;
    }
    return "Compilation failed\n" + error + output;
  }

}
