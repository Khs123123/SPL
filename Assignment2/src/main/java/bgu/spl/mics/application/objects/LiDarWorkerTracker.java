package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.util.*;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    // Fields
    private final int id; // Unique ID for the LiDAR worker
    private final int frequency; // Time interval for processing events
    private STATUS status; // Current operational status
    private List<TrackedObject> lastTrackedObjects; // Last tracked objects
    private Queue<DetectObjectsEvent> pendingEvents; // Queue of events awaiting processing
    private int lidarclock=-1;
    LiDarDataBase liDarDataBase;
    private int remainingObjectsToTrack;// =LiDarDataBase.getInstance().getdatasize();
    private int finaltime=-1;




    public LiDarWorkerTracker(int id, int frequency , String path) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP; // Default status
        this.lastTrackedObjects = new ArrayList<>();
        this.pendingEvents = new LinkedList<>();
        liDarDataBase=LiDarDataBase.getInstance(path);
        settotalnumberoftrackedobjects ();

    }

    private void settotalnumberoftrackedobjects () {
        List<StampedCloudPoints> list= liDarDataBase.getCloudPoints();
        for (StampedCloudPoints stmp : list){
            remainingObjectsToTrack++;

        }

    }



    public void setLidarclockClock(int currTime) {
        lidarclock = currTime;
    }

    public void isLidarWorking () {
        ArrayList<StampedCloudPoints> objectstrackedAtthisTime = getobjectstrackedAtthisTime();
        if(objectstrackedAtthisTime !=null && !objectstrackedAtthisTime.isEmpty()) {
            for (StampedCloudPoints stamped : objectstrackedAtthisTime) {
                if (Objects.equals(stamped.getId(), "ERROR")) setStatus(STATUS.ERROR);
                return;
            }
        }

    }

    private ArrayList<StampedCloudPoints> getobjectstrackedAtthisTime() {
        ArrayList<StampedCloudPoints> list =liDarDataBase.getCloudPointsbyTime(lidarclock);
        return list;
        }


    public boolean finishedtracking(){
        return remainingObjectsToTrack == 0;

    }

    public int returnfinaltime (){ return finaltime;}


    public void addToPendingEvents(DetectObjectsEvent event) {
        pendingEvents.add(event);
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {return status; }

    public void setStatus(STATUS status) {this.status = status;}

    public List<TrackedObject> getLastTrackedObjects() {return lastTrackedObjects;}



    public TrackedObjectsEvent processEvent(DetectObjectsEvent event, int currentTick,int detectionTime) {
        // Fetch cloud points for each detected object
        List<TrackedObject> trackedObjects = new ArrayList<>();
        for (DetectedObject detected : event.getStampedDetectedObjects().getDetectedObjects()) {
            String discription= detected.getDescription();
            if(detected.getId()=="ERROR") {
                setStatus(STATUS.ERROR);
                return null;
            }
            int tractionTime= liDarDataBase.getCloudPointsbyID(detected.getId(),detectionTime).getTime();

            StampedCloudPoints cloudPoints = liDarDataBase.getCloudPointsbyID(detected.getId(),detectionTime);
            if (cloudPoints != null) {
                remainingObjectsToTrack--;
                List<CloudPoint> points = cloudPoints.getCloudPoints();
                TrackedObject trackedObject = new TrackedObject(
                        detected.getId(),
                        tractionTime,
                        discription,
                        points
                );



                trackedObjects.add(trackedObject);
            }
        }

        // Update lastTrackedObjects
        lastTrackedObjects = trackedObjects;

        // Create and return a TrackedObjectsEvent
        return new TrackedObjectsEvent(trackedObjects);
    }



    public List<TrackedObjectsEvent> processPendingEvents(int currentTick) {
        List<TrackedObjectsEvent> processedEvents = new ArrayList<>();
        Iterator<DetectObjectsEvent> iterator = pendingEvents.iterator();

        while (iterator.hasNext()) {
            DetectObjectsEvent pendingEvent = iterator.next();
            int detectTime= pendingEvent.getStampedDetectedObjects().getTime();
            if (currentTick >= detectTime + frequency) {
                TrackedObjectsEvent trackedEvent = processEvent(pendingEvent, currentTick,detectTime);
                if (trackedEvent != null) {
                    processedEvents.add(trackedEvent);
                }
                iterator.remove(); // Remove from queue after processing
            }
        }
        return processedEvents;
    }

}
