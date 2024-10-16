package io.openbas.expectation;

import static java.util.Optional.ofNullable;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Setter
public class ExpectationPropertiesConfig {

  public static long DEFAULT_TECHNICAL_EXPECTATION_EXPIRATION_TIME = 21600L; // 6 hours
  public static long DEFAULT_HUMAN_EXPECTATION_EXPIRATION_TIME = 86400L; // 24 hours
  public static int DEFAULT_MANUAL_EXPECTATION_SCORE = 50;

  @Value("${openbas.expectation.technical.expiration-time:#{null}}")
  private Long technicalExpirationTime;

  @Value("${openbas.expectation.detection.expiration-time:#{null}}")
  private Long detectionExpirationTime;

  @Value("${openbas.expectation.prevention.expiration-time:#{null}}")
  private Long preventionExpirationTime;

  @Value("${openbas.expectation.human.expiration-time:#{null}}")
  private Long humanExpirationTime;

  @Value("${openbas.expectation.challenge.expiration-time:#{null}}")
  private Long challengeExpirationTime;

  @Value("${openbas.expectation.article.expiration-time:#{null}}")
  private Long articleExpirationTime;

  @Value("${openbas.expectation.manual.expiration-time:#{null}}")
  private Long manualExpirationTime;

  @Value("${openbas.expectation.manual.default-score-value:#{null}}")
  private Integer defaultManualExpectationScore;

  public long getDetectionExpirationTime() {
    return ofNullable(this.detectionExpirationTime)
        .orElse(
            ofNullable(this.technicalExpirationTime)
                .orElse(DEFAULT_TECHNICAL_EXPECTATION_EXPIRATION_TIME));
  }

  public long getPreventionExpirationTime() {
    return ofNullable(this.preventionExpirationTime)
        .orElse(
            ofNullable(this.technicalExpirationTime)
                .orElse(DEFAULT_TECHNICAL_EXPECTATION_EXPIRATION_TIME));
  }

  public long getChallengeExpirationTime() {
    return ofNullable(this.challengeExpirationTime)
        .orElse(
            ofNullable(this.humanExpirationTime).orElse(DEFAULT_HUMAN_EXPECTATION_EXPIRATION_TIME));
  }

  public long getArticleExpirationTime() {
    return ofNullable(this.articleExpirationTime)
        .orElse(
            ofNullable(this.humanExpirationTime).orElse(DEFAULT_HUMAN_EXPECTATION_EXPIRATION_TIME));
  }

  public long getManualExpirationTime() {
    return ofNullable(this.manualExpirationTime)
        .orElse(
            ofNullable(this.humanExpirationTime).orElse(DEFAULT_HUMAN_EXPECTATION_EXPIRATION_TIME));
  }

  public int getDefaultExpectationScoreValue() {
    return ofNullable(this.defaultManualExpectationScore).orElse(DEFAULT_MANUAL_EXPECTATION_SCORE);
  }
}
