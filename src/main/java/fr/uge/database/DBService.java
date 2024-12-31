package fr.uge.database;

import fr.uge.utilities.HistoryDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.*;

@ApplicationScoped
public final class DBService {

  @Inject
  EntityManager em;

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

  private static String getFirstThreeLines(String classText) {
    var sj = new StringJoiner("\n", "", "");
    var lines = classText.split("\n");
    for (int i = 0; i < lines.length && i < 3; i++) {
      sj.add(lines[i]);
    }
    return sj.toString();
  }

  @Transactional
  public List<HistoryDTO> getAllResponses() {
    var responses = em.createQuery("SELECT r FROM XplainTable r", XplainTable.class).getResultList();
    var dtoList = new ArrayList<HistoryDTO>();
    for (XplainTable response : responses) {
      var dto = new HistoryDTO(response.getClassText(), response.getLlmResponse(),
        response.getCompilerResponse(), response.getHistory(), response.getTimestamp(), response.getSuccess());
      dtoList.add(dto);
    }
    return dtoList;
  }

  @Transactional
  public void updateLlmResponse(long id, String token) { // supprimer
    var row = em.getReference(XplainTable.class, id);
    var llmResponse = row.getLlmResponse() + token; // ajouter update llmresponse
    row.setLlmResponse(llmResponse);
  }

}
