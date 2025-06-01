package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.LiDarService;

import java.util.ArrayList;
import java.util.List;

public class LidarWorkersConfiguration {
    private List<LiDarJSON> LidarConfigurations;
    private String lidars_data_path;

    // Getters and Setters
    public List<LiDarJSON> getLidarConfigurations() {
        return LidarConfigurations;
    }


    public void setLidarConfigurations(List<LiDarJSON> lidarConfigurations) {
        LidarConfigurations = lidarConfigurations;
    }

    public String getLidarsDataPath() {
        return lidars_data_path;
    }

    public void setLidarsDataPath(String lidarsDataPath) {
        this.lidars_data_path = lidarsDataPath;
    }
    @Override
    public String toString() {
        return "LidarWorkersConfiguration{" +
                "LidarConfigurations=" + LidarConfigurations +
                ", lidars_data_path='" + lidars_data_path + '\'' +
                '}';
    }

    public List<LiDarService> getLidarServises() {
        List<LiDarService> lidarServisesToReturn = new ArrayList<LiDarService>();
        for(LiDarJSON liDarJSON : LidarConfigurations) {
            LiDarWorkerTracker lworker =new LiDarWorkerTracker(liDarJSON.getId(),liDarJSON.getFrequency(),lidars_data_path);
            LiDarService lidServise=new LiDarService(lworker);
            lidarServisesToReturn.add(lidServise);
        }
        return lidarServisesToReturn;
    }
}

