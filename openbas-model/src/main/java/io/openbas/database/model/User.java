package io.openbas.database.model;

import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static java.util.stream.StreamSupport.stream;
import static lombok.AccessLevel.NONE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.openbas.annotation.Queryable;
import io.openbas.database.audit.ModelBaseListener;
import io.openbas.helper.MonoIdDeserializer;
import io.openbas.helper.MultiIdListDeserializer;
import io.openbas.helper.MultiIdSetDeserializer;
import io.openbas.helper.UserHelper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Entity
@Table(name = "users")
@EntityListeners(ModelBaseListener.class)
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = "Player.tags-organization",
      attributeNodes = {@NamedAttributeNode("tags"), @NamedAttributeNode("organization")})
})
public class User implements Base {

  public static final String ADMIN_UUID = "89206193-dbfb-4513-a186-d72c037dda4c";
  public static final String ADMIN_FIRSTNAME = "admin";
  public static final String ADMIN_LASTNAME = "openbas";
  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  public static final String ROLE_USER = "ROLE_USER";
  public static final String THEME_DEFAULT = "default";
  public static final String LANG_AUTO = "auto";

  @Setter
  @Id
  @Column(name = "user_id")
  @GeneratedValue(generator = "UUID")
  @UuidGenerator
  @JsonProperty("user_id")
  @NotBlank
  private String id;

  @Setter
  @Column(name = "user_firstname")
  @JsonProperty("user_firstname")
  @Queryable(filterable = true, searchable = true, sortable = true)
  private String firstname;

  @Setter
  @Column(name = "user_lastname")
  @JsonProperty("user_lastname")
  @Queryable(filterable = true, searchable = true, sortable = true)
  private String lastname;

  @Getter(NONE)
  @Setter
  @Column(name = "user_lang")
  @JsonProperty("user_lang")
  private String lang = LANG_AUTO;

  @Getter(NONE)
  @Setter
  @Column(name = "user_theme")
  @JsonProperty("user_theme")
  private String theme = THEME_DEFAULT;

  @Getter(NONE)
  @Column(name = "user_email")
  @JsonProperty("user_email")
  @Queryable(filterable = true, searchable = true, sortable = true)
  @NotBlank
  private String email;

  @Setter
  @Column(name = "user_phone")
  @JsonProperty("user_phone")
  private String phone;

  @Setter
  @Column(name = "user_phone2")
  @JsonProperty("user_phone2")
  private String phone2;

  @Setter
  @Column(name = "user_pgp_key")
  @JsonProperty("user_pgp_key")
  private String pgpKey;

  @Setter
  @Column(name = "user_status")
  @JsonProperty("user_status")
  @NotNull
  private Short status = 0;

  @Setter
  @Column(name = "user_password")
  @JsonIgnore
  private String password;

  @Setter
  @Column(name = "user_created_at")
  @JsonProperty("user_created_at")
  @NotNull
  private Instant createdAt = now();

  @Setter
  @Column(name = "user_updated_at")
  @JsonProperty("user_updated_at")
  @NotNull
  private Instant updatedAt = now();

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_organization")
  @JsonSerialize(using = MonoIdDeserializer.class)
  @JsonProperty("user_organization")
  @Queryable(dynamicValues = true, filterable = true, sortable = true, path = "organization.id")
  private Organization organization;

  @Setter
  @Column(name = "user_admin")
  @JsonProperty("user_admin")
  @Queryable(filterable = true, sortable = true)
  private boolean admin = false;

  @Setter
  @Column(name = "user_country")
  @JsonProperty("user_country")
  private String country;

  @Setter
  @Column(name = "user_city")
  @JsonProperty("user_city")
  private String city;

  @Setter
  // @ManyToMany(fetch = FetchType.LAZY)
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_groups",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "group_id"))
  @JsonSerialize(using = MultiIdListDeserializer.class)
  @JsonProperty("user_groups")
  private List<Group> groups = new ArrayList<>();

  @Setter
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "users_teams",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "team_id"))
  @JsonSerialize(using = MultiIdListDeserializer.class)
  @JsonProperty("user_teams")
  private List<Team> teams = new ArrayList<>();

  @Setter
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "users_tags",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @JsonSerialize(using = MultiIdSetDeserializer.class)
  @JsonProperty("user_tags")
  @Queryable(dynamicValues = true, filterable = true, sortable = true, path = "tags.id")
  private Set<Tag> tags = new HashSet<>();

  @Setter
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "communications_users",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "communication_id"))
  @JsonSerialize(using = MultiIdListDeserializer.class)
  @JsonProperty("user_communications")
  private List<Communication> communications = new ArrayList<>();

  @Setter
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @JsonIgnore
  private List<Token> tokens = new ArrayList<>();

  @Setter
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  @JsonIgnore
  private List<ComcheckStatus> comcheckStatuses = new ArrayList<>();

  public String getLang() {
    return ofNullable(this.lang).orElse(LANG_AUTO);
  }

  public String getTheme() {
    return ofNullable(this.theme).orElse(THEME_DEFAULT);
  }

  public String getEmail() {
    return ofNullable(this.email).map(String::toLowerCase).orElse(null);
  }

  public void setEmail(final String email) {
    this.email =
        ofNullable(email)
            .map(String::toLowerCase)
            .orElseThrow(() -> new IllegalArgumentException("Email can't be null"));
  }

  @Transient private List<Inject> injects = new ArrayList<>();

  public void resolveInjects(Iterable<Inject> injects) {
    this.injects =
        stream(injects.spliterator(), false)
            .filter(
                inject ->
                    inject.isAllTeams()
                        || inject.getTeams().stream().anyMatch(team -> getTeams().contains(team)))
            .collect(Collectors.toList());
  }

  @JsonProperty("user_injects")
  @JsonSerialize(using = MultiIdListDeserializer.class)
  public List<Inject> getUserInject() {
    return this.injects;
  }

  @JsonProperty("user_injects_number")
  public long getUserInjectsNumber() {
    return this.injects.size();
  }

  @JsonProperty("user_gravatar")
  public String getGravatar() {
    return UserHelper.getGravatar(getEmail());
  }

  @JsonProperty("user_is_planner")
  public boolean isPlanner() {
    return isAdmin()
        || getGroups().stream()
            .flatMap(group -> group.getGrants().stream())
            .anyMatch(grant -> Grant.GRANT_TYPE.PLANNER.equals(grant.getName()));
  }

  @JsonProperty("user_is_observer")
  public boolean isObserver() {
    return isAdmin() || getGroups().stream().mapToLong(group -> group.getGrants().size()).sum() > 0;
  }

  @JsonProperty("user_is_manager")
  public boolean isManager() {
    return isPlanner() || isObserver();
  }

  @JsonProperty("user_is_player")
  public boolean isPlayer() {
    return isAdmin() || isPlanner() || isObserver() || !getTeams().isEmpty();
  }

  @JsonProperty("user_last_comcheck")
  public Optional<Instant> getLastComcheck() {
    return getComcheckStatuses().stream()
        .filter(comcheckStatus -> comcheckStatus.getReceiveDate().isPresent())
        .map(comcheckStatus -> comcheckStatus.getReceiveDate().get())
        .min(Instant::compareTo);
  }

  @JsonProperty("user_is_external")
  public boolean isExternal() {
    return this.getId().equals(ADMIN_UUID);
  }

  @JsonIgnore
  public String getName() {
    return getFirstname() + " " + getLastname();
  }

  @JsonProperty("user_is_only_player")
  public boolean isOnlyPlayer() {
    return !isAdmin() && !isManager();
  }

  @Override
  public boolean isUserHasAccess(User user) {
    return user.isAdmin() || user.getId().equals(getId());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !Base.class.isAssignableFrom(o.getClass())) {
      return false;
    }
    Base base = (Base) o;
    return this.id.equals(base.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public String toString() {
    return this.email;
  }
}
