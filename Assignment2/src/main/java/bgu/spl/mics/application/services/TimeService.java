package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.FusionSlam;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private final int tickTime;
    private final int duration;
    private int numofoperetinalservices = 0;
    private int indicator;
    private int count = 1;
    private String whoCrashed;
    private String crashingDescription;
    volatile boolean isTerminated = false;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in milliseconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TickBroadcast");
        tickTime = TickTime;
        duration = Duration;
    }

    public int getTickTime() {
        return tickTime;
    }

    public int getDuration() {
        return duration;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */


    public void setNumofoperetinalservices(int num) {

        numofoperetinalservices = num;
        indicator = num;

    }


    @Override
    protected void initialize() {
        // Subscribe to TerminatedBroadcast to terminate the service when needed
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            int sign = terminate.getTimesign();
            if (sign == 10) numofoperetinalservices--;

            if (numofoperetinalservices == 2) {
                isTerminated = true;
                sendBroadcast(new TerminatedBroadcast(1));
                terminate();
                count = duration + 99;
            }







        });
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            isTerminated = true;
            numofoperetinalservices--;


            if (numofoperetinalservices == 2) {

                sendBroadcast(new CrashedBroadcast(whoCrashed, crashingDescription,broadcast.getRecentCamSer(), broadcast.getCamerasRecentData(), broadcast.getRecentLidar(),broadcast.getLidarsRecentData(), broadcast.getRecentPoses(), 1));
                terminate();

            }

        });

        // Start a separate thread for ticking
        new Thread(() -> {
            count =1;//broadcast.getTime();


            while ((duration >= count) & !isTerminated) {
                // Broadcast a tick to all services
                sendBroadcast(new TickBroadcast(count));

                try {
                    Thread.sleep(tickTime*100 );//TODO check tick time
                    count++;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    //break;
                }
            }




            if(!isTerminated) {
                //sendBroadcast(new TickBroadcast(1));
                sendBroadcast(new TickBroadcast(-1));
                 sendBroadcast(new TerminatedBroadcast(1));
                terminate();
            }
        }).start();
        // Send final termination tick


    }

}
