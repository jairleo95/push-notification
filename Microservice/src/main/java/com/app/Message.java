package com.app;

import com.google.gson.JsonObject;

/**
 * Created by santjair on 6/13/2017.
 */
public class Message {
    private String tittle;
    private String content;
    private String sessionId;
    private String action;
    private JsonObject data;

    public Message(String tittle, String content, String sessionId, String action, JsonObject data) {
        this.tittle = tittle;
        this.content = content;
        this.sessionId = sessionId;
        this.action = action;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
