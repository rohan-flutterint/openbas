package io.openbas.rest.exercise.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpectationUpdateInput {
  @JsonProperty("source_id")
  @NotNull
  private String sourceId;

  @JsonProperty("source_type")
  @NotNull
  private String sourceType;

  @JsonProperty("source_name")
  @NotNull
  private String sourceName;

  @JsonProperty("expectation_score")
  @NotNull
  private Double score;
}
