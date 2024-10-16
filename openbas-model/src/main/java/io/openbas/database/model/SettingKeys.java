package io.openbas.database.model;

public enum SettingKeys {
  DEFAULT_THEME("platform_theme", "dark"),
  DEFAULT_LANG("platform_lang", "auto"),
  PLATFORM_CONSENT_MESSAGE("platform_consent_message", ""),
  PLATFORM_CONSENT_CONFIRM_TEXT("platform_consent_confirm_text", ""),
  PLATFORM_ENTERPRISE_EDITION("platform_enterprise_edition", "false"),
  PLATFORM_LOGIN_MESSAGE(
      "platform_login_message",
      "This platform is dedicated to Filigran team testing. **Sandbox running the latest rolling release.**"),
  PLATFORM_WHITEMARK("platform_whitemark", "false"),
  PLATFORM_NAME("platform_name", "OpenBAS - Breach and Attack Simulation Platform"),
  PLATFORM_BANNER("platform_banner", "");

  private final String key;
  private final String defaultValue;

  SettingKeys(String key, String defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
  }

  public String key() {
    return key;
  }

  public String defaultValue() {
    return defaultValue;
  }
}
