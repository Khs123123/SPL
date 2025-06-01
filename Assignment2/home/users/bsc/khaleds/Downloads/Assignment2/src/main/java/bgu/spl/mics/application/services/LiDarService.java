package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;


/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 *
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */




public class LiDarService extends MicroService {
    private  LiDarWorkerTracker lidarWorkerTracker;
    int currentTick=0;

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LiDarService-" + LiDarWorkerTracker.getId());
        this.lidarWorkerTracker = LiDarWorkerTracker;
    }



    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        // Subscribe to DetectObjectsEvent
        subscribeEvent(DetectObjectsEvent.class, event -> {

            int detectionTime = event.getStampedDetectedObjects().getTime();
            if (currentTick < detectionTime + lidarWorkerTracker.getFrequency()) {


                lidarWorkerTracker.addToPendingEvents(event);
            }



            if (currentTick >= detectionTime + lidarWorkerTracker.getFrequency()) {
                TrackedObjectsEvent trackedObjectsEvent = lidarWorkerTracker.processEvent(event, currentTick,detectionTime);
                if (trackedObjectsEvent != null){
                    FusionSlam.getInstance().addLiDarFrame(getName(), trackedObjectsEvent.getTrackedObjects());
                    sendEvent(trackedObjectsEvent);
                } // Send to Fusion-SLAM

            }
            complete(event,true);
        });

        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {



            currentTick = tick.getTime();
            if (currentTick==-1) terminate(); // terminating if the timeservice terminated
            lidarWorkerTracker.setLidarclockClock(currentTick);
            lidarWorkerTracker.isLidarWorking();

            if (lidarWorkerTracker.getStatus()==STATUS.ERROR) {

                sendBroadcast(new CrashedBroadcast("LiDarWorkerTracker"+lidarWorkerTracker.getId(),"Lidar not working",null,null,getName(),lidarWorkerTracker.getLastTrackedObjects(),null,10));
                terminate();
                return;

            }

            // Process pending events based on the current tick
            List<TrackedObjectsEvent> processedEvents = lidarWorkerTracker.processPendingEvents(currentTick);
            for (TrackedObjectsEvent event : processedEvents) {
                FusionSlam.getInstance().addLiDarFrame(getName(), event.getTrackedObjects());

                sendEvent(event); // Send each processed event to Fusion-SLAM
            }




            if(lidarWorkerTracker.finishedtracking()) {
                sendBroadcast(new TerminatedBroadcast(10));
                terminate();

            }

        });

        // Ready to terminate when the system shuts down
        subscribeBroadcast(TerminatedBroadcast.class, terminate ->{


        });

        subscribeBroadcast(CrashedBroadcast.class, crash -> {



            List<TrackedObject>  lidarsRecentData=lidarWorkerTracker.getLastTrackedObjects();

            if(crash.getLidarsRecentData()==null)
                sendBroadcast(new CrashedBroadcast(crash.getFaultySensor(),crash.getErrorDescription(),null,null,getName(),lidarsRecentData,null,10));
            else
                sendBroadcast(new CrashedBroadcast(crash.getFaultySensor(),crash.getErrorDescription(),null,null,crash.getRecentLidar(),crash.getLidarsRecentData(),null,10));

            terminate();

        });




    }
}
