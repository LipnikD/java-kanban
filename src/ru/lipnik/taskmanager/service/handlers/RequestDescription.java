package ru.lipnik.taskmanager.service.handlers;

public class RequestDescription {
    private final Endpoint endpoint;
    private final int id;

    public RequestDescription() {
        this.endpoint = Endpoint.UNKNOWN;
        this.id = 0;
    }

    public RequestDescription(Endpoint endpoint) {
        this.endpoint = endpoint;
        this.id = 0;
    }

    public RequestDescription(Endpoint endpoint, int id) {
        this.endpoint = endpoint;
        this.id = id;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public int getId() {
        return id;
    }
}
