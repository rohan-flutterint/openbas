package io.openbas.database.model;

import static java.time.Instant.now;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

public class Execution {

  private static final Logger LOGGER = Logger.getLogger(Execution.class.getName());

  @Getter
  @JsonProperty("execution_runtime")
  private boolean runtime;

  @Getter
  @Setter
  @JsonProperty("execution_async")
  private boolean async;

  @Getter
  @Setter
  @JsonProperty("execution_expected_count")
  private Integer expectedCount;

  @Getter
  @JsonProperty("execution_start")
  private Instant startTime;

  @JsonProperty("execution_stop")
  private Instant stopTime;

  @Getter
  @Setter
  @JsonProperty("execution_traces")
  private List<InjectStatusExecution> traces = new ArrayList<>();

  public Execution() {
    // Default constructor for serialization
  }

  public Execution(boolean runtime) {
    this.runtime = runtime;
    this.startTime = now();
  }

  @SuppressWarnings("unused")
  public void setRuntime(boolean runtime) {
    this.runtime = runtime;
  }

  public void stop() {
    this.stopTime = now();
  }

  public void addTrace(InjectStatusExecution context) {
    ExecutionStatus status = context.getStatus();
    if (status.equals(ExecutionStatus.SUCCESS) || status.equals(ExecutionStatus.INFO)) {
      LOGGER.log(Level.INFO, context.getMessage());
    } else {
      LOGGER.log(Level.SEVERE, context.getMessage());
    }
    this.traces.add(context);
  }

  @JsonProperty("execution_time")
  public int getExecutionTime() {
    return (int) (this.stopTime.toEpochMilli() - this.startTime.toEpochMilli());
  }

  public ExecutionStatus getStatus() {
    boolean hasSuccess =
        traces.stream().anyMatch(context -> context.getStatus().equals(ExecutionStatus.SUCCESS));
    boolean hasError =
        traces.stream().anyMatch(context -> context.getStatus().equals(ExecutionStatus.ERROR));
    if (!hasSuccess && !hasError) {
      return ExecutionStatus.PENDING;
    } else if (hasSuccess && hasError) {
      return ExecutionStatus.PARTIAL;
    } else {
      return hasSuccess ? ExecutionStatus.SUCCESS : ExecutionStatus.ERROR;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Execution execution = (Execution) o;
    return Objects.equals(stopTime, execution.stopTime)
        && startTime.equals(execution.startTime)
        && Objects.equals(traces, execution.traces);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stopTime, startTime, traces);
  }
}
