package bgu.spl.mics.application.objects;

import java.util.List;

public class stambedCloudPointJSON {
    int time;
    String id;
    List<List<Double>> cloudPoints;
    public void setTime(int time) {
        this.time = time;
    }
    public int getTime() {
        return time;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setCloudPoints(List<List<Double>> cloudPoints) {
        this.cloudPoints = cloudPoints;
    }
    public List<List<Double>> getCloudPoints() {
        return cloudPoints;
    }
    public String toString() {
        return id + " " + cloudPoints;
    }

}
