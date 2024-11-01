package io.openbas.rest.inject.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.openbas.database.model.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InjectDependencyInput {

  @JsonProperty("dependency_relationship")
  private InjectDependencyIdInput relationship;

  @JsonProperty("dependency_condition")
  private InjectDependencyConditions.InjectDependencyCondition conditions;
}
