package io.openbas.database.model;

import static java.time.Instant.now;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.openbas.database.audit.ModelBaseListener;
import io.openbas.helper.MonoIdDeserializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Entity
@Table(name = "channels")
@EntityListeners(ModelBaseListener.class)
public class Channel implements Base {

  @Id
  @Column(name = "channel_id")
  @GeneratedValue(generator = "UUID")
  @UuidGenerator
  @JsonProperty("channel_id")
  @NotBlank
  private String id;

  @Column(name = "channel_created_at")
  @JsonProperty("channel_created_at")
  @NotNull
  private Instant createdAt = now();

  @Column(name = "channel_updated_at")
  @JsonProperty("channel_updated_at")
  @NotNull
  private Instant updatedAt = now();

  @Column(name = "channel_type")
  @JsonProperty("channel_type")
  private String type;

  @Column(name = "channel_name")
  @JsonProperty("channel_name")
  private String name;

  @Column(name = "channel_description")
  @JsonProperty("channel_description")
  private String description;

  @Column(name = "channel_mode")
  @JsonProperty("channel_mode")
  private String mode;

  @Column(name = "channel_primary_color_dark")
  @JsonProperty("channel_primary_color_dark")
  private String primaryColorDark;

  @Column(name = "channel_primary_color_light")
  @JsonProperty("channel_primary_color_light")
  private String primaryColorLight;

  @Column(name = "channel_secondary_color_dark")
  @JsonProperty("channel_secondary_color_dark")
  private String secondaryColorDark;

  @Column(name = "channel_secondary_color_light")
  @JsonProperty("channel_secondary_color_light")
  private String secondaryColorLight;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_logo_dark")
  @JsonSerialize(using = MonoIdDeserializer.class)
  @JsonProperty("channel_logo_dark")
  private Document logoDark;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_logo_light")
  @JsonSerialize(using = MonoIdDeserializer.class)
  @JsonProperty("channel_logo_light")
  private Document logoLight;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public boolean isUserHasAccess(User user) {
    return user.isAdmin();
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getMode() {
    return mode;
  }

  public String getPrimaryColorDark() {
    return primaryColorDark;
  }

  public String getPrimaryColorLight() {
    return primaryColorLight;
  }

  public String getSecondaryColorDark() {
    return secondaryColorDark;
  }

  public String getSecondaryColorLight() {
    return secondaryColorLight;
  }

  public Document getLogoDark() {
    return logoDark;
  }

  public Document getLogoLight() {
    return logoLight;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public List<Document> getLogos() {
    List<Document> logos = new ArrayList<>();
    if (logoLight != null) {
      logos.add(logoLight);
    }
    if (logoDark != null) {
      logos.add(logoDark);
    }
    return logos;
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
