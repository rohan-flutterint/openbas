package io.openex.database.specification;

import io.openex.database.model.Exercise;
import io.openex.database.model.Inject;
import io.openex.injects.manual.ManualContract;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;

public class InjectSpecification {

    public static <T> Specification<Inject<T>> fromExercise(String exerciseId) {
        return (root, query, cb) -> cb.equal(root.get("exercise").get("id"), exerciseId);
    }

    public static <T> Specification<Inject<T>> next() {
        return (root, query, cb) -> {
            Path<Object> exercisePath = root.get("exercise");
            return cb.and(
                    cb.equal(root.get("enabled"), true), // isEnable
                    cb.isNotNull(exercisePath.get("start")), // fromScheduled
                    cb.isNull(root.join("status", JoinType.LEFT).get("name")) // notExecuted
            );
        };
    }

    public static <T> Specification<Inject<T>> executable() {
        return (root, query, cb) -> {
            Path<Object> exercisePath = root.get("exercise");
            return cb.and(
                    cb.notEqual(root.get("type"), ManualContract.NAME),  // notManual
                    cb.equal(root.get("enabled"), true), // isEnable
                    cb.isNotNull(exercisePath.get("start")), // fromScheduled
                    cb.equal(exercisePath.get("status"), Exercise.STATUS.RUNNING), // fromRunningExercise
                    cb.isNull(root.join("status", JoinType.LEFT).get("name")) // notExecuted
            );
        };
    }

    public static <T> Specification<Inject<T>> forDryrun(String exerciseId) {
        return (root, query, cb) -> cb.and(
                cb.notEqual(root.get("type"), ManualContract.NAME),  // notManual
                cb.equal(root.get("enabled"), true), // isEnable
                cb.equal(root.get("exercise").get("id"), exerciseId) // fromWantedExercise
        );
    }
}
