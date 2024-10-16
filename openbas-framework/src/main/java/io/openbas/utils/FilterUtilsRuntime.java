package io.openbas.utils;

import static io.openbas.database.model.Filters.FilterMode.and;
import static io.openbas.database.model.Filters.FilterMode.or;
import static io.openbas.utils.schema.SchemaUtils.getFilterableProperties;
import static io.openbas.utils.schema.SchemaUtils.retrieveProperty;

import io.openbas.database.model.Filters.Filter;
import io.openbas.database.model.Filters.FilterGroup;
import io.openbas.database.model.Filters.FilterMode;
import io.openbas.database.model.Filters.FilterOperator;
import io.openbas.utils.schema.PropertySchema;
import io.openbas.utils.schema.SchemaUtils;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class FilterUtilsRuntime {

  private FilterUtilsRuntime() {}

  private static final Predicate<Object> EMPTY_PREDICATE = (value) -> true;

  public static Predicate<Object> computeFilterGroupRuntime(
      @Nullable final FilterGroup filterGroup) {
    if (filterGroup == null) {
      return EMPTY_PREDICATE;
    }
    List<Filter> filters = Optional.ofNullable(filterGroup.getFilters()).orElse(new ArrayList<>());
    FilterMode mode = Optional.ofNullable(filterGroup.getMode()).orElse(and);

    if (!filters.isEmpty()) {
      List<Predicate<Object>> list =
          filters.stream().map(FilterUtilsRuntime::computeFilter).toList();
      Predicate<Object> result = null;
      for (Predicate<Object> el : list) {
        if (result == null) {
          result = el;
        } else {
          if (or.equals(mode)) {
            result = result.or(el);
          } else {
            // Default case
            result = result.and(el);
          }
        }
      }
      return result;
    }
    return EMPTY_PREDICATE;
  }

  private static Predicate<Object> computeFilter(@Nullable final Filter filter) {
    if (filter == null) {
      return EMPTY_PREDICATE;
    }
    String filterKey = filter.getKey();
    List<String> filterValues = filter.getValues();

    if (!filterValues.isEmpty()) {
      return (value) -> {
        List<PropertySchema> propertySchemas = SchemaUtils.schema(value.getClass());
        List<PropertySchema> filterableProperties = getFilterableProperties(propertySchemas);
        PropertySchema filterableProperty = retrieveProperty(filterableProperties, filterKey);
        Map.Entry<Class<Object>, Object> entry = getPropertyInfo(value, filterableProperty);
        return getPropertyValue(entry, filter);
      };
    }
    return EMPTY_PREDICATE;
  }

  @SuppressWarnings("unchecked")
  private static boolean getPropertyValue(Map.Entry<Class<Object>, Object> entry, Filter filter) {
    if (entry == null || entry.getValue() == null) {
      return false;
    }

    BiFunction<Object, List<String>, Boolean> operation = computeOperation(filter.getOperator());

    if (entry.getKey().isAssignableFrom(Map.class)
        || entry.getKey().getName().contains("ImmutableCollections")) {
      return ((Map) entry.getValue())
          .values().stream().anyMatch(v -> operation.apply(v, filter.getValues()));
    } else if (entry.getKey().isArray()) {
      return Arrays.stream(((Object[]) entry.getValue()))
          .anyMatch(v -> operation.apply(v, filter.getValues()));
    } else {
      return operation.apply(entry.getValue(), filter.getValues());
    }
  }

  @SuppressWarnings("unchecked")
  private static Map.Entry<Class<Object>, Object> getPropertyInfo(
      Object obj, PropertySchema propertySchema) {
    if (obj == null) {
      return null;
    }

    Field field;
    Object currentObject;
    try {
      field = obj.getClass().getDeclaredField(propertySchema.getName());
      field.setAccessible(true);

      currentObject = field.get(obj);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    return Map.entry((Class<Object>) currentObject.getClass(), currentObject);
  }

  // -- OPERATOR --

  private static BiFunction<Object, List<String>, Boolean> computeOperation(
      @NotNull final FilterOperator operator) {
    if (operator == null) {
      // Default case
      return OperationUtilsRuntime::equalsTexts;
    }

    if (operator.equals(FilterOperator.not_contains)) {
      return OperationUtilsRuntime::notContainsTexts;
    } else if (operator.equals(FilterOperator.contains)) {
      return OperationUtilsRuntime::containsTexts;
    } else if (operator.equals(FilterOperator.not_starts_with)) {
      return OperationUtilsRuntime::notStartWithTexts;
    } else if (operator.equals(FilterOperator.starts_with)) {
      return OperationUtilsRuntime::startWithTexts;
    } else if (operator.equals(FilterOperator.not_eq)) {
      return OperationUtilsRuntime::notEqualsTexts;
    } else if (operator.equals(FilterOperator.empty)) {
      return (value, texts) -> OperationUtilsRuntime.empty(value);
    } else if (operator.equals(FilterOperator.not_empty)) {
      return (value, texts) -> OperationUtilsRuntime.notEmpty(value);
    } else { // Default case
      return OperationUtilsRuntime::equalsTexts;
    }
  }
}
