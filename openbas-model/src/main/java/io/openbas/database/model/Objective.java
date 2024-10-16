package io.openbas.database.model;

import static java.time.Instant.now;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.openbas.database.audit.ModelBaseListener;
import io.openbas.helper.MonoIdDeserializer;
import io.openbas.helper.MultiIdListDeserializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Table(name = "objectives")
@EntityListeners(ModelBaseListener.class)
public class Objective implements Base {

  @Id
  @Column(name = "objective_id")
  @GeneratedValue(generator = "UUID")
  @UuidGenerator
  @JsonProperty("objective_id")
  @NotBlank
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "objective_exercise")
  @JsonSerialize(using = MonoIdDeserializer.class)
  @JsonProperty("objective_exercise")
  private Exercise exercise;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "objective_scenario")
  @JsonSerialize(using = MonoIdDeserializer.class)
  @JsonProperty("objective_scenario")
  private Scenario scenario;

  @Column(name = "objective_title")
  @JsonProperty("objective_title")
  private String title;

  @Column(name = "objective_description")
  @JsonProperty("objective_description")
  private String description;

  @Column(name = "objective_priority")
  @JsonProperty("objective_priority")
  private Short priority;

  @Column(name = "objective_created_at")
  @JsonProperty("objective_created_at")
  @NotNull
  private Instant createdAt = now();

  @Column(name = "objective_updated_at")
  @JsonProperty("objective_updated_at")
  @NotNull
  private Instant updatedAt = now();

  @OneToMany(mappedBy = "objective", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonSerialize(using = MultiIdListDeserializer.class)
  @JsonProperty("objective_evaluations")
  private List<Evaluation> evaluations = new ArrayList<>();

  // region transient
  @JsonProperty("objective_score")
  public Double getEvaluationAverage() {
    return getEvaluations().stream().mapToDouble(Evaluation::getScore).average().orElse(0D);
  }

  // endregion

  @Override
  public boolean isUserHasAccess(User user) {
    return getExercise().isUserHasAccess(user);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || !Base.class.isAssignableFrom(o.getClass())) return false;
    Base base = (Base) o;
    return id.equals(base.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
