package io.openbas.rest.injector_contract.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.openbas.config.AppConfig.MANDATORY_MESSAGE;

@Getter
@Setter
public class InjectorContractUpdateMappingInput {
    @Getter
    @JsonProperty("contract_attack_patterns_ids")
    private List<String> attackPatternsIds = new ArrayList<>();

    public List<String> getAttackPatternsIds() {
        return attackPatternsIds;
    }

    public void setAttackPatternsIds(List<String> attackPatternsIds) {
        this.attackPatternsIds = attackPatternsIds;
    }
}
