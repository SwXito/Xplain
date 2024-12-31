package fr.uge.xplain;

import fr.uge.database.DBService;
import fr.uge.model.LLMService;
import fr.uge.utilities.HistoryDTO;
import fr.uge.utilities.ResponseBoxer;
import fr.uge.utilities.SimpleBoxer;
import io.smallrye.mutiny.Multi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.util.List;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ControllerTest {

  @InjectMocks
  Controller controller; // Controller to be tested

  @Mock
  DBService dbService; // Mock DBService

  @Mock
  LLMService llmService; // Mock LLMService

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this); // Initialize mocks
  }

  @Test
  void testReceiveData() throws IOException, InterruptedException {
    var classText = "public class Test {}";
    var compilerResponse = "No errors.";
    var requestData = new SimpleBoxer("classData", classText);
    try (var mockedCompiler = Mockito.mockStatic(Compiler.class)) {
      mockedCompiler.when(() -> Compiler.compile(eq(classText))).thenReturn(compilerResponse);

      when(dbService.createXplainTable(eq(compilerResponse), eq(""), eq(classText), eq(true))).thenReturn(1L);

      var response = controller.receiveData(requestData);

      Thread.sleep(100);

      verify(dbService).createXplainTable(eq(compilerResponse), eq(""), eq(classText), eq(true));
      verify(llmService).newRequest(eq(classText), eq(compilerResponse), eq(1L));

      assert(response.getStatus() == 200);
      assert(response.getEntity() instanceof ResponseBoxer);
      var responseBoxer = (ResponseBoxer) response.getEntity();
      assert(responseBoxer.contentDescription().equals("generationStarted"));
    }
  }


  @Test
  void testStreamResponse() {
    var token1 = new SimpleBoxer("token", "Response token 1");
    var token2 = new SimpleBoxer("token", "Response token 2");

    var multi = Multi.createFrom().items(token1, token2);
    when(llmService.generateCompleteResponse()).thenReturn(multi);

    var responseStream = controller.streamResponse();

    responseStream.subscribe().with(item -> {
      assertEquals("token", item.contentDescription());
      assertTrue(item.content().equals("Response token 1") || item.content().equals("Response token 2"));
    });
  }

  @Test
  void testGetHistory() {
    when(dbService.getAllResponses()).thenReturn(List.of(
      new HistoryDTO("", "", "", "", "", true)));

    var response = controller.getHistory();

    verify(dbService).getAllResponses();
    assertEquals(200, response.getStatus());
    assert(response.getEntity() instanceof java.util.List);
    java.util.List<?> responses = (java.util.List<?>) response.getEntity();
    assert(!responses.isEmpty());
  }

  @Test
  void testReceiveModel() {
    var modelName = "light";

    var response = controller.receiveModel(modelName);

    verify(llmService).changeModel(eq(modelName));
    assertEquals(200, response.getStatus());
    assert(response.getEntity() instanceof SimpleBoxer);
    var simpleBoxer = (SimpleBoxer) response.getEntity();
    assertEquals("model", simpleBoxer.contentDescription());
    assertEquals(modelName, simpleBoxer.content());
  }
}
