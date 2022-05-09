package com.cegal.twitch.service;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwitchDispatch {

  @Value("${azure.service.bus.connection}")
  private String azureServiceBusConnection;

  @Value("${azure.service.bus.queue}")
  private String azureServiceBusQueue;

  @Value("${azure.service.bus.subscription}")
  private String azureServiceBusSubscription;

  public int sceneChange(String sceneName) {
    ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
            .connectionString(azureServiceBusConnection)
            .sender()
            .queueName(azureServiceBusQueue)
            .buildClient();

    // send one message to the queue
    senderClient.sendMessage(new ServiceBusMessage(sceneName));
    System.out.println("Sent a single message to the queue: " + azureServiceBusQueue);

    return 1;
  }
}

