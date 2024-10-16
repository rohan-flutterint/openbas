package io.openbas.database.repository;

import io.openbas.database.model.Setting;
import java.util.Collection;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SettingRepository
    extends CrudRepository<Setting, String>, JpaSpecificationExecutor<Setting> {

  @NotNull
  Optional<Setting> findById(@NotNull final String id);

  Optional<Setting> findByKey(@NotNull final String key);

  @Query(value = "SHOW server_version", nativeQuery = true)
  String getServerVersion();

  @Transactional
  void deleteByKeyIn(@NotNull final Collection<String> keys);
}
