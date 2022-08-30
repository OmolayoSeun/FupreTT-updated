package com.omolayoseun.fuprett;

public enum IntentKey {
    MESSAGE("MESSAGE"),
    COURSE_PATH("COURSE_PATH"),
    TIME_TABLE_PATH("TIME_TABLE_PATH"),
    DEPT("DEPT"),
    LEVEL("LEVEL"),
    SEMESTER("SEMESTER"),
    OK("OK");

    private final String value;

    IntentKey(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
