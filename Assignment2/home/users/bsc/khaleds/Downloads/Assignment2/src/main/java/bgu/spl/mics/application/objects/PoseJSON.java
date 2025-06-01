package bgu.spl.mics.application.objects;

public class PoseJSON {
    float x;

    public float getY() {
        return y;
    }

    float y;

    public float getZ() {
        return z;
    }

    float z;
    public PoseJSON(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public float getX() {
        return x;
    }
    public String toString(){
        return "x: " + x + ", y: " + y + ", z: " + z;
    }
}
