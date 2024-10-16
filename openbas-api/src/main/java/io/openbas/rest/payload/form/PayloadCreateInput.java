package io.openbas.rest.payload.form;

import static io.openbas.config.AppConfig.MANDATORY_MESSAGE;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.openbas.database.model.Endpoint;
import io.openbas.database.model.Endpoint.PLATFORM_TYPE;
import io.openbas.database.model.Payload.PAYLOAD_SOURCE;
import io.openbas.database.model.Payload.PAYLOAD_STATUS;
import io.openbas.database.model.PayloadArgument;
import io.openbas.database.model.PayloadPrerequisite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayloadCreateInput {

  @NotBlank(message = MANDATORY_MESSAGE)
  @JsonProperty("payload_type")
  private String type;

  @NotBlank(message = MANDATORY_MESSAGE)
  @JsonProperty("payload_name")
  private String name;

  @NotNull(message = MANDATORY_MESSAGE)
  @JsonProperty("payload_source")
  private PAYLOAD_SOURCE source;

  @NotNull(message = MANDATORY_MESSAGE)
  @JsonProperty("payload_status")
  private PAYLOAD_STATUS status;

  @NotEmpty(message = MANDATORY_MESSAGE)
  @JsonProperty("payload_platforms")
  private PLATFORM_TYPE[] platforms;

  @JsonProperty("payload_description")
  private String description;

  @JsonProperty("command_executor")
  private String executor;

  @JsonProperty("command_content")
  private String content;

  @JsonProperty("executable_arch")
  private Endpoint.PLATFORM_ARCH executableArch;

  @JsonProperty("executable_file")
  private String executableFile;

  @JsonProperty("file_drop_file")
  private String fileDropFile;

  @JsonProperty("dns_resolution_hostname")
  private String hostname;

  @JsonProperty("payload_arguments")
  private List<PayloadArgument> arguments;

  @JsonProperty("payload_prerequisites")
  private List<PayloadPrerequisite> prerequisites;

  @JsonProperty("payload_cleanup_executor")
  private String cleanupExecutor;

  @JsonProperty("payload_cleanup_command")
  private String cleanupCommand;

  @JsonProperty("payload_tags")
  private List<String> tagIds = new ArrayList<>();

  @JsonProperty("payload_attack_patterns")
  private List<String> attackPatternsIds = new ArrayList<>();
}
