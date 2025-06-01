package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {

    private final AtomicInteger systemRuntime;
    private final AtomicInteger numDetectedObjects;
    private final AtomicInteger numTrackedObjects;
    private final AtomicInteger numLandmarks;

    public StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
    }

    //add to the field
    public void incrementSystemRuntime() {
        systemRuntime.incrementAndGet();
    }
    public void incrementNumDetectedObjects() {
        numDetectedObjects.incrementAndGet();
    }
    public void incrementNumTrackedObjects() {
        numTrackedObjects.incrementAndGet();
    }
    public void incrementNumTrackedObjects(int size) {
        numTrackedObjects.addAndGet(size);
    }
    public void incrementNumLandmarks() {
        numLandmarks.incrementAndGet();
    }
    // Add to the number of detected objects
    public void addDetectedObjects(int count) {
        numDetectedObjects.addAndGet(count);
    }

    // Add to the number of tracked objects
    public void addTrackedObjects(int count) {
        numTrackedObjects.addAndGet(count);
    }

    //Getters of the field
    public int getSystemRuntime() {
        return systemRuntime.get();
    }
    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }
    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }
    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    //json output
    // Method to retrieve statistics as a JSON-like structure
    public String getStatistics() {
        return String.format(
                "{ \"systemRuntime\": %d, \"numDetectedObjects\": %d, \"numTrackedObjects\": %d, \"numLandmarks\": %d }",
                getSystemRuntime(), getNumDetectedObjects(), getNumTrackedObjects(), getNumLandmarks()
        );
    }

}
