package fr.uge.utilities;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class JavaSourceFromString extends SimpleJavaFileObject {

  final String code;

  public JavaSourceFromString(String name, String code) {
    super(URI.create("memory:///" + name.replace('.','/') + Kind.SOURCE.extension),
        Kind.SOURCE);
    this.code = code;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return code;
  }
}