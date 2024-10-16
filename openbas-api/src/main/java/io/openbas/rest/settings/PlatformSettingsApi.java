package io.openbas.rest.settings;

import static io.openbas.database.model.User.ROLE_ADMIN;

import io.openbas.rest.helper.RestBehavior;
import io.openbas.rest.settings.form.PolicyInput;
import io.openbas.rest.settings.form.SettingsEnterpriseEditionUpdateInput;
import io.openbas.rest.settings.form.SettingsPlatformWhitemarkUpdateInput;
import io.openbas.rest.settings.form.SettingsUpdateInput;
import io.openbas.rest.settings.form.ThemeInput;
import io.openbas.rest.settings.response.PlatformSettings;
import io.openbas.service.PlatformSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/settings")
@RestController
public class PlatformSettingsApi extends RestBehavior {

  private PlatformSettingsService platformSettingsService;

  @Autowired
  public void setPlatformSettingsService(PlatformSettingsService platformSettingsService) {
    this.platformSettingsService = platformSettingsService;
  }

  @GetMapping()
  public PlatformSettings settings() {
    return platformSettingsService.findSettings();
  }

  @Secured(ROLE_ADMIN)
  @PutMapping()
  public PlatformSettings updateBasicConfigurationSettings(
      @Valid @RequestBody SettingsUpdateInput input) {
    return platformSettingsService.updateBasicConfigurationSettings(input);
  }

  @Secured(ROLE_ADMIN)
  @PutMapping("/enterprise_edition")
  public PlatformSettings updateSettingsEnterpriseEdition(
      @Valid @RequestBody SettingsEnterpriseEditionUpdateInput input) {
    return platformSettingsService.updateSettingsEnterpriseEdition(input);
  }

  @Secured(ROLE_ADMIN)
  @PutMapping("/platform_whitemark")
  public PlatformSettings updateSettingsPlatformWhitemark(
      @Valid @RequestBody SettingsPlatformWhitemarkUpdateInput input) {
    return platformSettingsService.updateSettingsPlatformWhitemark(input);
  }

  @Secured(ROLE_ADMIN)
  @PutMapping("/theme/light")
  public PlatformSettings updateThemeLight(@Valid @RequestBody ThemeInput input) {
    return platformSettingsService.updateThemeLight(input);
  }

  @Secured(ROLE_ADMIN)
  @PutMapping("/theme/dark")
  public PlatformSettings updateThemeDark(@Valid @RequestBody ThemeInput input) {
    return platformSettingsService.updateThemeDark(input);
  }

  @Secured(ROLE_ADMIN)
  @PutMapping("/policies")
  public PlatformSettings updateSettingsPolicies(@Valid @RequestBody PolicyInput input) {
    return platformSettingsService.updateSettingsPolicies(input);
  }
}
