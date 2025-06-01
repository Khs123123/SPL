package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

class  Main {
    public static void main() {
        // Create first list of CloudPoints
        List<CloudPoint> coords1 = new ArrayList<>();
        coords1.add(new CloudPoint(0.1176, 3.6969));
        coords1.add(new CloudPoint(0.11362, 3.6039));

        // Create second list of CloudPoints
        List<CloudPoint> coords2 = new ArrayList<>();
        coords2.add(new CloudPoint(0.5, 3.9));
        coords2.add(new CloudPoint(0.2, 3.7));

        // Create Pose objects
        Pose pose1 = new Pose(-3.2076, 0.0755, -87.48, 2); // First Pose
        Pose pose2 = new Pose(0.0, 3.6, 57.3, 10);         // Second Pose




        // Test transformToGlobal method with Pose 1
        List<CloudPoint> globalCoordinatesPose1 = transformToGlobal(coords1, pose1);


        // Test transformToGlobal method with Pose 2
        List<CloudPoint> globalCoordinatesPose2 = transformToGlobal(coords2, pose2);

          List<CloudPoint> averagedCoordinates = averageCoordinates(globalCoordinatesPose1, globalCoordinatesPose2);


    }

    // Static copies of methods for simplicity
    private static List<CloudPoint> averageCoordinates(List<CloudPoint> coords1, List<CloudPoint> coords2) {
        List<CloudPoint> averaged = new ArrayList<>();
        int size = Math.min(coords1.size(), coords2.size());
        for (int i = 0; i < size; i++) {
            CloudPoint p1 = coords1.get(i);
            CloudPoint p2 = coords2.get(i);
            averaged.add(new CloudPoint(
                    (p1.getX() + p2.getX()) / 2,
                    (p1.getY() + p2.getY()) / 2
            ));
        }
        return averaged;
    }

    private static List<CloudPoint> transformToGlobal(List<CloudPoint> localCoordinates, Pose pose) {
        List<CloudPoint> globalCoordinates = new ArrayList<>();
        double yawRadians = Math.toRadians(pose.getYaw());
        double cosTheta = Math.cos(yawRadians);
        double sinTheta = Math.sin(yawRadians);

        for (CloudPoint local : localCoordinates) {
            double xGlobal = cosTheta * local.getX() - sinTheta * local.getY() + pose.getX();
            double yGlobal = sinTheta * local.getX() + cosTheta * local.getY() + pose.getY();
            globalCoordinates.add(new CloudPoint(xGlobal, yGlobal));
        }

        return globalCoordinates;
    }
}
