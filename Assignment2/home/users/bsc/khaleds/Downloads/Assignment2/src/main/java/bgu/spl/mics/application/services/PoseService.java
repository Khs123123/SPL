package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    GPSIMU gpsimu;
   // private final Map<Integer, Pose> poseMap; // Stores poses indexed by tick

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu=gpsimu;

    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tick) -> {
            int currentTick=tick.getTime();
            if (currentTick==-1) terminate(); // terminating if the timeservice terminated
            Pose p = gpsimu.getPose(currentTick);

            if(p!=null) {
                gpsimu.addPoseToRecents(p);
                gpsimu.setRemainingposess();
                PoseEvent currentpose = new PoseEvent(p);
                sendEvent(currentpose);

            }

            if (gpsimu.getRemainingposess()==0) {
                sendBroadcast(new TerminatedBroadcast(10));
                terminate();
            }


        });

        subscribeBroadcast(TerminatedBroadcast.class, (TerminatedBroadcast terminate) -> {

        });


        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            sendBroadcast(new CrashedBroadcast(broadcast.getFaultySensor(),broadcast.getErrorDescription(),null,null,null,null,gpsimu.getRecentposes(),10));
            terminate();
        });

    }



}
