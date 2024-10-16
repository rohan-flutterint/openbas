package io.openbas.rest.report.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.openbas.database.model.ReportInformationsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportInformationInput {
  @JsonProperty("report_informations_type")
  @NotBlank
  private ReportInformationsType reportInformationsType;

  @JsonProperty("report_informations_display")
  @NotNull
  private Boolean reportInformationsDisplay;
}
