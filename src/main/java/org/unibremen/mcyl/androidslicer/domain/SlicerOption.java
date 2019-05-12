package org.unibremen.mcyl.androidslicer.domain;


import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

import org.unibremen.mcyl.androidslicer.domain.enumeration.SlicerOptionType;

/**
 * A SlicerOption.
 */
@Document(collection = "slicer_option")
public class SlicerOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("type")
    private SlicerOptionType type;

    @NotNull
    @Field("key")
    private String key;

    @Field("description")
    private String description;

    /**
     * There can only be one default per SlicerOptionType. This handled during create/save process.
     */
    @ApiModelProperty(value = "There can only be one default per SlicerOptionType. This handled during create/save process.")
    @Field("is_default")
    private Boolean isDefault;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SlicerOptionType getType() {
        return type;
    }

    public SlicerOption type(SlicerOptionType type) {
        this.type = type;
        return this;
    }

    public void setType(SlicerOptionType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public SlicerOption key(String key) {
        this.key = key;
        return this;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public SlicerOption description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isIsDefault() {
        return isDefault;
    }

    public SlicerOption isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SlicerOption)) {
            return false;
        }
        return id != null && id.equals(((SlicerOption) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "SlicerOption{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", key='" + getKey() + "'" +
            ", description='" + getDescription() + "'" +
            ", isDefault='" + isIsDefault() + "'" +
            "}";
    }
}
