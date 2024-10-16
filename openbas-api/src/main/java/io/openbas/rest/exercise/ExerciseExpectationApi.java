package io.openbas.rest.exercise;

import io.openbas.database.model.InjectExpectation;
import io.openbas.rest.helper.RestBehavior;
import io.openbas.service.ExerciseExpectationService;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ExerciseExpectationApi extends RestBehavior {

  private final ExerciseExpectationService exerciseExpectationService;

  @GetMapping(value = "/api/exercises/{exerciseId}/expectations")
  @PreAuthorize("isExerciseObserver(#exerciseId)")
  public List<InjectExpectation> exerciseInjectExpectations(
      @PathVariable @NotBlank final String exerciseId) {
    return this.exerciseExpectationService.injectExpectations(exerciseId);
  }
}
