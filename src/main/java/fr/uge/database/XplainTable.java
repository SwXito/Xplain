package fr.uge.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity class representing a record in the XplainTable.
 * Stores information related to compiler and language model responses, along with metadata.
 */
@Entity
public class XplainTable {

  @Id
  @GeneratedValue
  private long id; // Unique identifier for each record
  @Lob
  private String llmResponse; // Response from the language model
  private String compilerResponse; // Response from the compiler
  @Lob
  private String classText; // Text of the class being processed
  private String history; // Shortened history derived from the class text
  private String timestamp = LocalDateTime.now().toString(); // Timestamp of when the record was created
  private boolean success; // Indicates if the compilation was successful

  /**
   * Sets the compiler response.
   *
   * @param compilerResponse the response from the compiler.
   * @throws NullPointerException if the compilerResponse is null.
   */
  public void setCompilerResponse(String compilerResponse) {
    Objects.requireNonNull(compilerResponse);
    this.compilerResponse = compilerResponse;
  }

  /**
   * Sets the language model response.
   *
   * @param llmResponse the response from the language model.
   * @throws NullPointerException if the llmResponse is null.
   */
  public void setLlmResponse(String llmResponse) {
    Objects.requireNonNull(llmResponse);
    this.llmResponse = llmResponse;
  }

  /**
   * Sets the class text.
   *
   * @param classText the text of the class being processed.
   * @throws NullPointerException if the classText is null.
   */
  public void setClassText(String classText) {
    Objects.requireNonNull(classText);
    this.classText = classText;
  }

  /**
   * Sets the history for the record.
   *
   * @param history a brief representation of the class text.
   * @throws NullPointerException if the history is null.
   */
  public void setHistory(String history) {
    Objects.requireNonNull(history);
    this.history = history;
  }

  /**
   * Sets whether the compilation was successful.
   *
   * @param success true if the operation was successful, false otherwise.
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * Gets the unique identifier for this record.
   *
   * @return the ID of the record.
   */
  public long getId() {
    return id;
  }

  /**
   * Gets the compiler response.
   *
   * @return the compiler response.
   */
  public String getCompilerResponse() {
    return compilerResponse;
  }

  /**
   * Gets the language model response.
   *
   * @return the language model response.
   */
  public String getLlmResponse() {
    return llmResponse;
  }

  /**
   * Gets the class text.
   *
   * @return the text of the class being processed.
   */
  public String getClassText() {
    return classText;
  }

  /**
   * Gets the history.
   *
   * @return a brief representation of the class text.
   */
  public String getHistory() {
    return history;
  }

  /**
   * Gets the timestamp of when the record was created.
   *
   * @return the creation timestamp.
   */
  public String getTimestamp() {
    return timestamp;
  }

  /**
   * Indicates whether the compilation was successful.
   *
   * @return true if the compilation was successful, false otherwise.
   */
  public boolean getSuccess() {
    return success;
  }
}
