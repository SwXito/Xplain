package fr.uge.xplain;

import fr.uge.database.DBService;
import fr.uge.database.XplainTable;
import fr.uge.utilities.HistoryDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DBServiceTest {

  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private DBService dbService;

  @Mock
  private Query query;

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
    // Given
    String compilerResponse = "compiler response";
    String llmResponse = "llm response";
    String classText = "class text\nline 2\nline 3";
    boolean success = true;

    // When
    //doNothing().when(entityManager).persist(any(XplainTable.class)); // Simule persist() sans rien faire

    long id = dbService.createXplainTable(compilerResponse, llmResponse, classText, success);

    // Then
    assertNotNull(id, "The ID should not be null");
    //verify(entityManager, times(1)).persist(any(XplainTable.class));
  }

  @Test
  public void testGetAllResponses() {
    // Given
    List<XplainTable> mockResponses = Collections.singletonList(mockXplainTable);

    // Créez un mock de TypedQuery avec le type générique spécifié
    TypedQuery<XplainTable> mockQuery = mock(TypedQuery.class);

    // Configurez le mock pour retourner les résultats souhaités
    when(entityManager.createQuery(anyString(), eq(XplainTable.class))).thenReturn(mockQuery);
    when(mockQuery.getResultList()).thenReturn(mockResponses);

    // When
    List<HistoryDTO> responseList = dbService.getAllResponses();

    // Then
    assertNotNull(responseList, "The response list should not be null");
    assertEquals(1, responseList.size(), "There should be one response");
    HistoryDTO dto = responseList.getFirst();
    assertEquals("class text", dto.classText());  // Assurez-vous que la méthode est en camel case
    assertEquals("llm response", dto.llmResponse());

    // Vérifier que createQuery() a été appelé
    verify(entityManager).createQuery(anyString(), eq(XplainTable.class));
  }

  @Test
  public void testUpdateLlmResponse() {
    // Given
    long id = 1L;
    String token = "additional token";
    XplainTable row = mockXplainTable;

    when(entityManager.getReference(XplainTable.class, id)).thenReturn(row);

    // When
    dbService.updateLlmResponse(id, token);

    // Then
    assertEquals("llm responseadditional token", row.getLlmResponse(), "Llm response should be updated correctly");
    verify(entityManager, times(1)).getReference(XplainTable.class, id);
  }

  @Test
  public void testCreateXplainTableNullParams() {
    // Given
    String compilerResponse = null;
    String llmResponse = "llm response";
    String classText = "class text\nline 2\nline 3";
    boolean success = true;

    // Then
    assertThrows(NullPointerException.class, () -> dbService.createXplainTable(compilerResponse, llmResponse, classText, success));
  }

}