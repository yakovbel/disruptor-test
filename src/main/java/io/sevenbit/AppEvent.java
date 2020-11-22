package io.sevenbit;

public class AppEvent {
    private String value;

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "AppEvent{ " + "value='" + value + "' }";
    }
}
