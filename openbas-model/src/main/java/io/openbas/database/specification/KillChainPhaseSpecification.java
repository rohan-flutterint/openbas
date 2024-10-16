package io.openbas.database.specification;

import io.openbas.database.model.KillChainPhase;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class KillChainPhaseSpecification {

  private KillChainPhaseSpecification() {}

  public static Specification<KillChainPhase> byName(@Nullable final String searchText) {
    return UtilsSpecification.byName(searchText, "name");
  }
}
