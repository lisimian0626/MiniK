package com.beidousat.libbns.evenbus;

public class OctoEvent {

    public int id;
    public Object data;


    public static OctoEvent getEvent(int id) {
        return new OctoEvent(id);
    }

    public static OctoEvent getEvent(int id, Object data) {
        return new OctoEvent(id, data);
    }

    protected OctoEvent() {
    }

    protected OctoEvent(int id) {
        this.id = id;
    }

    protected OctoEvent(int id, Object data) {
        this.id = id;
        this.data = data;
    }

}
