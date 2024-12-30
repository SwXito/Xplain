package fr.uge.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class XplainTable {

  @Id
  @GeneratedValue
  private Long id;
  @Lob
  private String llmResponse;
  private String compilerResponse;
  @Lob
  private String classText;
  private String history;
  private String timestamp = LocalDateTime.now().toString();
  private boolean success;

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

  public void setHistory(String history) {
    Objects.requireNonNull(history);
    this.history = history;
  }

  public long getId() { return id; }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getCompilerResponse() {
    return compilerResponse;
  }

  public String getLlmResponse() {
    return llmResponse;
  }

  public String getClassText() {
    return classText;
  }

  public String getHistory(){
    return history;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public boolean getSuccess() {
    return success;
  }

}
