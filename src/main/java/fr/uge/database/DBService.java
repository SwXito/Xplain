package fr.uge.database;

import fr.uge.utilities.HistoryDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class DBService {

  @Inject
  EntityManager em;

  @Transactional
  public void createXplainTable(String compilerResponse, String LlmResponse, String classText) {
    Objects.requireNonNull(compilerResponse);
    Objects.requireNonNull(LlmResponse);
    Objects.requireNonNull(classText);
    var r = new XplainTable();
    r.setCompilerResponse(compilerResponse);
    r.setLlmResponse(LlmResponse);
    r.setClassText(classText);
    r.setHistory(getFirstThreeLines(classText));
    em.persist(r);
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
        response.getCompilerResponse(), response.getHistory(), response.getTimestamp());
      dtoList.add(dto);
    }
    return dtoList;
  }

}
