package fr.uge.xplain;

import fr.uge.database.XplainTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class XplainTableTest {

  @Test
  void testSetAndGetCompilerResponse() {
    XplainTable table = new XplainTable();
    String compilerResponse = "Compilation successful";

    table.setCompilerResponse(compilerResponse);
    assertEquals(compilerResponse, table.getCompilerResponse());
  }

  @Test
  void testSetCompilerResponseNull() {
    XplainTable table = new XplainTable();
    assertThrows(NullPointerException.class, () -> table.setCompilerResponse(null));
  }

  @Test
  void testSetAndGetLlmResponse() {
    XplainTable table = new XplainTable();
    String llmResponse = "Generated code snippet";

    table.setLlmResponse(llmResponse);
    assertEquals(llmResponse, table.getLlmResponse());
  }

  @Test
  void testSetLlmResponseNull() {
    XplainTable table = new XplainTable();
    assertThrows(NullPointerException.class, () -> table.setLlmResponse(null));
  }

  @Test
  void testSetAndGetClassText() {
    XplainTable table = new XplainTable();
    String classText = "public class Test { }";

    table.setClassText(classText);
    assertEquals(classText, table.getClassText());
  }

  @Test
  void testSetClassTextNull() {
    XplainTable table = new XplainTable();
    assertThrows(NullPointerException.class, () -> table.setClassText(null));
  }

  @Test
  void testSetAndGetHistory() {
    XplainTable table = new XplainTable();
    String history = "User action history";

    table.setHistory(history);
    assertEquals(history, table.getHistory());
  }

  @Test
  void testSetHistoryNull() {
    XplainTable table = new XplainTable();
    assertThrows(NullPointerException.class, () -> table.setHistory(null));
  }

  @Test
  void testSetAndGetSuccess() {
    XplainTable table = new XplainTable();
    table.setSuccess(true);
    assertTrue(table.getSuccess());

    table.setSuccess(false);
    assertFalse(table.getSuccess());
  }

  @Test
  void testGetTimestamp() {
    XplainTable table = new XplainTable();
    assertNotNull(table.getTimestamp());
  }

  @Test
  void testGetIdDefault() {
    XplainTable table = new XplainTable();
    assertEquals(0, table.getId());
  }
}
