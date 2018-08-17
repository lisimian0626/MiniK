package com.beidousat.libbns.evenbus;

public class BusEvent {

    public int id;
    public Object data;


    public static BusEvent getEvent(int id) {
        return new BusEvent(id);
    }

    public static BusEvent getEvent(int id, Object data) {
        return new BusEvent(id, data);
    }

    protected BusEvent() {
    }

    protected BusEvent(int id) {
        this.id = id;
    }

    protected BusEvent(int id, Object data) {
        this.id = id;
        this.data = data;
    }

}
