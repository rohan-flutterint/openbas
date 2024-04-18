package io.openbas.utils.schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.NONE;
import static org.springframework.util.StringUtils.hasText;

@Builder
@Getter
public class PropertySchema {

  @NotBlank
  private final String name;

  @Getter(NONE)
  private final String jsonName;

  @NotNull
  private final Class<?> type;

  private final boolean unicity;
  private final boolean mandatory;
  private final boolean multiple;

  private final boolean searchable;
  private final boolean filterable;
  private final List<String> availableValues;
  private final boolean sortable;
  private final String propertyRepresentative;

  private final JoinTable joinTable;

  @Singular("propertySchema")
  private final List<PropertySchema> propertiesSchema;

  public String getJsonName() {
    return Optional.ofNullable(this.jsonName).orElse(this.name);
  }

  @Builder
  @Getter
  public static class JoinTable {
    private final String joinOn;
  }

  // -- VALIDATION --

  public static PropertySchemaBuilder builder() {
    return new ValidationBuilder();
  }

  private static class ValidationBuilder extends PropertySchemaBuilder {

    public PropertySchema build() {
      if (!hasText(super.name)) {
        throw new RuntimeException("Property name should not be empty");
      }
      if (super.type == null) {
        throw new RuntimeException("Property type should not be empty");
      }

      return super.build();
    }
  }

}
