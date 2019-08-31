package org.unibremen.mcyl.androidslicer.domain;

import java.io.Serializable;

public class SlicedClass implements Serializable {

    private static final long serialVersionUID = 1L;

    private String className;
    private String packagePath;
    private String code;

    public SlicedClass(String className, String packagePath, String code){
        this.className = className;
        this.packagePath = packagePath;
        this.code = code;
    }

    public String getClassName() {
        return className;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}