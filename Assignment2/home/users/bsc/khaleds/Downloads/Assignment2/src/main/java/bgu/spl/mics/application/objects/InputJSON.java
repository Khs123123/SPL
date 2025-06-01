package bgu.spl.mics.application.objects;


public class InputJSON {
    private CamerasJSON Cameras;
    private LidarWorkersConfiguration LiDarWorkers;
    private String poseJsonFile;
    private int TickTime;
    private int Duration;

    // Getters and Setters
    public CamerasJSON getCameras() {
        return Cameras;
    }

    public void setCameras(CamerasJSON cameras) {
        Cameras = cameras;
    }

    public LidarWorkersConfiguration getLidarWorkers() {
        return LiDarWorkers;
    }

    public void setLidarWorkers(LidarWorkersConfiguration lidarWorkers) {
        LiDarWorkers = lidarWorkers;
    }

    public String getPoseJsonFile() {
        return poseJsonFile;
    }

    public void setPoseJsonFile(String poseJsonFile) {
        this.poseJsonFile = poseJsonFile;
    }

    public int getTickTime() {
        return TickTime;
    }

    public void setTickTime(int tickTime) {
        TickTime = tickTime;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public String toString(){
        return "\nCameras: " + Cameras.toString() + "\nLidarWorkers: " + LiDarWorkers.toString() + "\nPoseJsonFile: " + poseJsonFile + "\nTickTime: " + TickTime + "\nDuration: " + Duration;
    }

}
