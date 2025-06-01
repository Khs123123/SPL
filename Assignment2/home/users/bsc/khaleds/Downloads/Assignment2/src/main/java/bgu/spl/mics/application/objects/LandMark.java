package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    String id;
    String description;
    List<CloudPoint> Coordinates;

    public LandMark(String id , String description, List<CloudPoint> Coordinates) {
        this.id = id;
        this.description = description;
        this.Coordinates = Coordinates;
    }
    public String getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public List<CloudPoint> getCoordinates() {
        return Coordinates;
    }



}
