package bgu.spl.mics.application.objects;

public class CloudPointJSON {

    private double x;
    private double y;
    private double z;

    public CloudPointJSON(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    @Override
    public String toString() {
        return "CloudPointJSON{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
