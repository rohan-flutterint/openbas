package io.openbas.utils;

import static org.springframework.util.StringUtils.hasText;

import io.openbas.database.model.Base;
import io.openbas.utils.schema.PropertySchema;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class JpaUtils {

  private JpaUtils() {}

  private static <U> Path<U> computePath(
      @NotNull final From<?, ?> from, @NotNull final String key) {
    String[] jsonPaths = key.split("\\.");

    // Deep path -> use join
    if (jsonPaths.length > 1) {
      From<?, ?> currentFrom = from;
      for (int i = 0; i < jsonPaths.length - 1; i++) {
        currentFrom = currentFrom.join(jsonPaths[i], JoinType.LEFT);
      }
      // Last path part -> use get
      return currentFrom.get(jsonPaths[jsonPaths.length - 1]);
    }

    // Simple path -> use get
    else if (jsonPaths.length == 1) {
      return from.get(jsonPaths[0]);
    }

    return null;
  }

  public static <T, U> Expression<U> toPath(
      @NotNull final PropertySchema propertySchema,
      @NotNull final Root<T> root,
      @NotNull final Map<String, Join<Base, Base>> joinMap) {
    // Path
    if (hasText(propertySchema.getPath())) {
      if (joinMap.isEmpty()) {
        return computePath(root, propertySchema.getPath());
      }

      String existingPath = propertySchema.getPath();
      Join<Base, Base> existingJoin = null;
      String existingKey = null;

      // Compute existing join
      while (hasText(existingPath)) {
        if (joinMap.containsKey(existingPath)) {
          existingJoin = joinMap.get(existingPath);
          existingKey = existingPath;
          break;
        }
        // Nothing found -> exit
        int lastDotIndex = existingPath.lastIndexOf(".");
        if (lastDotIndex == -1) {
          break;
        }
        existingPath = existingPath.substring(0, existingPath.lastIndexOf("."));
      }

      // If existing join in joinMap
      if (existingJoin != null) {
        // If equals to key -> return it
        if (existingKey.equals(propertySchema.getPath())) {
          return (Expression<U>) existingJoin;
          // If not, compute the remaining path and return it
        } else {
          String remainingPath = propertySchema.getPath().substring(existingKey.length() + 1);
          return computePath(existingJoin, remainingPath);
        }
      }

      return computePath(root, propertySchema.getPath());
    }
    // Join
    if (propertySchema.getJoinTable() != null) {
      PropertySchema.JoinTable joinTable = propertySchema.getJoinTable();
      return root.join(joinTable.getJoinOn(), JoinType.LEFT).get("id");
    } else {
      return root.get(propertySchema.getName());
    }
  }

  // -- FUNCTION --

  public static <T, U> Expression<String[]> arrayAggOnId(
      @NotNull final CriteriaBuilder cb, @NotNull final Join<T, U> join) {
    return cb.function(
        "array_remove",
        String[].class,
        cb.function("array_agg", String[].class, join.get("id")),
        cb.nullLiteral(String.class));
  }

  // -- JOIN --

  public static <X, Y> Join<X, Y> createLeftJoin(Root<X> root, String attributeName) {
    return root.join(attributeName, JoinType.LEFT);
  }

  public static <X, Y> Expression<String[]> createJoinArrayAggOnId(
      CriteriaBuilder cb, Root<X> root, String attributeName) {
    Join<X, Y> join = createLeftJoin(root, attributeName);
    return arrayAggOnId(cb, join);
  }
}
