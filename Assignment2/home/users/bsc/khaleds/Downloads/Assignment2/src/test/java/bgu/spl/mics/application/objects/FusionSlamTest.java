package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FusionSlamTest {

    @Test
    public void testTransformToGlobal_SinglePoint() {
        Pose pose = new Pose(1.0f, 2.0f, 45.0f, 0); // Robot's pose
        List<CloudPoint> localPoints = Arrays.asList(new CloudPoint(1.0, 1.0)); // Local coordinates

        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose);

        // Manually calculated global coordinates:
        double expectedX = 1.0;
        double expectedY = 3.4142;

        assertEquals(expectedX, globalPoints.get(0).getX(), 0.0001, "First point's X coordinate is incorrect");
        assertEquals(expectedY, globalPoints.get(0).getY(), 0.0001, "First point's Y coordinate is incorrect");
    }

    @Test
    void testTransformToGlobal_SinglePointNegativeCoordinates() {
        Pose pose = new Pose(2.0f, 3.0f, 90.0f, 0);
        List<CloudPoint> localPoints = Arrays.asList(new CloudPoint(-1.0, -1.0));

        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose);



        assertEquals(3.0, globalPoints.get(0).getX(), 0.0001, "First point's X coordinate is incorrect");
        assertEquals(2.0, globalPoints.get(0).getY(), 0.0001, "First point's Y coordinate is incorrect");
    }


    @Test
    public void testTransformToGlobal_MultiplePoints() {
        Pose pose = new Pose(0.0f, 0.0f, 45.0f, 0); // Robot's pose
        List<CloudPoint> localPoints = Arrays.asList(
                new CloudPoint(1.0, 0.0),
                new CloudPoint(0.0, 1.0),
                new CloudPoint(1.0, 1.0)
        );

        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose);

        // Manually calculated global coordinates:
        double[][] expectedCoordinates = {
                {0.7071, 0.7071}, // Point 1
                {-0.7071, 0.7071}, // Point 2
                {0.0, 1.4142} // Point 3
        };

        for (int i = 0; i < globalPoints.size(); i++) {
            assertEquals(expectedCoordinates[i][0], globalPoints.get(i).getX(), 0.0001, "Point " + (i + 1) + " X coordinate is incorrect");
            assertEquals(expectedCoordinates[i][1], globalPoints.get(i).getY(), 0.0001, "Point " + (i + 1) + " Y coordinate is incorrect");
        }
    }

    @Test
    public void testTransformToGlobal_ZeroYaw() {
        Pose pose = new Pose(5.0f, 5.0f, 0.0f, 0); // Robot's pose
        List<CloudPoint> localPoints = Arrays.asList(new CloudPoint(2.0, 3.0)); // Local coordinates

        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose);

        // Manually calculated global coordinates:
        double expectedX = 7.0;
        double expectedY = 8.0;

        assertEquals(expectedX, globalPoints.get(0).getX(), 0.0001, "First point's X coordinate is incorrect");
        assertEquals(expectedY, globalPoints.get(0).getY(), 0.0001, "First point's Y coordinate is incorrect");
    }

    @Test
    public void testTransformToGlobal_180Yaw() {
        Pose pose = new Pose(0.0f, 0.0f, 180.0f, 0); // Robot's pose
        List<CloudPoint> localPoints = Arrays.asList(new CloudPoint(1.0, 0.0)); // Local coordinates

        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose);

        // Manually calculated global coordinates:
        double expectedX = -1.0;
        double expectedY = 0.0;

        assertEquals(expectedX, globalPoints.get(0).getX(), 0.0001, "First point's X coordinate is incorrect");
        assertEquals(expectedY, globalPoints.get(0).getY(), 0.0001, "First point's Y coordinate is incorrect");
    }

    @Test
    public void testTransformToGlobal_NonZeroPoseTranslation() {
        Pose pose = new Pose(3.0f, 4.0f, 45.0f, 0); // Robot's pose
        List<CloudPoint> localPoints = Arrays.asList(new CloudPoint(1.0, 1.0)); // Local coordinates

        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose);

        // Manually calculated global coordinates:
        double expectedX = 3.0;
        double expectedY = 5.4142;

        assertEquals(expectedX, globalPoints.get(0).getX(), 0.0001, "First point's X coordinate is incorrect");
        assertEquals(expectedY, globalPoints.get(0).getY(), 0.0001, "First point's Y coordinate is incorrect");
    }
    @Test
    void testTransformToGlobal_ComplexRotationWithOffset() {
        Pose pose = new Pose(5.5f, -3.3f, 120.0f, 0);
        List<CloudPoint> localPoints = Arrays.asList(new CloudPoint(2.2, -1.7));

        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose);



        assertEquals(5.8722, globalPoints.get(0).getX(), 0.0001, "First point's X coordinate is incorrect");
        assertEquals(-0.5448, globalPoints.get(0).getY(), 0.0001, "First point's Y coordinate is incorrect");
    }
    @Test
    void testTransformToGlobal_NegativeRotationAndCoordinates() {
        Pose pose = new Pose(-2.0f, 4.0f, -45.0f, 0);
        List<CloudPoint> localPoints = Arrays.asList(new CloudPoint(-3.0, 2.0));

        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose);

        System.out.println("Testing negative rotation and local coordinates...");
        System.out.println("Pose: " + pose);
        System.out.println("Local Points: " + localPoints);
        System.out.println("Global Points: " + globalPoints);

        assertEquals(-2.7071, globalPoints.get(0).getX(), 0.0001, "First point's X coordinate is incorrect");
        assertEquals(7.5355, globalPoints.get(0).getY(), 0.0001, "First point's Y coordinate is incorrect");
    }
    @Test
    void testTransformToGlobal_MultipleLocalPoints() {
        Pose pose = new Pose(0.0f, 0.0f, 30.0f, 0);
        List<CloudPoint> localPoints = Arrays.asList(
                new CloudPoint(1.0, 1.0),
                new CloudPoint(2.0, -1.0),
                new CloudPoint(-1.0, -2.0)
        );

        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose);



        assertEquals(0.366, globalPoints.get(0).getX(), 0.0001, "First point's X coordinate is incorrect");
        assertEquals(1.366, globalPoints.get(0).getY(), 0.0001, "First point's Y coordinate is incorrect");

        assertEquals(2.232, globalPoints.get(1).getX(), 0.0001, "Second point's X coordinate is incorrect");
        assertEquals(0.134, globalPoints.get(1).getY(), 0.0001, "Second point's Y coordinate is incorrect");

        assertEquals(0.134, globalPoints.get(2).getX(), 0.0001, "Third point's X coordinate is incorrect");
        assertEquals(-2.232, globalPoints.get(2).getY(), 0.0001, "Third point's Y coordinate is incorrect");
    }


}
