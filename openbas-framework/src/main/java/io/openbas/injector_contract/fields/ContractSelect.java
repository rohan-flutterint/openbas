package io.openbas.injector_contract.fields;

import io.openbas.injector_contract.ContractCardinality;
import io.openbas.injector_contract.ContractType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContractSelect extends ContractCardinalityElement {

  private Map<String, String> choices = new HashMap<>();

  public ContractSelect(String key, String label, ContractCardinality cardinality) {
    super(key, label, cardinality);
  }

  public static ContractSelect selectField(String key, String label, Map<String, String> choices) {
    ContractSelect contractSelect = new ContractSelect(key, label, ContractCardinality.One);
    contractSelect.setChoices(choices);
    return contractSelect;
  }

  public static ContractSelect selectFieldWithDefault(
      String key, String label, Map<String, String> choices, String def) {
    ContractSelect contractSelect = new ContractSelect(key, label, ContractCardinality.One);
    contractSelect.setChoices(choices);
    contractSelect.setDefaultValue(List.of(def));
    return contractSelect;
  }

  public static ContractSelect multiSelectField(
      String key, String label, Map<String, String> choices) {
    ContractSelect contractSelect = new ContractSelect(key, label, ContractCardinality.Multiple);
    contractSelect.setChoices(choices);
    return contractSelect;
  }

  @Override
  public ContractType getType() {
    return ContractType.Select;
  }
}
