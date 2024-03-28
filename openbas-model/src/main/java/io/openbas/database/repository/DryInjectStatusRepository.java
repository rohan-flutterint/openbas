package io.openbas.database.repository;

import io.openbas.database.model.DryInjectStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DryInjectStatusRepository extends CrudRepository<DryInjectStatus, String>, JpaSpecificationExecutor<DryInjectStatus> {

    @NotNull
    Optional<DryInjectStatus> findById(@NotNull String id);
}