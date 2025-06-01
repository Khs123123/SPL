package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {


    private static MessageBusImpl instance = null;
    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microServicesMsgs = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Event, Future> FutureEvents = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> EventsSubs = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> BroadSubs = new ConcurrentHashMap<>();
    private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Event>>> MicrosEventsSubs = new ConcurrentHashMap<>();
    private ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Class<? extends Broadcast>>> MicrosBroadSubs = new ConcurrentHashMap<>();



    public static MessageBusImpl getInstance() {
        if (instance == null) {
            instance = new MessageBusImpl();
        }
        return instance;
    }




    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        EventsSubs.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        MicrosEventsSubs.putIfAbsent(m, new ConcurrentLinkedQueue<Class<? extends Event>>());
        synchronized (m) {
            ConcurrentLinkedQueue<Class<? extends Event>> q = MicrosEventsSubs.get(m);
            if (q != null)
                q.add(type);
        }
        synchronized (type) {
            ConcurrentLinkedQueue<MicroService> q2 = EventsSubs.get(type);
            if (q2 != null)
                q2.add(m);
        }

    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        BroadSubs.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        MicrosBroadSubs.putIfAbsent(m, new ConcurrentLinkedQueue<>());
        synchronized (m) {
            ConcurrentLinkedQueue<Class<? extends Broadcast>> q = MicrosBroadSubs.get(m);
            if (q != null)
                q.add(type);
        }
        synchronized (type) {
            ConcurrentLinkedQueue<MicroService> q2 = BroadSubs.get(type);
            if (q2 != null)
                q2.add(m);
        }

    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        FutureEvents.get(e).resolve(result);

    }

    @Override
    public void sendBroadcast(Broadcast b) {
        synchronized (b.getClass()) {
            if (BroadSubs.containsKey(b.getClass())) {
                ConcurrentLinkedQueue<MicroService> micro = BroadSubs.get(b.getClass());
                for (MicroService m : micro) {
                    LinkedBlockingQueue<Message> q = microServicesMsgs.get(m);
                    if (q != null) {
                        q.add(b);
                    }
                }
            }
        }

    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        MicroService m;
        Future<T> future = new Future<>();
        synchronized (e.getClass()) {
            if (EventsSubs.get(e.getClass()) == null || !EventsSubs.containsKey(e.getClass())) {
                return null;
            }
            FutureEvents.put(e, future);
            ConcurrentLinkedQueue<MicroService> queue = EventsSubs.get(e.getClass());
            if (queue == null) {//if no queue then no one has registered to it yet, or already unregistered
                return null;
            }
            m = queue.poll();
            if (m == null) {
                return null;
            }
            queue.add(m);
        }

        synchronized (m) {
            LinkedBlockingQueue<Message> q2 = microServicesMsgs.get(m);
            if (q2 == null) {
                return null;
            }
            q2.add(e);
        }
        return future;
    }

    @Override
    public void register(MicroService m) {
        microServicesMsgs.putIfAbsent(m, new LinkedBlockingQueue<>());


    }

    @Override
    public void unregister(MicroService m) {
        if (microServicesMsgs.containsKey(m)) {
            LinkedBlockingQueue<Message> q;

            if (MicrosEventsSubs.containsKey(m)) {
                ConcurrentLinkedQueue<Class<? extends Event>> q3 = MicrosEventsSubs.get(m);
                for (Class<? extends Event> type : q3) {
                    synchronized (type) {
                        EventsSubs.get(type).remove(m);
                    }
                }
                MicrosEventsSubs.remove(m);
            }
            synchronized (m) {
                if (MicrosBroadSubs.containsKey(m)) {
                    ConcurrentLinkedQueue<Class<? extends Broadcast>> q2 = MicrosBroadSubs.get(m);
                    for (Class<? extends Broadcast> type : q2) {
                        synchronized (type) {
                            BroadSubs.get(type).remove(m);
                        }
                    }
                    MicrosBroadSubs.remove(m);
                }
                q = microServicesMsgs.remove(m);
                if (q == null) {
                    return;
                }
            }
            while (!q.isEmpty()) {
                Message message = q.poll();
                if (message != null) {
                    Future<?> future = FutureEvents.get(message);
                    if (future != null) {
                        future.resolve(null);
                    }
                }
            }
        }


    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        LinkedBlockingQueue<Message> q = microServicesMsgs.get(m);
        if (q == null) {
            throw new IllegalArgumentException("MicroService is not registered");
        }
        Message msg = null;
        synchronized (q) {
            try {
                msg = q.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }


    /**
     * Getter for the BroadSubs map (for testing purposes).
     */


    public ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> getEventsSubs() {
        return EventsSubs;
    }

    /**
     * Getter for the BroadSubs map (for testing purposes).
     */
    public ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> getBroadSubs() {
        return BroadSubs;
    }

    /**
     * Getter for the microServicesMsgs map (for testing purposes).
     */
    public ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> getMicroServicesMsgs() {
        return microServicesMsgs;
    }

}
