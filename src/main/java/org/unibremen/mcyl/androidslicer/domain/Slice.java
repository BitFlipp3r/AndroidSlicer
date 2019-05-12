package org.unibremen.mcyl.androidslicer.domain;


import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Slice.
 */
@Document(collection = "slice")
public class Slice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("android_version")
    private Integer androidVersion;

    @NotNull
    @Field("android_class_name")
    private String androidClassName;

    /**
     * JSON-List with entry method names
     */
    
    @ApiModelProperty(value = "JSON-List with entry method names", required = true)
    @Field("entry_methods")
    private String entryMethods;

    /**
     * JSON-List with seed statement names
     */
    
    @ApiModelProperty(value = "JSON-List with seed statement names", required = true)
    @Field("seed_statements")
    private String seedStatements;

    @Field("slice")
    private String slice;

    @Field("log")
    private String log;

    @Field("thread_id")
    private String threadId;

    @Field("running")
    private Boolean running;

    /**
     * SlicerOption must have type ReflectionOptions
     */
    @ApiModelProperty(value = "SlicerOption must have type ReflectionOptions")
    @DBRef
    @Field("reflectionOptions")
    private SlicerOption reflectionOptions;

    /**
     * SlicerOption must have type ReflectionOptions
     */
    @ApiModelProperty(value = "SlicerOption must have type ReflectionOptions")
    @DBRef
    @Field("dataDependenceOptions")
    private SlicerOption dataDependenceOptions;

    /**
     * SlicerOption must have type ReflectionOptions
     */
    @ApiModelProperty(value = "SlicerOption must have type ReflectionOptions")
    @DBRef
    @Field("controlDependenceOptions")
    private SlicerOption controlDependenceOptions;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAndroidVersion() {
        return androidVersion;
    }

    public Slice androidVersion(Integer androidVersion) {
        this.androidVersion = androidVersion;
        return this;
    }

    public void setAndroidVersion(Integer androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getAndroidClassName() {
        return androidClassName;
    }

    public Slice androidClassName(String androidClassName) {
        this.androidClassName = androidClassName;
        return this;
    }

    public void setAndroidClassName(String androidClassName) {
        this.androidClassName = androidClassName;
    }

    public String getEntryMethods() {
        return entryMethods;
    }

    public Slice entryMethods(String entryMethods) {
        this.entryMethods = entryMethods;
        return this;
    }

    public void setEntryMethods(String entryMethods) {
        this.entryMethods = entryMethods;
    }

    public String getSeedStatements() {
        return seedStatements;
    }

    public Slice seedStatements(String seedStatements) {
        this.seedStatements = seedStatements;
        return this;
    }

    public void setSeedStatements(String seedStatements) {
        this.seedStatements = seedStatements;
    }

    public String getSlice() {
        return slice;
    }

    public Slice slice(String slice) {
        this.slice = slice;
        return this;
    }

    public void setSlice(String slice) {
        this.slice = slice;
    }

    public String getLog() {
        return log;
    }

    public Slice log(String log) {
        this.log = log;
        return this;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getThreadId() {
        return threadId;
    }

    public Slice threadId(String threadId) {
        this.threadId = threadId;
        return this;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public Boolean isRunning() {
        return running;
    }

    public Slice running(Boolean running) {
        this.running = running;
        return this;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public SlicerOption getReflectionOptions() {
        return reflectionOptions;
    }

    public Slice reflectionOptions(SlicerOption slicerOption) {
        this.reflectionOptions = slicerOption;
        return this;
    }

    public void setReflectionOptions(SlicerOption slicerOption) {
        this.reflectionOptions = slicerOption;
    }

    public SlicerOption getDataDependenceOptions() {
        return dataDependenceOptions;
    }

    public Slice dataDependenceOptions(SlicerOption slicerOption) {
        this.dataDependenceOptions = slicerOption;
        return this;
    }

    public void setDataDependenceOptions(SlicerOption slicerOption) {
        this.dataDependenceOptions = slicerOption;
    }

    public SlicerOption getControlDependenceOptions() {
        return controlDependenceOptions;
    }

    public Slice controlDependenceOptions(SlicerOption slicerOption) {
        this.controlDependenceOptions = slicerOption;
        return this;
    }

    public void setControlDependenceOptions(SlicerOption slicerOption) {
        this.controlDependenceOptions = slicerOption;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Slice)) {
            return false;
        }
        return id != null && id.equals(((Slice) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Slice{" +
            "id=" + getId() +
            ", androidVersion=" + getAndroidVersion() +
            ", androidClassName='" + getAndroidClassName() + "'" +
            ", entryMethods='" + getEntryMethods() + "'" +
            ", seedStatements='" + getSeedStatements() + "'" +
            ", slice='" + getSlice() + "'" +
            ", log='" + getLog() + "'" +
            ", threadId='" + getThreadId() + "'" +
            ", running='" + isRunning() + "'" +
            "}";
    }
}
