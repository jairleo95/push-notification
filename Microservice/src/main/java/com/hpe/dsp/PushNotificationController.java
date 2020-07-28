/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hpe.dsp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PushNotificationController {
  private final Logger log = LogManager.getLogger(getClass().getName());
  
  @RequestMapping(value={"pushNotify"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, consumes={"application/json"}, produces={"application/json"})
  @ResponseBody
  public String pushNotifications(@RequestBody String request)  throws IOException {
    SocketHandler s = new SocketHandler();
    this.log.info("pushNotifications - Request received : " + request);
    //JsonObject json =new Gson().fromJson(request, JsonObject.class);
    // JsonObject data = json.getAsJsonObject("data");
    // String sessionId = json.getAsJsonPrimitive("sessionId").getAsString();
    // message = new Message(json.getAsJsonPrimitive("tittle").getAsString(), json.getAsJsonPrimitive("content").getAsString(), sessionId, json.getAsJsonPrimitive("action").getAsString(), data);
    Message message =  new Gson().fromJson(request, Message.class);
    s.uniCast(message);
    String response = new Gson().toJson(message);
    this.log.info("pushNotifications - Request processed successfully. Sending response : " + response);
    return response;
  }
}
