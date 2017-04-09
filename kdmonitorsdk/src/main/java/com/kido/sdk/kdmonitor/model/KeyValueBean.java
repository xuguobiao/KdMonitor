package com.kido.sdk.kdmonitor.model;

/**
 * 业务对应的key-value
 */
public class KeyValueBean {
    private String name;
    private String value;

    public KeyValueBean() {
    }

    public KeyValueBean(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KeyValueBean{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
