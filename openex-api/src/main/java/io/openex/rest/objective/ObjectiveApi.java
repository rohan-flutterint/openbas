package io.openex.rest.objective;

import io.openex.database.model.Exercise;
import io.openex.database.model.Objective;
import io.openex.database.repository.ExerciseRepository;
import io.openex.database.repository.ObjectiveRepository;
import io.openex.database.specification.ObjectiveSpecification;
import io.openex.rest.helper.RestBehavior;
import io.openex.rest.objective.form.ObjectiveCreateInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class ObjectiveApi extends RestBehavior {

    private ExerciseRepository exerciseRepository;
    private ObjectiveRepository objectiveRepository;

    @Autowired
    public void setExerciseRepository(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Autowired
    public void setObjectiveRepository(ObjectiveRepository objectiveRepository) {
        this.objectiveRepository = objectiveRepository;
    }

    @GetMapping("/api/exercises/{exerciseId}/objectives")
    public Iterable<Objective> getMainObjectives(@PathVariable String exerciseId) {
        return objectiveRepository.findAll(ObjectiveSpecification.fromExercise(exerciseId));
    }

    @PostMapping("/api/exercises/{exerciseId}/objectives")
    @PostAuthorize("isExercisePlanner(#exerciseId)")
    public Objective createObjective(@PathVariable String exerciseId,
                                     @Valid @RequestBody ObjectiveCreateInput input) {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow();
        Objective objective = new Objective();
        objective.setUpdateAttributes(input);
        objective.setExercise(exercise);
        return objectiveRepository.save(objective);
    }

    @DeleteMapping("/api/exercises/{exerciseId}/objectives/{objectiveId}")
    @PostAuthorize("isExercisePlanner(#exerciseId)")
    public void deleteObjective(@PathVariable String objectiveId) {
        objectiveRepository.deleteById(objectiveId);
    }
}