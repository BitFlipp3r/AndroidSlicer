package org.unibremen.mcyl.androidslicer.web.rest.vm;

/**
 * View Model object for storing android versions.
 */
public class AndroidVersionVM {

    private Integer version;

    private String path;

    public Integer getVersion() {
        return version;
    }

    public void setKey(Integer version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}