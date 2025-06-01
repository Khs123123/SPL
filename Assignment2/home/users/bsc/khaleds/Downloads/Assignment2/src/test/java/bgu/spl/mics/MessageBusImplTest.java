package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest2 {

    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;

    // Concrete event and broadcast classes for testing
    private static class TestEvent implements Event<String> {}
    private static class TestBroadcast implements Broadcast {}

    private TestEvent testEvent;
    private TestBroadcast testBroadcast;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance(); // @inv: Singleton instance must be created before tests.
        microService1 = new MicroService("TestMicroService1") {
            @Override
            protected void initialize() {
            }
        };
        microService2 = new MicroService("TestMicroService2") {
            @Override
            protected void initialize() {
            }
        };
        testEvent = new TestEvent();
        testBroadcast = new TestBroadcast();
    }

    @AfterEach
    void tearDown() {
        messageBus.unregister(microService1);
        messageBus.unregister(microService2);
    }

    @Test
    void getInstance() {
        // @post: The method should always return the same instance.
        MessageBusImpl instance1 = MessageBusImpl.getInstance();
        MessageBusImpl instance2 = MessageBusImpl.getInstance();

        // @inv: The instance must not be null and must be the same.
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }



    @Test
    void subscribeEvent() {
        // @param: type - Event type to subscribe to.
        // @param: m - Microservice subscribing to the event.
        // @post: The MicroService is added to the subscription queue for the specified event type.
        messageBus.register(microService1);
        messageBus.subscribeEvent(TestEvent.class, microService1);
        assertTrue(messageBus.getEventsSubs().get(TestEvent.class).contains(microService1));

        // @inv: The subscription queue should not be null or inconsistent.
        assertNotNull(messageBus.getEventsSubs().get(TestEvent.class));
        assertEquals(1, messageBus.getEventsSubs().get(TestEvent.class).size());
    }

    @Test
    void subscribeBroadcast() {
        // @param: type - Broadcast type to subscribe to.
        // @param: m - Microservice subscribing to the broadcast.
        // @post: The MicroService is added to the subscription queue for the specified broadcast type.
        messageBus.register(microService1);
        messageBus.subscribeBroadcast(TestBroadcast.class, microService1);
        assertTrue(messageBus.getBroadSubs().get(TestBroadcast.class).contains(microService1));
    }

    @Test
    void complete() {
        // @param: e - Event to complete.
        // @param: result - Result to resolve the future with.
        // @post: The future associated with the event is resolved with the provided result.
        messageBus.register(microService1);
        messageBus.subscribeEvent(TestEvent.class, microService1);
        Future<String> future = messageBus.sendEvent(testEvent);
        messageBus.complete(testEvent, "Completed");

        // @post: The future should be resolved with the provided result.
        assertTrue(future.isDone());
        assertEquals("Completed", future.get());

        // @inv: The future's state must remain consistent after resolution.
        assertNotNull(future);
        assertEquals("Completed", future.get());
    }

    @Test
    void sendBroadcast() {
        // @param: b - Broadcast message to send.
        // @post: All subscribed MicroServices receive the broadcast in their message queues.
        messageBus.register(microService1);
        messageBus.subscribeBroadcast(TestBroadcast.class, microService1);
        messageBus.sendBroadcast(testBroadcast);

        try {
            assertEquals(testBroadcast, messageBus.awaitMessage(microService1));
        } catch (InterruptedException e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    void sendEvent() {
        // @param: e - Event to send.
        // @post: The event is placed in the message queue of a subscribed MicroService.
        messageBus.register(microService1);
        messageBus.subscribeEvent(TestEvent.class, microService1);
        Future<String> future = messageBus.sendEvent(testEvent);

        try {
            Message message = messageBus.awaitMessage(microService1);
            assertEquals(testEvent, message);
        } catch (InterruptedException e) {
            fail("Exception should not be thrown");
        }

        // The returned future must not be null.
        assertNotNull(future);

        // @inv: The event subscription structure should remain consistent.
        assertTrue(messageBus.getEventsSubs().get(TestEvent.class).contains(microService1));
    }

    @Test
    void register() {
        // @param: m - Microservice to register.
        // @post: The MicroService is added to the MessageBus's internal structures.
        messageBus.register(microService1);
        assertNotNull(messageBus.getMicroServicesMsgs().get(microService1));
    }

    @Test
    void unregister() {
        // @param: m - Microservice to unregister.
        // @post: The MicroService is removed from the MessageBus's internal structures.
        messageBus.register(microService1);
        messageBus.unregister(microService1);
        assertNull(messageBus.getMicroServicesMsgs().get(microService1));
    }

    @Test
    void awaitMessage() {
        // @param: m - MicroService waiting for a message.
        // @post: Returns the first message from the MicroService's queue.
        messageBus.register(microService1);
        messageBus.subscribeEvent(TestEvent.class, microService1);
        messageBus.sendEvent(testEvent);

        try {
            Message message = messageBus.awaitMessage(microService1);
            assertEquals(testEvent, message);
        } catch (InterruptedException e) {
            fail("Exception should not be thrown");
        }

        // @inv: The queue for the microservice should not contain the message after it's retrieved.
        assertTrue(messageBus.getMicroServicesMsgs().get(microService1).isEmpty());
    }
}
