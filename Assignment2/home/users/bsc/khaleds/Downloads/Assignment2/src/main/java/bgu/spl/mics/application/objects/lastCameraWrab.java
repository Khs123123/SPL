package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class lastCameraWrab {




    public void setLastDetectedObjects(String name ,List<StampedDetectedObjects> lastDetectedObjects) {

        this.detectedObjects.put(name, lastDetectedObjects);
    }

    Map<String,List<StampedDetectedObjects>> detectedObjects=new HashMap<>();

}
