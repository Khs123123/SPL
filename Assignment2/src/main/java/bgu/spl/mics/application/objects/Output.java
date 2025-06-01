package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Output {
    String error;
    String faultySensor;
    Map<String,StampedDetectedObjects> lastCamerasFrame;
    Map<String,List<TrackedObject>> lastLiDarWorkerTrackersFrame;
    List<Pose> poses;

    private final int systemRuntime;
    private final int numDetectedObjects;
    private final int numTrackedObjects;
    private final int numLandmarks;
    Map<String, LandMark> landMarks;

    public Output(FusionSlam fusionSlam) {
        this.landMarks = new HashMap<>();
        for (LandMark landmark : fusionSlam.getLandmarks()) {
            this.landMarks.put(landmark.getId(), landmark);
        }        this.numLandmarks=fusionSlam.getStatisticalFolder().getNumLandmarks();
        this.numTrackedObjects=fusionSlam.getStatisticalFolder().getNumTrackedObjects();
        this.systemRuntime=fusionSlam.getStatisticalFolder().getSystemRuntime();
        this.numDetectedObjects=fusionSlam.getStatisticalFolder().getNumDetectedObjects();
        getError(fusionSlam);
    }

    private void getError(FusionSlam fusionSlam) {
        if(fusionSlam.getErrorDescription()!=null)
        {
            lastLiDarWorkerTrackersFrame=fusionSlam.getLastLiDarMap();
            lastCamerasFrame=fusionSlam.getLastCameraMap();
            error=fusionSlam.getErrorDescription();
            faultySensor=fusionSlam.getWhoCrashed();
            poses=fusionSlam.getRecentPoses();




        }
    }


}
