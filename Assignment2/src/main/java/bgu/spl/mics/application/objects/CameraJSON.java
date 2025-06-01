package bgu.spl.mics.application.objects;

public class CameraJSON {
    private int id;
    private int frequency;
    private String camera_key;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getFrequency() {
        return frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public String getCameraKey() {
        return camera_key;
    }
    public void setCameraKey(String camera_key) {
        this.camera_key = camera_key;
    }
    @Override
    public String toString() {
        return "CameraJSON{" +
               "id=" + id +
               ", frequency=" + frequency +
               ", camera_key='" + camera_key + '\'' +
               '}';
    }

}
