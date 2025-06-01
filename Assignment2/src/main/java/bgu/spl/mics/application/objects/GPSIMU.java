package bgu.spl.mics.application.objects;


import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {


    private ArrayList<Pose> PoseList;
    private int remainingposess;
    private ArrayList<Pose> recentposes=new ArrayList<>();


    public GPSIMU(ArrayList<Pose> PoseLists) {

        this.PoseList = PoseLists;
        remainingposess= PoseList.size();
    }

    public void addPoseToRecents(Pose pose){
        recentposes.add(pose);

    }

    public ArrayList<Pose> getRecentposes(){ return recentposes;}



    public Pose getPose(int time) {
        for(Pose poseToReturn : PoseList){
            if(poseToReturn.getTime()==time)
                return poseToReturn;
        }
        return null;
    }

    public int getRemainingposess(){ return remainingposess;}

    public void setRemainingposess() {

        this.remainingposess = remainingposess-1;
    }
}
