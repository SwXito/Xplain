package fr.uge.database;

import fr.uge.utilities.HistoryDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.*;

/**
 * Database service for managing operations on the XplainTable.
 * Provides methods to create, retrieve, and update records in the database.
 */
@ApplicationScoped
public final class DBService {

  @Inject
  EntityManager em;

  /**
   * Creates a new record in the XplainTable with the given data.
   *
   * @param compilerResponse the response from the compiler.
   * @param LlmResponse the response from the language model.
   * @param classText the text of the class being processed.
   * @param success indicates whether the compilation was successful.
   * @return the ID of the newly created record.
   * @throws NullPointerException if any required parameter is null.
   */
  @Transactional
  public long createXplainTable(String compilerResponse, String LlmResponse, String classText, boolean success) {
    Objects.requireNonNull(compilerResponse);
    Objects.requireNonNull(LlmResponse);
    Objects.requireNonNull(classText);
    var table = new XplainTable();
    table.setCompilerResponse(compilerResponse);
    table.setLlmResponse(LlmResponse);
    table.setClassText(classText);
    table.setHistory(getFirstThreeLines(classText));
    table.setSuccess(success);
    em.persist(table);
    return table.getId();
  }

  /**
   * Extracts the first three lines from the given class text.
   *
   * @param classText the text of the class.
   * @return a string containing the first three lines of the class text.
   */
  private static String getFirstThreeLines(String classText) {
    var sj = new StringJoiner("\n", "", "");
    var lines = classText.split("\n");
    for (int i = 0; i < lines.length && i < 3; i++) {
      sj.add(lines[i]);
    }
    return sj.toString();
  }

  /**
   * Retrieves all responses from the XplainTable and maps them to DTO objects.
   *
   * @return a list of {@link HistoryDTO} objects representing the records in the table.
   */
  @Transactional
  public List<HistoryDTO> getAllResponses() {
    var responses = em.createQuery("SELECT r FROM XplainTable r", XplainTable.class).getResultList();
    var dtoList = new ArrayList<HistoryDTO>();
    for (XplainTable response : responses) {
      var dto = new HistoryDTO(
        response.getClassText(),
        response.getLlmResponse(),
        response.getCompilerResponse(),
        response.getHistory(),
        response.getTimestamp(),
        response.getSuccess()
      );
      dtoList.add(dto);
    }
    return dtoList;
  }

  /**
   * Updates the LLM response for a specific record by appending the given token.
   *
   * @param id the ID of the record to update.
   * @param token the text to append to the existing LLM response.
   */
  @Transactional
  public void updateLlmResponse(long id, String token) { // remove
    var row = em.getReference(XplainTable.class, id);
    var llmResponse = row.getLlmResponse() + token; // append token to the current LLM response
    row.setLlmResponse(llmResponse);
  }
}
