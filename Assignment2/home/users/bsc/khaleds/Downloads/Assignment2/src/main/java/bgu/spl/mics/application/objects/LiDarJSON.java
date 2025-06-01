package bgu.spl.mics.application.objects;

public class LiDarJSON {
    private int id;
    private int frequency;

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
    public String toString(){
        return "ID: " + id + " FREQ: " + frequency;
    }
}
