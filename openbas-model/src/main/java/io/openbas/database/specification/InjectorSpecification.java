package io.openbas.database.specification;

import io.openbas.database.model.Injector;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class InjectorSpecification {

  private InjectorSpecification() {}

  public static Specification<Injector> byName(@Nullable final String searchText) {
    return UtilsSpecification.byName(searchText, "name");
  }
}
