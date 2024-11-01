package io.openbas.rest.exercise;

import static io.openbas.rest.exercise.ExerciseApi.EXERCISE_URI;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.openbas.database.model.Exercise;
import io.openbas.database.model.ExerciseTeamUser;
import io.openbas.database.model.Team;
import io.openbas.database.model.User;
import io.openbas.database.repository.ExerciseRepository;
import io.openbas.database.repository.ExerciseTeamUserRepository;
import io.openbas.database.repository.TeamRepository;
import io.openbas.database.repository.UserRepository;
import io.openbas.utils.fixtures.ExerciseFixture;
import io.openbas.utils.fixtures.TeamFixture;
import io.openbas.utils.fixtures.UserFixture;
import io.openbas.utils.mockUser.WithMockAdminUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
public class ExerciseApiTest {
  @Autowired private MockMvc mvc;

  @Autowired private ExerciseRepository exerciseRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private TeamRepository teamRepository;

  @Autowired private ExerciseTeamUserRepository exerciseTeamUserRepository;

  private static final List<String> EXERCISE_IDS = new ArrayList<>();
  private static final List<String> USER_IDS = new ArrayList<>();
  private static final List<String> TEAM_IDS = new ArrayList<>();

  @AfterAll
  void afterAll() {
    this.exerciseRepository.deleteAllById(EXERCISE_IDS);
    this.userRepository.deleteAllById(USER_IDS);
    this.teamRepository.deleteAllById(TEAM_IDS);
  }

  @Nested
  @DisplayName("Retrieving exercise informations")
  class RetrievingExercises {
    @Test
    @DisplayName("Retrieving players by exercise")
    @WithMockAdminUser
    void retrievingPlayersByExercise() throws Exception {
      // -- PREPARE --
      User userTom = userRepository.save(UserFixture.getUser("Tom", "TEST", "tom-test@fake.email"));
      User userBen = userRepository.save(UserFixture.getUser("Ben", "TEST", "ben-test@fake.email"));
      USER_IDS.addAll(Arrays.asList(userTom.getId(), userBen.getId()));
      Team teamA = teamRepository.save(TeamFixture.getTeam(userTom, "TeamA", false));
      Team teamB = teamRepository.save(TeamFixture.getTeam(userBen, "TeamB", false));
      TEAM_IDS.addAll(Arrays.asList(teamA.getId(), teamB.getId()));

      Exercise exercise = ExerciseFixture.createDefaultCrisisExercise();
      exercise.setTeams(Arrays.asList(teamA, teamB));
      Exercise exerciseSaved = exerciseRepository.save(exercise);
      EXERCISE_IDS.add(exerciseSaved.getId());

      ExerciseTeamUser exerciseTeamUser = new ExerciseTeamUser();
      exerciseTeamUser.setExercise(exerciseSaved);
      exerciseTeamUser.setTeam(teamA);
      exerciseTeamUser.setUser(userTom);
      ExerciseTeamUser exerciseTeamUser2 = new ExerciseTeamUser();
      exerciseTeamUser2.setExercise(exerciseSaved);
      exerciseTeamUser2.setTeam(teamB);
      exerciseTeamUser2.setUser(userBen);
      exerciseTeamUserRepository.saveAll(Arrays.asList(exerciseTeamUser, exerciseTeamUser2));

      mvc.perform(
              get(EXERCISE_URI + "/" + exerciseSaved.getId() + "/players")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().is2xxSuccessful())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(
              jsonPath("$[*].user_id")
                  .value(
                      org.hamcrest.Matchers.containsInAnyOrder(userTom.getId(), userBen.getId())));
    }
  }
}
