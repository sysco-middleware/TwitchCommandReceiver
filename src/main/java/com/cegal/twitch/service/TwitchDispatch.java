package com.cegal.twitch.service;

import com.azure.messaging.servicebus.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class TwitchDispatch {

  @Value("${azure.service.bus.connection}")
  private String azureServiceBusConnection;

  @Value("${azure.service.bus.queue}")
  private String azureServiceBusQueue;

  @Value("${azure.service.bus.responseQueue}")
  private String azureServiceBusResponseQueue;

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

  // handles received messages
  public void receiveMessages() throws InterruptedException
  {
    CountDownLatch countdownLatch = new CountDownLatch(1);

    // Create an instance of the processor through the ServiceBusClientBuilder
    ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
            .connectionString(azureServiceBusConnection)
            .processor()
            .queueName(azureServiceBusResponseQueue)
            .processMessage(this::processMessage)
            .processError(context -> processError(context, countdownLatch))
            .buildProcessorClient();

    System.out.println("Starting the processor");
    processorClient.start();

    TimeUnit.SECONDS.sleep(10);
    System.out.println("Stopping and closing the processor");
    processorClient.close();
  }

  private ServiceBusReceivedMessage message;

  public ServiceBusReceivedMessage getMessage() {
    return message;
  }

  private void processMessage(ServiceBusReceivedMessageContext context) {
    message = context.getMessage();

    System.out.printf("Processing message. Session: %s, Sequence #: %s. Contents: %s%n", message.getMessageId(),
            message.getSequenceNumber(), message.getBody());
  }

  private void processError(ServiceBusErrorContext context, CountDownLatch countdownLatch) {
    System.err.printf("Error when receiving messages from namespace: '%s'. Entity: '%s'%n",
            context.getFullyQualifiedNamespace(), context.getEntityPath());

    if (!(context.getException() instanceof ServiceBusException)) {
      System.err.printf("Non-ServiceBusException occurred: %s%n", context.getException());
      return;
    }

    ServiceBusException exception = (ServiceBusException) context.getException();
    ServiceBusFailureReason reason = exception.getReason();

    if (reason == ServiceBusFailureReason.MESSAGING_ENTITY_DISABLED
            || reason == ServiceBusFailureReason.MESSAGING_ENTITY_NOT_FOUND
            || reason == ServiceBusFailureReason.UNAUTHORIZED) {
      System.err.printf("An unrecoverable error occurred. Stopping processing with reason %s: %s%n",
              reason, exception.getMessage());

      countdownLatch.countDown();
    } else if (reason == ServiceBusFailureReason.MESSAGE_LOCK_LOST) {
      System.err.printf("Message lock lost for message: %s%n", context.getException());
    } else if (reason == ServiceBusFailureReason.SERVICE_BUSY) {
      try {
        // Choosing an arbitrary amount of time to wait until trying again.
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        System.err.println("Unable to sleep for period of time");
      }
    } else {
      System.err.printf("Error source %s, reason %s, message: %s%n", context.getErrorSource(),
              reason, context.getException());
    }
  }
}

