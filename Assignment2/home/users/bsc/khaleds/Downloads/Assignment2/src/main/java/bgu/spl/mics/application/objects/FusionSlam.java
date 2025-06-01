package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FusionSlam {
   private  StatisticalFolder statisticalFolder;
    private final Map<String, LandMark> landmarks;
    private final List<Pose> poses;
    private final List<TrackedObjectsEvent> pendingTrackedObjects;
    //TODO:data to generate output file in case of crashing :
    private List <StampedDetectedObjects> camerasRecentData=new ArrayList<>(); // might be many cameras each object is list of last detected objects of some camera
    String recentLidar;
    Map<String,StampedDetectedObjects > lastCameraMap=new ConcurrentHashMap<>();
    Map<String,List<TrackedObject> > lastLiDarMap = new ConcurrentHashMap<>();

    public void setLastCameraMap(Map<String, StampedDetectedObjects> lastCameraMap) {
        this.lastCameraMap = lastCameraMap;
    }

    public String getRecentCamer() {
        return recentCamer;
    }

    public void setRecentCamer(String recentCamer) {
        this.recentCamer = recentCamer;
    }

    String recentCamer;

    public List<Pose> getRecentPoses() {
        return recentPoses;
    }

    public List<List<TrackedObject>> getLidarsRecentData() {
        return lidarsRecentData;
    }

    public List<StampedDetectedObjects> getCamerasRecentData() {
        return camerasRecentData;
    }

    private List<List<TrackedObject>>  lidarsRecentData=new ArrayList<>();// might be many lidars each object is list of the last tracked objects of somelidar

    public String getErrorDescription() {
        return errorDescription;
    }

    private List <Pose> recentPoses= new ArrayList<>();
    private String whoCrashed;

    public String getWhoCrashed() {
        return whoCrashed;
    }

    private String errorDescription;





    private FusionSlam() {
        statisticalFolder=new StatisticalFolder();
        this.landmarks = new HashMap<>();
        this.poses = new ArrayList<>();
        this.pendingTrackedObjects = new ArrayList<>();
    }


    public void setWhoCrashed(String whocrashed){whoCrashed=whocrashed;}
    public void setErrorDescription (String errordescription){errorDescription=errordescription;}
    public void addrecentposes (List <Pose> poses){this.recentPoses=poses;}
    public void addToCamerasRecentData (StampedDetectedObjects dataOfSomeCamera){camerasRecentData.add(dataOfSomeCamera);}
    public void addTolidarsRecentData (List<TrackedObject> dataofSomeLidar){
        lidarsRecentData=new ArrayList<>();
        lidarsRecentData.add(dataofSomeLidar);
    }


    public synchronized StatisticalFolder getStatisticalFolder() {
        return statisticalFolder;
    }

    public List<LandMark> getLandmarks() {
        return landmarks.values().stream().collect(Collectors.toList());
    }

    public void setLidarRecent(TrackedObjectsEvent event) {
        lidarsRecentData=new ArrayList<>();
        lidarsRecentData.add(event.getTrackedObjects());
    }

    public String getRecentCameraSer() {
        return recentCamer;
    }

    public String getRecentLidar() {
        return recentLidar;
    }

    public void setRecentLidar(String recentLidar) {
        this.recentLidar=recentLidar;
    }

    public void addCameraFrame(String name, StampedDetectedObjects obj){
        lastCameraMap.put(name, obj);
    }
    public Map<String, StampedDetectedObjects> getLastCameraMap() {
        return lastCameraMap;
    }

    public void addLiDarFrame(String name, List<TrackedObject> trackedObjects) {
        lastLiDarMap.put(name, trackedObjects);
    }

    public Map<String, List<TrackedObject>> getLastLiDarMap() {
        return lastLiDarMap;
    }

    // Singleton pattern
    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();
    }


    public static FusionSlam getInstance() {
        return FusionSlamHolder.INSTANCE;
    }



    public  void addPose(Pose pose) {
        poses.add(pose);
    }


    public  Pose getPoseAtTime(int time) {
        for (Pose pose : poses) {
            if (pose.getTime() == time) {
                return pose;
            }
        }
        return null;
    }



    public synchronized void storePendingTrackedObject(TrackedObjectsEvent event) {
        pendingTrackedObjects.add(event);
    }



    public synchronized void addOrUpdateLandmark(LandMark landmark) {
        String id = landmark.getId();
        if (landmarks.containsKey(id)) {
            // Update existing landmark by averaging coordinates
            LandMark existingLandmark = landmarks.get(id);
            List<CloudPoint> updatedCoordinates = averageCoordinates(existingLandmark.getCoordinates(), landmark.getCoordinates());
            landmarks.put(id, new LandMark(id, landmark.getDescription(), updatedCoordinates));
        } else {
            statisticalFolder.incrementNumLandmarks();
            // Add new landmark
            landmarks.put(id, landmark);
        }
    }



    public synchronized void processPendingTrackedObjects(Pose pose) {
        Iterator<TrackedObjectsEvent> iterator = pendingTrackedObjects.iterator();
        boolean eventFound=false;
        while (!eventFound && iterator.hasNext()) {
            TrackedObjectsEvent event = iterator.next();
            if (event.getTractiontime() == pose.getTime()) {
                eventFound=true;
                for (TrackedObject object : event.getTrackedObjects()) {
                    List<CloudPoint> globalCoordinates = transformToGlobal(object.getCoordinates(), pose);
                    addOrUpdateLandmark(new LandMark(object.getId(), object.getDescription(), globalCoordinates));
                }
                iterator.remove(); // Remove processed event
            }
        }
    }



    public List<CloudPoint> transformToGlobal(List<CloudPoint> localCoordinates, Pose pose) {
        List<CloudPoint> globalCoordinates = new ArrayList<>();
        double yawRadians = Math.toRadians(pose.getYaw());
        double cosTheta = Math.cos(yawRadians);
        double sinTheta = Math.sin(yawRadians);

        for (CloudPoint local : localCoordinates) {
            double xGlobal = cosTheta * local.getX() - sinTheta * local.getY() + pose.getX();
            double yGlobal = sinTheta * local.getX() + cosTheta * local.getY() + pose.getY();
            globalCoordinates.add(new CloudPoint(xGlobal, yGlobal));
        }

        return globalCoordinates;
    }



    private List<CloudPoint> averageCoordinates(List<CloudPoint> coords1, List<CloudPoint> coords2) {
        List<CloudPoint> averaged = new ArrayList<>();
        int size = Math.min(coords1.size(), coords2.size());
        for (int i = 0; i < size; i++) {
            CloudPoint p1 = coords1.get(i);
            CloudPoint p2 = coords2.get(i);
            averaged.add(new CloudPoint(
                    (p1.getX() + p2.getX()) / 2,
                    (p1.getY() + p2.getY()) / 2
            ));
        }
        return averaged;
    }



}
