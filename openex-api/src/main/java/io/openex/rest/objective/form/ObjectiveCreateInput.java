package io.openex.rest.objective.form;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectiveCreateInput {

    @JsonProperty("evaluation_score")
    private Long score;

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
