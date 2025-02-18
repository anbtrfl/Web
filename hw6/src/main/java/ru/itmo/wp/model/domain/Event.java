package ru.itmo.wp.model.domain;

import java.util.Date;

public class Event {
    private Long userId;
    private Type type;
    private Date creationTime;
    private Long id;

    public Event() {
    }

    public Event(Long id, Long userId, Type type, Date creationTime) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.creationTime = creationTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
