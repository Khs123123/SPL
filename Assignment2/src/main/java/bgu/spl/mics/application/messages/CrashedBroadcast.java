package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StampedCloudPoints;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.ArrayList;
import java.util.List;

/**
 * CrashedBroadcast is sent by a sensor to notify all other services
 * that it has crashed.
 */
public class CrashedBroadcast implements Broadcast {
    private final String faultySensor;
    private final String errorDescription;
    private  StampedDetectedObjects camerasRecentData;
    private List<TrackedObject>  lidarsRecentData;
    private ArrayList<Pose> recentPoses;
    private int timeindicator;
    String recentCam;
    String recentLidar;



    /**
     * Constructor to initialize CrashedBroadcast.
     *
     * @param faultySensor    The name of the faulty sensor.
     * @param errorDescription A description of the error.
     */
    public CrashedBroadcast(String faultySensor, String errorDescription,String recentCam,StampedDetectedObjects camerasRecentData,String recentLidar,List<TrackedObject>  lidarsRecentData,ArrayList<Pose> recentPoses,int timeindicator) {
        this.faultySensor = faultySensor;
        this.errorDescription = errorDescription;
        this.camerasRecentData=camerasRecentData;
        this.lidarsRecentData=lidarsRecentData;
        this.recentPoses=recentPoses;
        this.timeindicator=timeindicator;
        this.recentLidar=recentLidar;

        this.recentCam=recentCam;
    }

    public StampedDetectedObjects getCamerasRecentData(){return camerasRecentData;}
    public List<TrackedObject> getLidarsRecentData(){return lidarsRecentData;}
    public ArrayList<Pose> getRecentPoses (){return recentPoses;}
    public int getTimeindicator (){return timeindicator;}
    /**
     * Gets the name of the faulty sensor.
     *
     * @return The faulty sensor's name.
     */
    public String getFaultySensor() {
        return faultySensor;
    }

    /**
     * Gets the error description.
     *
     * @return The error description.
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    public String getRecentCamSer() {
        return recentCam;
    }

    public String  getRecentLidar() {
        return recentLidar;
    }
}
