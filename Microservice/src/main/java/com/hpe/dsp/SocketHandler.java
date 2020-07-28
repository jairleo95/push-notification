package com.hpe.dsp;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketHandler extends TextWebSocketHandler {
    private final Logger log = LogManager.getLogger(getClass().getName());
    private static final Set<SocketHandler> connections2 = new CopyOnWriteArraySet<SocketHandler>();
    private String sessionId;
    private WebSocketSession session;
    /*actions*/
    public static final String NOTIFY="notify";
    public static final  String ADD_USER_ATTRIBUTES="setUserAttributes";

      public boolean uniCast(Message message) {
        Boolean isMessageSent = Boolean.FALSE;
        try{
            for (SocketHandler entry : connections2) {
                try {
                    synchronized (entry) {
                        if (entry.sessionId.equals(message.getSessionId())) {
                            if (entry.session.isOpen()){
                                entry.session.sendMessage(new TextMessage(new Gson().toJson(message)));
                                this.log.info("uniCast - Message unicast finished for user[" + entry.sessionId + "]");
                                isMessageSent = Boolean.TRUE;
                            }else{
                                this.log.info("uniCast - Connection closed for user[" + entry.sessionId + "]");
                            }
                        }
                    }
                } catch (IOException e) {
                    this.log.error("uniCast - Error: Failed to send message to client: ", e);
                    connections2.remove(entry);
                    try {
                        entry.session.close();
                    } catch (IOException e1) {
                        // Ignore
                    }
                    this.log.info("uniCast - User[" + entry.sessionId + "] has been disconnected.");
                }
            }
            if (!isMessageSent.booleanValue()) {
                this.log.info("uniCast - Message could not be notified. User[" + message.getSessionId() + "] is not available.");
            }
        } catch (JsonSyntaxException e){
            this.log.error("uniCast - Error:" + e.getMessage());
        }
        return true;
    }
    public SocketHandler(String sessionId,WebSocketSession session) {
        this.sessionId = sessionId;
        this.session = session;
    }
    public SocketHandler() {
        this.sessionId = "";
        this.session = null;
    }
  public void afterConnectionEstablished(WebSocketSession session){
    this.log.info("afterConnectionEstablished - A new Websocket connection established.");
  }
  public void handleTextMessage(WebSocketSession session, TextMessage textMessage){
    Message message;
    try{
      message =  new Gson().fromJson(textMessage.getPayload(), Message.class);
      if (message.getAction().equals(NOTIFY)) {
          uniCast(message);
      } else if (message.getAction().equals(ADD_USER_ATTRIBUTES)) {
          connections2.add(new SocketHandler(message.getSessionId(),session));
          this.log.info("Attributes correctly added for user[" + message.getSessionId() + "]");
          this.log.info("handleTextMessage - Total connections "+connections2.size());
      }
    }
    catch (JsonSyntaxException e) {
        this.log.error("handleTextMessage - Error:" + e.getMessage());
    }
  }

  public void afterConnectionClosed(WebSocketSession wsSession, CloseStatus status) {
      this.log.info("afterConnectionClosed - reason:" +status.getReason());
      for (SocketHandler entry : connections2) {
          synchronized (entry) {
              if (entry.session.equals(wsSession)){
                  this.log.info("afterConnectionClosed - Removing connections from Collection by user ["+entry.sessionId+"].");
                  connections2.remove(entry);
                  this.log.info("afterConnectionClosed - Connections size:" + connections2.size());
              }
          }
      }

  }
  public void handleTransportError(WebSocketSession wsSession, Throwable exception){
    this.log.info("handleTransportError - " + exception.getMessage());
  }
}
