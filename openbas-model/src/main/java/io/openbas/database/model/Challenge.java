package io.openbas.database.model;

import static java.time.Instant.now;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.openbas.database.audit.ModelBaseListener;
import io.openbas.helper.MultiIdListDeserializer;
import io.openbas.helper.MultiIdSetDeserializer;
import io.openbas.helper.MultiModelDeserializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@Table(name = "challenges")
@EntityListeners(ModelBaseListener.class)
public class Challenge implements Base {

  @Id
  @Column(name = "challenge_id")
  @GeneratedValue(generator = "UUID")
  @UuidGenerator
  @JsonProperty("challenge_id")
  @NotBlank
  private String id;

  @Column(name = "challenge_created_at")
  @JsonProperty("challenge_created_at")
  @NotNull
  private Instant createdAt = now();

  @Column(name = "challenge_updated_at")
  @JsonProperty("challenge_updated_at")
  @NotNull
  private Instant updatedAt = now();

  @Column(name = "challenge_name")
  @JsonProperty("challenge_name")
  private String name;

  @Column(name = "challenge_category")
  @JsonProperty("challenge_category")
  private String category;

  @Column(name = "challenge_content")
  @JsonProperty("challenge_content")
  private String content;

  @Column(name = "challenge_score")
  @JsonProperty("challenge_score")
  private Double score;

  @Column(name = "challenge_max_attempts")
  @JsonProperty("challenge_max_attempts")
  private Integer maxAttempts;

  // CascadeType.ALL is required here because Flags are embedded
  @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JsonProperty("challenge_flags")
  @JsonSerialize(using = MultiModelDeserializer.class)
  private List<ChallengeFlag> flags = new ArrayList<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "challenges_tags",
      joinColumns = @JoinColumn(name = "challenge_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @JsonSerialize(using = MultiIdSetDeserializer.class)
  @JsonProperty("challenge_tags")
  private Set<Tag> tags = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "challenges_documents",
      joinColumns = @JoinColumn(name = "challenge_id"),
      inverseJoinColumns = @JoinColumn(name = "document_id"))
  @JsonSerialize(using = MultiIdListDeserializer.class)
  @JsonProperty("challenge_documents")
  private List<Document> documents = new ArrayList<>();

  @Transient private List<String> exerciseIds = new ArrayList<>();

  @Transient private List<String> scenarioIds = new ArrayList<>();

  @Transient private Instant virtualPublication;

  @JsonProperty("challenge_virtual_publication")
  public Instant getVirtualPublication() {
    return virtualPublication;
  }

  @Override
  public boolean isUserHasAccess(User user) {
    return user.isAdmin();
  }

  @JsonProperty("challenge_exercises")
  public List<String> getExerciseIds() {
    return exerciseIds;
  }

  @JsonProperty("challenge_scenarios")
  public List<String> getScenarioIds() {
    return scenarioIds;
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
