package bgu.spl.mics.application.objects;

import java.util.List;

public class CameraDataJSON {
    int time;
    List<DetectedObjJSON> detectedObjects;
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public List<DetectedObjJSON> getDetectedObjects() {
        return detectedObjects;
    }
    public void setDetectedObjects(List<DetectedObjJSON> detectedObjects) {
        this.detectedObjects = detectedObjects;
    }
    @Override
    public String toString() {
        return "CameraDataJSON{" +
                "time=" + time +
                ", detectedObjects=" + detectedObjects +
                '}';
    }
}
