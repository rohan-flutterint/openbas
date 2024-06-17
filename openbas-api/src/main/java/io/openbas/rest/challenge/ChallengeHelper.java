package io.openbas.rest.challenge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.openbas.database.model.Inject;
import io.openbas.injectors.challenge.model.ChallengeContent;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Stream;

import static io.openbas.injectors.challenge.ChallengeContract.CHALLENGE_PUBLISH;

public class ChallengeHelper {

  private ChallengeHelper() {

  }

  public static List<String> resolveChallengeIds(
      @NotNull final List<Inject> injects,
      ObjectMapper mapper) {
    return injects.stream()
        .filter(inject -> inject.getInjectorContract().getId().equals(CHALLENGE_PUBLISH))
        .filter(inject -> inject.getContent() != null)
        .flatMap(inject -> {
          try {
            ChallengeContent content = mapper.treeToValue(inject.getContent(), ChallengeContent.class);
            return content.getChallenges().stream();
          } catch (JsonProcessingException e) {
            return Stream.empty();
          }
        })
        .distinct().toList();
  }

}