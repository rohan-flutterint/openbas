package io.openex.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.openex.database.model.Challenge;
import io.openex.database.model.Inject;
import io.openex.database.model.Scenario;
import io.openex.database.repository.ChallengeRepository;
import io.openex.database.repository.InjectRepository;
import io.openex.database.repository.ScenarioRepository;
import io.openex.injects.challenge.model.ChallengeContent;
import io.openex.rest.utils.WithMockObserverUser;
import io.openex.service.ScenarioService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static io.openex.injects.challenge.ChallengeContract.CHALLENGE_PUBLISH;
import static io.openex.injects.challenge.ChallengeContract.TYPE;
import static io.openex.rest.scenario.ScenarioApi.SCENARIO_URI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(PER_CLASS)
public class ChallengeApiTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ScenarioRepository scenarioRepository;
  @Autowired
  private ScenarioService scenarioService;
  @Autowired
  private InjectRepository injectRepository;
  @Autowired
  private ChallengeRepository challengeRepository;
  @Resource
  private ObjectMapper objectMapper;

  static String SCENARIO_ID;
  static String CHALLENGE_ID;
  static String INJECT_ID;

  @AfterAll
  void afterAll() {
    this.scenarioRepository.deleteById(SCENARIO_ID);
    this.challengeRepository.deleteById(CHALLENGE_ID);
    this.injectRepository.deleteById(INJECT_ID);
  }

  // -- SCENARIOS --

  @DisplayName("Retrieve challenges for scenario")
  @Test
  @Order(1)
  @WithMockObserverUser
  void retrieveChallengesVariableForScenarioTest() throws Exception {
    // -- PREPARE --
    Scenario scenario = new Scenario();
    scenario.setName("Scenario name");
    Scenario scenarioCreated = this.scenarioService.createScenario(scenario);
    SCENARIO_ID = scenarioCreated.getId();

    Challenge challenge = new Challenge();
    String challengeName = "My challenge";
    challenge.setName(challengeName);
    challenge = this.challengeRepository.save(challenge);
    CHALLENGE_ID = challenge.getId();
    ChallengeContent content = new ChallengeContent();
    content.setChallenges(List.of(challenge.getId()));
    Inject inject = new Inject();
    inject.setTitle("Test inject");
    inject.setType(TYPE);
    inject.setContract(CHALLENGE_PUBLISH);
    inject.setContent(this.objectMapper.valueToTree(content));
    inject.setDependsDuration(0L);
    inject.setScenario(scenario);
    inject = this.injectRepository.save(inject);
    INJECT_ID = inject.getId();

    // -- EXECUTE --
    String response = this.mvc
        .perform(get(SCENARIO_URI + "/" + SCENARIO_ID + "/challenges")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andReturn()
        .getResponse()
        .getContentAsString();

    // -- ASSERT --
    assertNotNull(response);
    assertEquals(challengeName, JsonPath.read(response, "$[0].challenge_name"));
  }

}