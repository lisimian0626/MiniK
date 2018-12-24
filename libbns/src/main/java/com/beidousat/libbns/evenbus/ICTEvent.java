package com.beidousat.libbns.evenbus;

public class ICTEvent {

    public int id;
    public Object data;

    private final int ACCEPT=1;
    private final int REJECT=2;

    public static ICTEvent getEvent(int id) {
        return new ICTEvent(id);
    }

    public static ICTEvent getEvent(int id, Object data) {
        return new ICTEvent(id, data);
    }

    protected ICTEvent() {
    }

    protected ICTEvent(int id) {
        this.id = id;
    }

    protected ICTEvent(int id, Object data) {
        this.id = id;
        this.data = data;
    }

}
