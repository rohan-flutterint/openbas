package io.openbas.rest.scenario.form;

import static io.openbas.config.AppConfig.MANDATORY_MESSAGE;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.openbas.database.model.Scenario.SEVERITY;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ScenarioInput {

  @NotBlank(message = MANDATORY_MESSAGE)
  @JsonProperty("scenario_name")
  private String name;

  @JsonProperty("scenario_description")
  private String description;

  @JsonProperty("scenario_subtitle")
  private String subtitle;

  @Nullable
  @JsonProperty("scenario_category")
  private String category;

  @Nullable
  @JsonProperty("scenario_main_focus")
  private String mainFocus;

  @Nullable
  @JsonProperty("scenario_severity")
  private SEVERITY severity;

  @Nullable
  @JsonProperty("scenario_external_reference")
  private String externalReference;

  @Nullable
  @JsonProperty("scenario_external_url")
  private String externalUrl;

  @JsonProperty("scenario_tags")
  private List<String> tagIds = new ArrayList<>();
}
