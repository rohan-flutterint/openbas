package io.openbas.injectors.ovh;

import static io.openbas.database.model.InjectStatusExecution.traceError;
import static io.openbas.database.model.InjectStatusExecution.traceSuccess;

import io.openbas.database.model.Execution;
import io.openbas.database.model.Inject;
import io.openbas.execution.ExecutableInject;
import io.openbas.execution.ExecutionContext;
import io.openbas.execution.Injector;
import io.openbas.execution.ProtectUser;
import io.openbas.injectors.ovh.model.OvhSmsContent;
import io.openbas.injectors.ovh.service.OvhSmsService;
import io.openbas.model.ExecutionProcess;
import io.openbas.model.Expectation;
import io.openbas.model.expectation.ManualExpectation;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component(OvhSmsContract.TYPE)
@RequiredArgsConstructor
public class OvhSmsExecutor extends Injector {

  private final OvhSmsService smsService;

  @Override
  public ExecutionProcess process(
      @NotNull final Execution execution, @NotNull final ExecutableInject injection)
      throws Exception {
    Inject inject = injection.getInjection().getInject();
    OvhSmsContent content = contentConvert(injection, OvhSmsContent.class);
    String smsMessage = content.buildMessage(inject.getFooter(), inject.getHeader());
    List<ExecutionContext> users = injection.getUsers();
    if (users.isEmpty()) {
      throw new UnsupportedOperationException("Sms needs at least one user");
    }

    // We check that at least one user receive the sms before to create expectations
    AtomicBoolean isSmsSent = new AtomicBoolean(false);

    users.stream()
        .parallel()
        .forEach(
            context -> {
              ProtectUser user = context.getUser();
              String phone = user.getPhone();
              String email = user.getEmail();
              if (!StringUtils.hasLength(phone)) {
                String message = "Sms fail for " + email + ": no phone number";
                execution.addTrace(traceError(message));
              } else {
                try {
                  String callResult = smsService.sendSms(context, phone, smsMessage);
                  isSmsSent.set(true);
                  String message =
                      "Sms sent to " + email + " through " + phone + " (" + callResult + ")";
                  execution.addTrace(traceSuccess(message));
                } catch (Exception e) {
                  execution.addTrace(traceError(e.getMessage()));
                }
              }
            });
    if (isSmsSent.get()) {
      List<Expectation> expectations =
          content.getExpectations().stream()
              .flatMap(
                  entry ->
                      switch (entry.getType()) {
                        case MANUAL -> Stream.of((Expectation) new ManualExpectation(entry));
                        default -> Stream.of();
                      })
              .toList();
      return new ExecutionProcess(false, expectations);
    }
    return new ExecutionProcess(false, Collections.emptyList());
  }
}
