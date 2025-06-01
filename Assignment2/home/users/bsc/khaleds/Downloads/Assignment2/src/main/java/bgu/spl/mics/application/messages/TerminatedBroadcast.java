package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminatedBroadcast implements Broadcast {
    private int timesign;


    public TerminatedBroadcast(int timesign){
        this.timesign=timesign;

    }

    public int getTimesign(){
        return timesign;

    }

}
