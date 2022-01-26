package com.griddynamics.pift.pojo;

public class ColumnProps {
    public String foreign_key;
    public String type;
    public String format;
    public Condition condition;

    @Override
    public String toString() {
        return "ColumnProps{" +
                "foreign_key='" + foreign_key + '\'' +
                ", type='" + type + '\'' +
                ", format='" + format + '\'' +
                ", condition=" + condition +
                '}';
    }
}
