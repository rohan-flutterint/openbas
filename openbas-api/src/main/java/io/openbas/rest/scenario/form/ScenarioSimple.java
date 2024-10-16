package io.openbas.rest.scenario.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.openbas.database.model.Scenario;
import io.openbas.database.model.Tag;
import io.openbas.database.raw.RawScenario;
import io.openbas.helper.MultiIdSetDeserializer;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ScenarioSimple {

  @JsonProperty("scenario_id")
  private String id;

  @JsonProperty("scenario_name")
  private String name;

  @JsonProperty("scenario_subtitle")
  private String subtitle;

  @JsonSerialize(using = MultiIdSetDeserializer.class)
  @JsonProperty("scenario_tags")
  private Set<Tag> tags = new HashSet<>();

  public static ScenarioSimple fromScenario(@NotNull final Scenario scenario) {
    ScenarioSimple simple = new ScenarioSimple();
    BeanUtils.copyProperties(scenario, simple);
    return simple;
  }

  public static ScenarioSimple fromRawScenario(@NotNull final RawScenario scenario) {
    ScenarioSimple simple = new ScenarioSimple();
    simple.setId(scenario.getScenario_id());
    simple.setName(scenario.getScenario_name());
    simple.setSubtitle(scenario.getScenario_subtitle());
    if (scenario.getScenario_tags() != null) {
      simple.setTags(
          scenario.getScenario_tags().stream()
              .map(
                  (tagId) -> {
                    Tag tag = new Tag();
                    tag.setId(tagId);
                    return tag;
                  })
              .collect(Collectors.toSet()));
    } else {
      simple.setTags(new HashSet<>());
    }
    return simple;
  }
}
