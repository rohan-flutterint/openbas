package io.openbas.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.openbas.database.audit.ModelBaseListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

import static java.time.Instant.now;

@Getter
@Setter
@Entity
@Table(name = "executors")
@EntityListeners(ModelBaseListener.class)
public class Executor implements Base {

    @Id
    @Column(name = "executor_id")
    @JsonProperty("executor_id")
    @NotBlank
    private String id;

    @Column(name = "executor_name")
    @JsonProperty("executor_name")
    @NotBlank
    private String name;

    @Column(name = "executor_type")
    @JsonProperty("executor_type")
    @NotBlank
    private String type;

    @Getter
    @Column(name = "executor_platforms", columnDefinition = "text[]")
    @JsonProperty("executor_platforms")
    private String[] platforms = new String[0];

    @Column(name = "executor_doc")
    @JsonProperty("executor_doc")
    private String doc;

    @Column(name = "executor_created_at")
    @JsonProperty("executor_created_at")
    @NotNull
    private Instant createdAt = now();

    @Column(name = "executor_updated_at")
    @JsonProperty("executor_updated_at")
    @NotNull
    private Instant updatedAt = now();

    @JsonIgnore
    @Override
    public boolean isUserHasAccess(User user) {
        return user.isAdmin();
    }

    @Override
    public String toString() {
        return name;
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
