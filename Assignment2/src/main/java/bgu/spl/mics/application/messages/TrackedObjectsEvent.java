

package bgu.spl.mics.application.messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

/**
 * TrackedObjectsEvent represents an event sent by a LiDAR worker to the Fusion-SLAM service.
 * It provides precise positional data for tracked objects.
 */
public class TrackedObjectsEvent implements Event<Boolean> {

    // Fields
    private final List<TrackedObject> trackedObjects; // List of tracked objects
    private int tractiontime=0;

    /**
     * Constructor to initialize a TrackedObjectsEvent.
     *
     * @param trackedObjects The list of tracked objects with precise positional data.
     */
    public TrackedObjectsEvent(List<TrackedObject> trackedObjects) {
        this.trackedObjects = trackedObjects;

    }

    public int getTractiontime (){
        if (trackedObjects.isEmpty()) return -1;
        else {
            TrackedObject firstone= trackedObjects.get(0);
            return firstone.getTime();

        }


    }



    /**
     * Gets the list of tracked objects.
     *
     * @return The list of TrackedObject instances.
     */
    public List<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }

    /**
     * Returns a string representation of the TrackedObjectsEvent.
     *
     * @return A string containing the list of tracked objects.
     */
    @Override
    public String toString() {
        return "TrackedObjectsEvent{" +
                "trackedObjects=" + trackedObjects +
                '}';
    }
}

