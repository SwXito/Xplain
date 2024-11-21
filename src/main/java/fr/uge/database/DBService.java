package fr.uge.database;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;

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
        em.persist(r);
    }

    @Transactional
    public List<XplainTable> getAllResponses() {
        return em.createQuery("SELECT r FROM XplainTable r", XplainTable.class).getResultList();
    }

}
