package fr.uge.xplain;

import javax.tools.ToolProvider;
import java.io.*;
import java.util.Objects;

public class Compiler {

  public static String compile(String input) throws IOException {
    Objects.requireNonNull(input);
    String output = "";
    var fileName = "Test";
    var file = new File(fileName + ".java");
    if(!file.createNewFile()){
      throw new IOException("File already exists: " + file.getPath());
    }
    try(var writer = new BufferedWriter(new FileWriter(file))) {
      writer.write(Parser.parse(input).orElse(input));
    }
    var compiler = ToolProvider.getSystemJavaCompiler();
    var fileManager = compiler.getStandardFileManager(null, null, null);
    var compilationUnits = fileManager.getJavaFileObjects(file);
    var outputWriter = new StringWriter();
    var errorWriter = new StringWriter();
    var success = compiler.getTask(new PrintWriter(outputWriter), fileManager, diagnostic -> {
      errorWriter.write(diagnostic.toString() + "\n");}, null, null, compilationUnits).call();
    output = constructCompilerMessage(outputWriter.toString(), errorWriter.toString(), success);
    var classFile = new File(fileName + ".class");
    //Clean up
    deleteFiles(file, classFile);
    fileManager.close();
    return output;
  }


  private static void deleteFiles(File ...files){
    for(var file: files){
      if(file.exists()){
        file.delete();
      }
    }
  }

  private static String constructCompilerMessage(String output, String error, boolean success){
    if(success){
      return "Compilation successful:\n" + output;
    }
    return "Compilation failed:\n" + error + output;
  }

}
