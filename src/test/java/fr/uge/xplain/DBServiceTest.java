package fr.uge.xplain;

import fr.uge.database.DBService;
import fr.uge.database.XplainTable;
import fr.uge.utilities.HistoryDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DBServiceTest {

  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private DBService dbService;

  private XplainTable mockXplainTable;

  @BeforeEach
  public void setUp() {
    mockXplainTable = new XplainTable();
    mockXplainTable.setCompilerResponse("compiler response");
    mockXplainTable.setLlmResponse("llm response");
    mockXplainTable.setClassText("class text");
    mockXplainTable.setHistory("class text");
    mockXplainTable.setSuccess(true);
  }

  @Test
  public void testCreateXplainTable() {
    var compilerResponse = "compiler response";
    var llmResponse = "llm response";
    var classText = "class text\nline 2\nline 3";
    boolean success = true;

    doNothing().when(entityManager).persist(any(XplainTable.class));

    dbService.createXplainTable(compilerResponse, llmResponse, classText, success);

    verify(entityManager, times(1)).persist(any(XplainTable.class));
  }

  @Test
  public void testUpdateLlmResponse() {
    long id = 1L;
    var token = "additional token";
    var row = mockXplainTable;

    when(entityManager.getReference(XplainTable.class, id)).thenReturn(row);

    dbService.updateLlmResponse(id, token);

    assertEquals("llm responseadditional token", row.getLlmResponse(), "Llm response should be updated correctly");
    verify(entityManager, times(1)).getReference(XplainTable.class, id);
  }

  @Test
  public void testCreateXplainTableNullParams() {
    String compilerResponse = null;
    var llmResponse = "llm response";
    var classText = "class text\nline 2\nline 3";
    boolean success = true;

    assertThrows(NullPointerException.class, () -> dbService.createXplainTable(compilerResponse, llmResponse, classText, success));
  }

}