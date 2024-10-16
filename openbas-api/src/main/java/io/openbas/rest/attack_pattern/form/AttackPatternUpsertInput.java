package io.openbas.rest.attack_pattern.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class AttackPatternUpsertInput {

  @JsonProperty("attack_patterns")
  private List<AttackPatternCreateInput> attackPatterns = new ArrayList<>();
}
