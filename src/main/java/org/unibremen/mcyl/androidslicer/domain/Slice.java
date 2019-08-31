package org.unibremen.mcyl.androidslicer.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.unibremen.mcyl.androidslicer.domain.enumeration.CFAType;

import io.swagger.annotations.ApiModelProperty;

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
    private Set<String> entryMethods = new HashSet<String>();

    /**
     * JSON-List with seed statement names
     */

    @ApiModelProperty(value = "JSON-List with seed statement names", required = true)
    @Field("seed_statements")
    private Set<String> seedStatements = new HashSet<String>();

    @Field("sliced_classes")
    private Set<SlicedClass> slicedClasses = new HashSet<SlicedClass>();

    @Field("log")
    private String log;

    @Field("thread_id")
    private String threadId;

    @Field("running")
    private Boolean running;

    @NotNull
    @Field("cfa_option_type")
    private CFAType cfaType;

    @Field("cfa_option_level")
    private Integer cfaLevel;

    /**
     * com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions
     */
    @NotNull
    @ApiModelProperty(value = "com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions", required = true)
    @Field("reflection_options")
    private ReflectionOptions reflectionOptions;

    /**
     * com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions
     */
    @NotNull
    @ApiModelProperty(value = "com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions", required = true)
    @Field("data_dependence_options")
    private DataDependenceOptions dataDependenceOptions;

    /**
     * com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions
     */
    @NotNull
    @ApiModelProperty(value = "com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions", required = true)
    @Field("control_dependence_options")
    private ControlDependenceOptions controlDependenceOptions;

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

    public Set<String> getEntryMethods() {
        return entryMethods;
    }

    public Slice entryMethods(Set<String> entryMethods) {
        this.entryMethods = entryMethods;
        return this;
    }

    public void setEntryMethods(Set<String> entryMethods) {
        this.entryMethods = entryMethods;
    }

    public Set<String> getSeedStatements() {
        return seedStatements;
    }

    public Slice seedStatements(Set<String> seedStatements) {
        this.seedStatements = seedStatements;
        return this;
    }

    public void setSeedStatements(Set<String> seedStatements) {
        this.seedStatements = seedStatements;
    }

    public Set<SlicedClass> getSlicedClasses() {
        return slicedClasses;
    }

    public Slice slicedClasses(Set<SlicedClass> slicedClasses) {
        this.slicedClasses = slicedClasses;
        return this;
    }

    public void setSlicedClasses(Set<SlicedClass> slicedClasses) {
        this.slicedClasses = slicedClasses;
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

    public CFAType getCfaType() {
        return cfaType;
    }

    public Slice cfaType(CFAType cfaType) {
        this.cfaType = cfaType;
        return this;
    }

    public void setCfaType(CFAType cfaType) {
        this.cfaType = cfaType;
    }

    public Integer getcfaLevel() {
        return cfaLevel;
    }

    public Slice cfaLevel(Integer cfaLevel) {
        this.cfaLevel = cfaLevel;
        return this;
    }
    public ReflectionOptions getReflectionOptions() {
        return reflectionOptions;
    }

    public Slice reflectionOptions(ReflectionOptions reflectionOptions) {
        this.reflectionOptions = reflectionOptions;
        return this;
    }

    public void setReflectionOptions(ReflectionOptions reflectionOptions) {
        this.reflectionOptions = reflectionOptions;
    }

    public DataDependenceOptions getDataDependenceOptions() {
        return dataDependenceOptions;
    }

    public Slice dataDependenceOptions(DataDependenceOptions dataDependenceOptions) {
        this.dataDependenceOptions = dataDependenceOptions;
        return this;
    }

    public void setDataDependenceOptions(DataDependenceOptions dataDependenceOptions) {
        this.dataDependenceOptions = dataDependenceOptions;
    }

    public ControlDependenceOptions getControlDependenceOptions() {
        return controlDependenceOptions;
    }

    public Slice controlDependenceOptions(ControlDependenceOptions controlDependenceOptions) {
        this.controlDependenceOptions = controlDependenceOptions;
        return this;
    }

    public void setControlDependenceOptions(ControlDependenceOptions controlDependenceOptions) {
        this.controlDependenceOptions = controlDependenceOptions;
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
            ", slice='" + getSlicedClasses() + "'" +
            ", log='" + getLog() + "'" +
            ", threadId='" + getThreadId() + "'" +
            ", running='" + isRunning() + "'" +
            ", cfaType='" + getCfaType() + "'" +
            ", cfaLevel=" + getcfaLevel() +
            ", reflectionOptions='" + getReflectionOptions() + "'" +
            ", dataDependenceOptions='" + getDataDependenceOptions() + "'" +
            ", controlDependenceOptions='" + getControlDependenceOptions() + "'" +
            "}";
    }
}
