package io.openbas.rest.user.form.me;

import static io.openbas.config.AppConfig.EMAIL_FORMAT;
import static io.openbas.config.AppConfig.MANDATORY_MESSAGE;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileInput {

  @Email(message = EMAIL_FORMAT)
  @JsonProperty("user_email")
  private String email;

  @NotBlank(message = MANDATORY_MESSAGE)
  @JsonProperty("user_firstname")
  private String firstname;

  @NotBlank(message = MANDATORY_MESSAGE)
  @JsonProperty("user_lastname")
  private String lastname;

  @JsonProperty("user_organization")
  private String organizationId;

  @JsonProperty("user_lang")
  private String lang;

  @JsonProperty("user_theme")
  private String theme;

  @JsonProperty("user_country")
  private String country;
}
