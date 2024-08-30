package io.openbas.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.openbas.database.audit.ModelBaseListener;
import io.openbas.helper.MonoIdDeserializer;
import io.openbas.helper.MultiIdListDeserializer;
import jakarta.persistence.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.time.Instant.now;

@Setter
@Entity
@Table(name = "communications")
@EntityListeners(ModelBaseListener.class)
public class Communication implements Base {

    @Id
    @Column(name = "communication_id")
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @JsonProperty("communication_id")
    @NotBlank
    private String id;

    @Column(name = "communication_message_id")
    @JsonProperty("communication_message_id")
    @NotBlank
    private String identifier;

    @Column(name = "communication_received_at")
    @JsonProperty("communication_received_at")
    @NotNull
    private Instant receivedAt = now();

    @Column(name = "communication_sent_at")
    @JsonProperty("communication_sent_at")
    @NotNull
    private Instant sentAt = now();

    @Column(name = "communication_subject")
    @JsonProperty("communication_subject")
    private String subject;

    @Column(name = "communication_content")
    @JsonProperty("communication_content")
    private String content;

    @Column(name = "communication_content_html")
    @JsonProperty("communication_content_html")
    private String contentHtml;

    @Column(name = "communication_attachments", columnDefinition = "text[]")
    @JsonProperty("communication_attachments")
    private String[] attachments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "communication_inject")
    @JsonSerialize(using = MonoIdDeserializer.class)
    @JsonProperty("communication_inject")
    private Inject inject;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "communications_users",
            joinColumns = @JoinColumn(name = "communication_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonSerialize(using = MultiIdListDeserializer.class)
    @JsonProperty("communication_users")
    private List<User> users = new ArrayList<>();

    @Column(name = "communication_ack")
    @JsonProperty("communication_ack")
    private Boolean ack = false;

    @Column(name = "communication_animation")
    @JsonProperty("communication_animation")
    private Boolean animation = false;

    @Column(name = "communication_from")
    @JsonProperty("communication_from")
    @NotBlank
    private String from;

    @Column(name = "communication_to")
    @JsonProperty("communication_to")
    @NotBlank
    private String to;

    @Override
    public String getId() {
        return id;
    }

  public Instant getReceivedAt() {
        return receivedAt;
    }

  public Instant getSentAt() {
        return sentAt;
    }

  public String getSubject() {
        return subject;
    }

  public String getContent() {
        return content;
    }

  public String getContentHtml() {
        return contentHtml;
    }

  public Inject getInject() {
        return inject;
    }

  public List<User> getUsers() {
        return users;
    }

  public String getIdentifier() {
        return identifier;
    }

  public Boolean getAck() {
        return ack;
    }

  public Boolean getAnimation() {
        return animation;
    }

  public String getFrom() {
        return from;
    }

  public String getTo() {
        return to;
    }

  public String[] getAttachments() {
        return attachments;
    }

  @JsonProperty("communication_exercise")
    public String getExercise() {
        return this.inject.getExercise() != null ? this.inject.getExercise().getId() : StringUtils.EMPTY;
    }

    @JsonIgnore
    @Override
    public boolean isUserHasAccess(User user) {
        Inject inject = getInject();
        return user.isAdmin() || getUsers().contains(user) || (inject != null && inject.isUserHasAccess(user));
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
