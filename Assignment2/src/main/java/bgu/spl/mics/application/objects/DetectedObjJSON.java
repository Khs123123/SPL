package bgu.spl.mics.application.objects;

/**
 * Represents a detected object in the system with an ID and description.
 */
public class DetectedObjJSON {
    private String id;
    private String description;

    public DetectedObjJSON() {
    }

    public DetectedObjJSON(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DetectedObjJSON{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
