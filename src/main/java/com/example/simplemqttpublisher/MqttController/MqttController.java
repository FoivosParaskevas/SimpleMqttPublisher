package com.example.simplemqttpublisher.MqttController;


import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class MqttController implements MqttCallback {
    public void connectionLost(Throwable t) {
        // code to reconnect to the broker would go here if desired
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
    }
    @PostMapping("/")
    @ResponseBody
    public ResponseEntity receivePostRequest(@RequestBody String message) {
        MqttClient myClient = null;
        MqttConnectOptions connOpt;
        String M2MIO_THING = UUID.randomUUID().toString();
        String BROKER_URL = "tcp://test.mosquitto.org:1883";
        Random rnd = new Random();
        Logger log = LoggerFactory.getLogger(MqttController.class);
        String TOPIC = "grupatras/lab/message";

        String clientID = M2MIO_THING;
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);
        try {
            myClient = new MqttClient(BROKER_URL, clientID);
            myClient.setCallback(this);
            myClient.connect(connOpt);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        log.info("Connected to " + BROKER_URL);
        String myTopic = TOPIC;
        MqttTopic topic = myClient.getTopic(myTopic);
        double temp = 80 + rnd.nextDouble() * 20.0;
        String val = message;
        String pubMsg = "{\"Message\":" + val + "}";
        int pubQoS = 0;
        MqttMessage msg = new MqttMessage(pubMsg.getBytes());
        msg.setQos(pubQoS);
        msg.setRetained(false);

        log.info("Publishing to topic \"" + topic + "\" qos " + pubQoS + "\" value " + val);
        MqttDeliveryToken token = null;
        try {
            token = topic.publish(msg);

            token.waitForCompletion();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            myClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}