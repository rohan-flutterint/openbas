package io.openbas.database.repository;

import io.openbas.database.model.ChallengeFlag;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeFlagRepository
    extends CrudRepository<ChallengeFlag, String>, JpaSpecificationExecutor<ChallengeFlag> {

  @NotNull
  Optional<ChallengeFlag> findById(@NotNull String id);
}
