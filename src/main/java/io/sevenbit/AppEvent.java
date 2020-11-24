package io.sevenbit;

/**
 * Business event
 */
public class AppEvent {
    private String value;
    private long id;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AppEvent{" +
                "value='" + value + '\'' +
                ", id=" + id +
                '}';
    }
}
