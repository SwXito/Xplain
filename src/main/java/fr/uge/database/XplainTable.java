package fr.uge.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import java.util.Objects;

@Entity
public class XplainTable {

    @Id
    @GeneratedValue
    private Long id;
    @Lob
    private String llmResponse;
    private String compilerResponse;
    private String classText;

    public void setCompilerResponse(String compilerResponse) {
        Objects.requireNonNull(compilerResponse);
        this.compilerResponse = compilerResponse;
    }

    public void setLlmResponse(String llmResponse) {
        Objects.requireNonNull(llmResponse);
        this.llmResponse = llmResponse;
    }

    public void setClassText(String classText) {
        Objects.requireNonNull(classText);
        this.classText = classText;
    }

    @Override
    public String toString(){
        return "[CompilerResponse:"
                + "\nid = " + id
                + "\ncompilerResponse = " + compilerResponse
                + "\nllmResponse = " + llmResponse
                + "\nclassText = " + classText
                + "]";
    }
}
