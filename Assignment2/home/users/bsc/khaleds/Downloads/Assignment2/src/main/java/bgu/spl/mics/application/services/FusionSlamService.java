package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import com.google.gson.internal.bind.util.ISO8601Utils;

import java.util.List;

import java.util.ArrayList;
import java.util.List;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;

    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlamService");
        this.fusionSlam = fusionSlam;
    }

    @Override
    protected void initialize() {
        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            if(crash.getRecentCamSer()!=null){
                fusionSlam.setRecentCamer(crash.getRecentCamSer());
            }
            if(crash.getRecentLidar()!=null){
                fusionSlam.setRecentLidar(crash.getRecentLidar());
            }
            if(fusionSlam.getWhoCrashed()==null||fusionSlam.getWhoCrashed().isEmpty()||fusionSlam.getErrorDescription().isEmpty())
            {
                fusionSlam.setWhoCrashed(crash.getFaultySensor());
                fusionSlam.setErrorDescription(crash.getErrorDescription());
            }

            if (crash.getCamerasRecentData()!=null) {
                fusionSlam.addToCamerasRecentData(crash.getCamerasRecentData());
            }
            if (crash.getRecentPoses()!= null){
                fusionSlam.addrecentposes(crash.getRecentPoses());
            }
            if (crash.getLidarsRecentData()!=null){
                fusionSlam.addTolidarsRecentData(crash.getLidarsRecentData());

            }
            if (crash.getTimeindicator()==1) {
                terminate();
            }

        });

        // Subscribe to TerminateBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            int sign =terminate.getTimesign();
            if (sign==1) {

                terminate();

            }

        });

        // Subscribe to TickBroadcast (currently no action)
        subscribeBroadcast(TickBroadcast.class, tick -> {
            FusionSlam.getInstance().getStatisticalFolder().incrementSystemRuntime();

            if(tick.getTime()==-1){
                sendBroadcast(new TerminatedBroadcast(10));
                terminate();
            }



        });

        // Subscribe to PoseEvent
        subscribeEvent(PoseEvent.class,  event -> {
            Pose pose = event.getPose();
            fusionSlam.addPose(pose);

            // Process pending tracked objects for this pose's timestamp
            fusionSlam.processPendingTrackedObjects(pose);

            complete(event,true);

        });

        // Subscribe to TrackedObjectsEvent
        subscribeEvent(TrackedObjectsEvent.class, event -> {
            for(TrackedObject tr:event.getTrackedObjects()){
            }
            FusionSlam.getInstance().getStatisticalFolder().addTrackedObjects(event.getTrackedObjects().size());

            int Tractiontime = event.getTractiontime ();
            Pose poseAtTime = fusionSlam.getPoseAtTime(Tractiontime);


            if (poseAtTime != null) {
                // Transform coordinates and update landmarks
                for (TrackedObject object : event.getTrackedObjects()) {
                    List<CloudPoint> globalCoordinates = fusionSlam.transformToGlobal(object.getCoordinates(), poseAtTime);
                    LandMark landmark = new LandMark(object.getId(), object.getDescription(), globalCoordinates);
                    fusionSlam.addOrUpdateLandmark(landmark);
                }
            } else {
                // Store event for future processing
                fusionSlam.storePendingTrackedObject(event);
            }

            fusionSlam.setLidarRecent(event);
            complete(event,true);
        });


    }
}
