package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;
public class PoseEvent implements Event<Boolean>{
    private final Pose pose;
    //private final int time;

    public PoseEvent(Pose pose) {
        this.pose = pose;
       // this.time = time;
    }

    public Pose getPose() {
        return pose;
    }


}
