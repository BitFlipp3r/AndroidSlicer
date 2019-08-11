package org.unibremen.mcyl.androidslicer.domain;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.*;

import java.io.Serializable;

import org.unibremen.mcyl.androidslicer.domain.enumeration.CFAType;

/**
 * A CFAOption.
 */
@Document(collection = "cfa_option")
public class CFAOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("type")
    private CFAType type;

    @Field("description")
    private String description;

    /**
     * There can only be one default. This handled during create/save process.
     */
    @ApiModelProperty(value = "There can only be one default. This handled during create/save process.")
    @Field("is_default")
    private Boolean isDefault;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CFAType getType() {
        return type;
    }

    public CFAOption type(CFAType type) {
        this.type = type;
        return this;
    }

    public void setType(CFAType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public CFAOption description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public CFAOption isDefault(Boolean isDefault) {
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
        if (!(o instanceof CFAOption)) {
            return false;
        }
        return id != null && id.equals(((CFAOption) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "CFAOption{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", description='" + getDescription() + "'" +
            ", isDefault='" + getIsDefault() + "'" +
            "}";
    }
}
