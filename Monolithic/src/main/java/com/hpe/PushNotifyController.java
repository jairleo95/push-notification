/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hpe;

import java.io.IOException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author santjair
 */
@RestController
public class PushNotifyController {
    private final Logger log = LogManager.getLogger(getClass().getName());
    @RequestMapping(value = "pushNotify", method = {RequestMethod.POST},    
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String pushNotify(@RequestBody String request) throws IOException {
        SocketHandler s = new SocketHandler();
        this.log.info("Request received : " + request);
        JsonObject json = new Gson().fromJson(request, JsonObject.class);
        JsonObject data = json.getAsJsonObject("data");
        String sessionId = (json.getAsJsonPrimitive("sessionId").getAsString());
        Message m = new Message(json.getAsJsonPrimitive("title").getAsString(), json.getAsJsonPrimitive("content").getAsString(), sessionId, json.getAsJsonPrimitive("action").getAsString(), data);
        s.uniCast(m);
        String response = new Gson().toJson(m);
        this.log.info("Request processed successfully. Sending response : " + response);
        System.out.println("Request processed successfully. Sending response : " + response);
        return response;
    }
}
