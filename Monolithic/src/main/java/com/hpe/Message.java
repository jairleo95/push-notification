package com.hpe;

import com.google.gson.JsonObject;

/**
 * Created by santjair on 6/13/2017.
 */
public class Message {
    private String title;
    private String content;
    private String sessionId;
    private String action;
    private JsonObject data;

    public Message(String title, String content, String sessionId, String action, JsonObject data) {
        this.title = title;
        this.content = content;
        this.sessionId = sessionId;
        this.action = action;
        this.data = data;
    }
    public Message() {
        this.title = title;
        this.content = content;
        this.sessionId = sessionId;
        this.action = action;
        this.data = data;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
