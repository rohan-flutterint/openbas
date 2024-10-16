package io.openbas.rest.report.form;

import static io.openbas.config.AppConfig.MANDATORY_MESSAGE;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportInjectCommentInput {
  @NotBlank(message = MANDATORY_MESSAGE)
  @JsonProperty("inject_id")
  private String injectId;

  @JsonProperty("report_inject_comment")
  private String comment;
}
