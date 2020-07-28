package com.hpe;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@ServerEndpoint(value = "/notify")
public class SocketHandler {

    private final Logger log = LogManager.getLogger(getClass().getName());
    private String sessionId;
    private Session session;
    private static final Set<SocketHandler> connections = new CopyOnWriteArraySet<SocketHandler>();

    public SocketHandler() {
        sessionId = "";
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

   public boolean uniCast(Message message) {
       Boolean isMessageSent = Boolean.FALSE;

       try{
           for (SocketHandler entry : connections) {
               try {
                   synchronized (entry) {
                       if (entry.sessionId.equals(message.getSessionId())) {
                           entry.session.getBasicRemote().sendText(new Gson().toJson(message));
                           this.log.info("Message unicast finished for user[" + entry.session + "]");
                           isMessageSent = Boolean.TRUE;
                       }
                   }
               } catch (IOException e) {
                   this.log.error("Error: Failed to send message to client: ", e);
                   connections.remove(entry);
                   try {
                       entry.session.close();
                   } catch (IOException e1) {
                       // Ignore
                   }
                   String s = String.format("* %s %s", entry.sessionId, "has been disconnected.");
                   this.log.info(s);
               }
           }
           if (!isMessageSent.booleanValue()) {
               this.log.info("Message could not be notified. User[" + message.getSessionId() + "] is not available.");
           }
       } catch (JsonSyntaxException e){
           this.log.error("Error:" + e.getMessage());
       }
        return true;
    }

    @OnOpen
    public void start(Session session) {
        this.log.info("Websocket connection established.");
        String msg = String.format("* %s %s", sessionId, "has joined.");
        this.log.info(msg);
    }

    @OnClose
    public void end() {
        this.log.info("Closing connection for user");
        connections.remove(this);
        String message = String.format("* %s %s", sessionId, "has disconnected.");
        this.log.info(message);
    }

    @OnMessage
    public void incoming(String msg, Session session) {
        Message message = new Gson().fromJson(msg, Message.class);
        if (message.getAction().equals("notify")) {
            uniCast(message);
        } else if (message.getAction().equals("setUserAttributes")) {
            this.log.info("Enter to setUserAttributes action");
            this.session = session;
            this.sessionId = message.getSessionId();
            log.info("sessionId:"+ this.sessionId);
            connections.add(this);
            //getConnectionBySession(session).sessionId=message.getSessionId();
        }
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        this.log.error("Error: " + t.toString(), t);
    }
}
