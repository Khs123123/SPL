package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectObjectsEvent;

import bgu.spl.mics.application.objects.STATUS;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    // Fields
    private int id;                            // Unique ID for the camera
    private int frequency;                     // Time interval for sending events
    private STATUS status;                     // Camera's current status
    private List<StampedDetectedObjects> detectedObjectsList; // Time-stamped detected objects
    private StampedDetectedObjects recentdetectedobjects;
    private String cameraKey;
    private int cameraClock=-1;
    private int remainingObjectsTodetect=0;
    private int finaltime=-1;
  //  private int check;


    public Camera(int id, int frequency, List<StampedDetectedObjects> detectedObjectsList,String cameraKey) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP; // Default status
        this.detectedObjectsList = detectedObjectsList;
        this.cameraKey=cameraKey;
        setTotalnumberofobjects();






    }

    public StampedDetectedObjects getRecentdetectedobjects(){return recentdetectedobjects;}
    public void setRecentdetectedobjects(StampedDetectedObjects recentdetectedobjects) {
        this.recentdetectedobjects = recentdetectedobjects;
    }


    private void setTotalnumberofobjects(){

        for (StampedDetectedObjects list :detectedObjectsList){
            remainingObjectsTodetect+=list.getDetectedObjects().size();
        }


    }



    public void setCameraClock(int currTime) {
        cameraClock = currTime;
    }

    public void setcamerastatus () {
        StampedDetectedObjects objectsDetictedAtthisTime = getobjectsDetictedAtthisTime();
        if(objectsDetictedAtthisTime!=null) {
            for (DetectedObject stamped : objectsDetictedAtthisTime.getDetectedObjects()) {
                if (stamped.getId() == "ERROR"){
                    System.out.println("get an error in time "+cameraClock);
                    setStatus(STATUS.ERROR);
                return;
                }
            }
        }

    }

    public StampedDetectedObjects getobjectsDetictedAtthisTime() {
        for (StampedDetectedObjects stamped : detectedObjectsList) {
            if (stamped.getTime() + frequency == cameraClock) {
                return stamped;
            }
        }
        return null;

    }

    public boolean finishedDetecting(){
        return remainingObjectsTodetect == 0;

    }

    public int getFinaltime(){ return finaltime;}



    public int getId() {
        return id;
    }

    public String getcamerakey() {
        return cameraKey;
    }


    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    public DetectObjectsEvent processDetections(int currentTick) {
        int detectionTime = currentTick - frequency;
        for (StampedDetectedObjects stamped : detectedObjectsList) {
            if (stamped.getTime() == detectionTime) {
                //recentdetectedobjects=stamped;
                int size = stamped.getDetectedObjects().size();
                remainingObjectsTodetect-=size;
                if (remainingObjectsTodetect==0) finaltime=currentTick;
                return new DetectObjectsEvent(stamped);

            }
        }
        return null; // No detections to process at this tick
    }


    public StampedDetectedObjects getObjectAtTimeWithoutFrequency() {
        for (StampedDetectedObjects stamped : detectedObjectsList) {
            if (stamped.getTime()== cameraClock) {
                return stamped;
            }
        }
        return null;
    }
}
