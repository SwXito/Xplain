package fr.uge.xplain;

import fr.uge.database.XplainTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class XplainTableTest {

  @Test
  void testSetAndGetCompilerResponse() {
    var table = new XplainTable();
    var compilerResponse = "Compilation successful";

    table.setCompilerResponse(compilerResponse);
    assertEquals(compilerResponse, table.getCompilerResponse());
  }

  @Test
  void testSetCompilerResponseNull() {
    var table = new XplainTable();
    assertThrows(NullPointerException.class, () -> table.setCompilerResponse(null));
  }

  @Test
  void testSetAndGetLlmResponse() {
    var table = new XplainTable();
    var llmResponse = "Generated code snippet";

    table.setLlmResponse(llmResponse);
    assertEquals(llmResponse, table.getLlmResponse());
  }

  @Test
  void testSetLlmResponseNull() {
    var table = new XplainTable();
    assertThrows(NullPointerException.class, () -> table.setLlmResponse(null));
  }

  @Test
  void testSetAndGetClassText() {
    var table = new XplainTable();
    var classText = "public class Test { }";

    table.setClassText(classText);
    assertEquals(classText, table.getClassText());
  }

  @Test
  void testSetClassTextNull() {
    var table = new XplainTable();
    assertThrows(NullPointerException.class, () -> table.setClassText(null));
  }

  @Test
  void testSetAndGetHistory() {
    var table = new XplainTable();
    var history = "User action history";

    table.setHistory(history);
    assertEquals(history, table.getHistory());
  }

  @Test
  void testSetHistoryNull() {
    var table = new XplainTable();
    assertThrows(NullPointerException.class, () -> table.setHistory(null));
  }

  @Test
  void testSetAndGetSuccess() {
    var table = new XplainTable();
    table.setSuccess(true);
    assertTrue(table.getSuccess());

    table.setSuccess(false);
    assertFalse(table.getSuccess());
  }

  @Test
  void testGetTimestamp() {
    var table = new XplainTable();
    assertNotNull(table.getTimestamp());
  }

  @Test
  void testGetIdDefault() {
    var table = new XplainTable();
    assertEquals(0, table.getId());
  }
}
