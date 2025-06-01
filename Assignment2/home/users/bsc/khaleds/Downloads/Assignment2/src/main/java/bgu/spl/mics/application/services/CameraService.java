package bgu.spl.mics.application.services;


import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 *
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private boolean errorinprevious=false;
    private boolean errorincurrent=false;

    String errorMSG;
    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("CameraService_" + camera.getId());
        this.camera = camera;

    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {

            int currentTick = tick.getTime();
            if (currentTick==-1) terminate(); // terminating if the timeservice terminated
            camera.setCameraClock(currentTick);
            camera.setcamerastatus();



            // Check if there are any detections to process at this tick
            DetectObjectsEvent event = camera.processDetections(currentTick);

            if ( event != null) {
                for (DetectedObject obj : event.getStampedDetectedObjects().getDetectedObjects()) {
                    if (Objects.equals(obj.getId(), "ERROR")) {
                        errorinprevious = true;
                    }
                }
            }

            StampedDetectedObjects objects =camera.getobjectsDetictedAtthisTime();
            StampedDetectedObjects checkForError =camera.getObjectAtTimeWithoutFrequency();
            if (objects!=null) {
                for (DetectedObject obj : checkForError.getDetectedObjects()) {
                    if (Objects.equals(obj.getId(), "ERROR")) {
                        errorincurrent = true;
                        errorMSG = obj.getDescription();
                    }
                }
                if (!errorincurrent) FusionSlam.getInstance().getStatisticalFolder().addDetectedObjects(objects.getDetectedObjects().size());
            }



            if(event !=null) {
                if (errorincurrent) {
                    if (!errorinprevious) {
                        Future<?> future = sendEvent(event);
                        sendBroadcast(new CrashedBroadcast(camera.getcamerakey(), errorMSG, camera.getcamerakey(), camera.getRecentdetectedobjects(), null,null, null, 10));
                        terminate();
                        return;
                    } else if (errorinprevious) { sendBroadcast(new CrashedBroadcast(camera.getcamerakey(), errorMSG, camera.getcamerakey(), camera.getRecentdetectedobjects(), null,null, null, 10));
                        terminate();
                        return;
                    }
                } else {
                    camera.setRecentdetectedobjects(event.getStampedDetectedObjects());
                        Future<?> future = sendEvent(event);
                        FusionSlam.getInstance().addCameraFrame(camera.getcamerakey(), event.getStampedDetectedObjects());
                }
            }




             if (camera.finishedDetecting()&& currentTick >camera.getFinaltime()) {
                 sendBroadcast(new TerminatedBroadcast(10));

                 terminate();

             }
        });

        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {

        });

        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            if(broadcast.getCamerasRecentData()!=null){
                sendBroadcast(new CrashedBroadcast(broadcast.getFaultySensor(),broadcast.getErrorDescription(),broadcast.getRecentCamSer(),broadcast.getCamerasRecentData(),broadcast.getRecentLidar(),broadcast.getLidarsRecentData(),broadcast.getRecentPoses(),10));
            }
            sendBroadcast(new CrashedBroadcast(broadcast.getFaultySensor(),broadcast.getErrorDescription(),camera.getcamerakey(),camera.getRecentdetectedobjects(),broadcast.getRecentLidar(),broadcast.getLidarsRecentData(),broadcast.getRecentPoses(),10));
            terminate();
        });
    }

}
