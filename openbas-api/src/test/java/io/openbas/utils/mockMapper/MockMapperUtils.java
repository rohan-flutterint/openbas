package io.openbas.utils.mockMapper;

import io.openbas.database.model.ImportMapper;
import io.openbas.database.model.InjectImporter;
import io.openbas.database.model.InjectorContract;
import io.openbas.database.model.RuleAttribute;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class MockMapperUtils {
  public static ImportMapper createImportMapper() {
    ImportMapper importMapper = new ImportMapper();
    importMapper.setId(UUID.randomUUID().toString());
    importMapper.setName("Test");
    importMapper.setUpdateDate(Instant.now());
    importMapper.setCreationDate(Instant.now());
    importMapper.setInjectTypeColumn("A");
    importMapper.setInjectImporters(new ArrayList<>());

    importMapper.getInjectImporters().add(createInjectImporter());

    return importMapper;
  }

  private static InjectImporter createInjectImporter() {
    InjectImporter injectImporter = new InjectImporter();
    injectImporter.setId(UUID.randomUUID().toString());
    injectImporter.setImportTypeValue("Test");
    InjectorContract injectorContract = new InjectorContract();
    injectorContract.setId(UUID.randomUUID().toString());
    injectImporter.setInjectorContract(injectorContract);
    injectImporter.setRuleAttributes(new ArrayList<>());

    injectImporter.getRuleAttributes().add(createRuleAttribute());
    return injectImporter;
  }

  private static RuleAttribute createRuleAttribute() {
    RuleAttribute ruleAttribute = new RuleAttribute();
    ruleAttribute.setColumns("Test");
    ruleAttribute.setName("Test");
    ruleAttribute.setId(UUID.randomUUID().toString());
    ruleAttribute.setAdditionalConfig(Map.of("test", "test"));
    ruleAttribute.setDefaultValue("");
    return ruleAttribute;
  }
}
