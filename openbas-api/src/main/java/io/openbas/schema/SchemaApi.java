package io.openbas.schema;

import io.openbas.rest.helper.RestBehavior;
import io.openbas.schema.model.PropertySchemaDTO;
import io.openbas.utils.schema.PropertySchema;
import io.openbas.utils.schema.SchemaUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class SchemaApi extends RestBehavior {

  @PostMapping("/api/schemas/{className}")
  public List<PropertySchemaDTO> schemas(
      @PathVariable @NotNull final String className,
      @RequestParam final boolean filterableOnly,
      @RequestBody @Valid @NotNull List<String> filterNames)
      throws ClassNotFoundException {
    String completeClassName = "io.openbas.database.model." + className;
    if (filterableOnly) {
      return SchemaUtils.schemaWithSubtypes(Class.forName(completeClassName)).stream()
          .filter(PropertySchema::isFilterable)
          .filter(p -> filterNames.isEmpty() || filterNames.contains(p.getJsonName()))
          .map(PropertySchemaDTO::new)
          .toList();
    }
    return SchemaUtils.schemaWithSubtypes(Class.forName(completeClassName)).stream()
        .filter(p -> filterNames.isEmpty() || filterNames.contains(p.getJsonName()))
        .map(PropertySchemaDTO::new)
        .toList();
  }
}
