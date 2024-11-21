package fr.uge.xplain;

import fr.uge.utilities.JavaSourceFromString;
import fr.uge.utilities.Parser;

import javax.tools.ToolProvider;
import java.io.*;
import java.util.List;
import java.util.Objects;

public class Compiler {

  public static String compile(String input) throws IOException {
    Objects.requireNonNull(input);
    var fileName = Parser.getClassName(input).orElse("");
    if(fileName.isEmpty()){
      return "Compilation failed: No class name found in input";
    }
    var sourceObject = new JavaSourceFromString(fileName, input);
    var compiler = ToolProvider.getSystemJavaCompiler();
    var fileManager = compiler.getStandardFileManager(null, null, null);
    var compilationUnits = List.of(sourceObject);
    var outputWriter = new StringWriter();
    var errorWriter = new StringWriter();
    var success = compiler.getTask(new PrintWriter(outputWriter), fileManager, diagnostic -> {
      errorWriter.write(diagnostic.toString() + "\n");}, null, null, compilationUnits).call();
    fileManager.close();
    deleteUselessFiles(new File(fileName + ".class"));
    return constructCompilerMessage(outputWriter.toString(), errorWriter.toString(), success);
  }

  private static String constructCompilerMessage(String output, String error, boolean success){
    var res = "Compilation result:\n";
    if(success){
      return res + "Compilation successful\n" + output;
    }
    return  res + "Compilation failed\n" + error + output;
  }

  private static void deleteUselessFiles(File... files) {
    for (File file : files) {
      if(file.exists()){
        file.delete();
      }
    }
  }

}
