package io.openbas.helper;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.openbas.execution.ExecutionContext;
import java.io.StringReader;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

public class TemplateHelper {
  public static String buildContextualContent(String content, ExecutionContext context)
      throws Exception {
    if (content == null) return "";
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
    cfg.setTemplateExceptionHandler(new TemplateExceptionManager());
    cfg.setLogTemplateExceptions(false);
    Template template = new Template("template", new StringReader(content), cfg);
    return FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
  }
}
