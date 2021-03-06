package com.kido.sdk.kdmonitor.model;

/**
 * 异常信息
 */
public class ExceptionInfo {
    private String phoneModel;
    private String systemModel;
    private String systemVersion;
    private String exceptionString;


    public ExceptionInfo() {

    }

    public ExceptionInfo(String phoneModel, String systemModel, String systemVersion, String exceptionString) {
        this.phoneModel = phoneModel;
        this.systemModel = systemModel;
        this.systemVersion = systemVersion;
        this.exceptionString = exceptionString;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getSystemModel() {
        return systemModel;
    }

    public void setSystemModel(String systemModel) {
        this.systemModel = systemModel;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getExceptionString() {
        return exceptionString;
    }

    public void setExceptionString(String exceptionString) {
        this.exceptionString = exceptionString;
    }
}
