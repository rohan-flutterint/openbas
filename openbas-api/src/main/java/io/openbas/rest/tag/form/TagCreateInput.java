package io.openbas.rest.tag.form;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

import static io.openbas.config.AppConfig.MANDATORY_MESSAGE;

public class TagCreateInput {

    @NotBlank(message = MANDATORY_MESSAGE)
    @JsonProperty("tag_name")
    private String name;

    @NotBlank(message = MANDATORY_MESSAGE)
    @JsonProperty("tag_color")
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}