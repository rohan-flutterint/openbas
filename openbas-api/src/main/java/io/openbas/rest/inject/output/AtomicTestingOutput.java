package io.openbas.rest.inject.output;

import static io.openbas.database.model.InjectStatus.draftInjectStatus;
import static lombok.AccessLevel.NONE;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.openbas.database.model.InjectStatus;
import io.openbas.database.model.InjectorContract;
import io.openbas.rest.atomic_testing.form.InjectTargetWithResult;
import io.openbas.utils.AtomicTestingMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.Getter;

@Data
public class AtomicTestingOutput {

  @JsonProperty("inject_id")
  @NotBlank
  private String id;

  @JsonProperty("inject_title")
  @NotBlank
  private String title;

  @JsonProperty("inject_updated_at")
  @NotNull
  private Instant updatedAt;

  @JsonProperty("inject_type")
  public String injectType;

  @JsonProperty("inject_injector_contract")
  private InjectorContract injectorContract;

  @Getter(NONE)
  @JsonProperty("inject_status")
  private InjectStatus status;

  public InjectStatus getStatus() {
    if (status == null) {
      return draftInjectStatus();
    }
    return status;
  }

  @JsonProperty("inject_teams")
  @NotNull
  private List<String> teams;

  @JsonProperty("inject_assets")
  @NotNull
  private List<String> assets;

  @JsonProperty("inject_asset_groups")
  @NotNull
  private List<String> assetGroups;

  @JsonProperty("inject_expectations")
  @NotNull
  private List<String> expectations;

  // Pre Calcul

  @JsonProperty("inject_targets")
  private List<InjectTargetWithResult> targets;

  @JsonProperty("inject_expectation_results")
  private List<AtomicTestingMapper.ExpectationResultsByType> expectationResultByTypes =
      new ArrayList<>();

  public AtomicTestingOutput(
      String id,
      String title,
      Instant updatedAt,
      String injectType,
      InjectorContract injectorContract,
      InjectStatus injectStatus,
      String[] injectExpectations,
      String[] teams,
      String[] assets,
      String[] assetGroups) {
    this.id = id;
    this.title = title;
    this.updatedAt = updatedAt;
    this.injectType = injectType;
    this.injectorContract = injectorContract;
    this.status = injectStatus;
    this.expectations =
        injectExpectations != null
            ? new ArrayList<>(Arrays.asList(injectExpectations))
            : new ArrayList<>();

    this.teams = teams != null ? new ArrayList<>(Arrays.asList(teams)) : new ArrayList<>();
    this.assets = assets != null ? new ArrayList<>(Arrays.asList(assets)) : new ArrayList<>();
    this.assetGroups =
        assetGroups != null ? new ArrayList<>(Arrays.asList(assetGroups)) : new ArrayList<>();
  }
}
