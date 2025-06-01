package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * DetectObjectsEvent represents an event triggered by the CameraService
 * to notify a LiDarWorkerService about detected objects.
 */
public class DetectObjectsEvent implements Event<Boolean> {

    private final StampedDetectedObjects stampedDetectedObjects;

    /**
     * Constructor to initialize a DetectObjectsEvent.
     *
     * @param stampedDetectedObjects The detected objects with a timestamp.
     */
    public DetectObjectsEvent(StampedDetectedObjects stampedDetectedObjects) {
        this.stampedDetectedObjects = stampedDetectedObjects;
    }

    /**
     * Gets the StampedDetectedObjects associated with this event.
     *
     * @return The StampedDetectedObjects instance.
     */
    public StampedDetectedObjects getStampedDetectedObjects() {
        return stampedDetectedObjects;
    }


    public  int detectiontime(){

        return stampedDetectedObjects.getTime();
    }

    @Override
    public String toString() {
        return "DetectObjectsEvent{" +
                "stampedDetectedObjects=" + stampedDetectedObjects +
                '}';
    }
}
