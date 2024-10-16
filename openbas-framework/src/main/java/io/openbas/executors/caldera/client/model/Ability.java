package io.openbas.executors.caldera.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ability {

  private String ability_id;
  private String tactic;
  private String technique_name;
  private String technique_id;
  private String name;
  private String description;
  private List<Executor> executors;
}
