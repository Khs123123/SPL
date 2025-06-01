package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LiDarWorkerTrackerTest {

    private LiDarWorkerTracker tracker;

    @BeforeEach
    void setUp() {
        // Initialize the mock data for LiDarDataBase
        List<StampedCloudPoints> mockData = new ArrayList<>();
        mockData.add(new StampedCloudPoints("obj1", 2, List.of(new CloudPoint(1.0, 2.0))));
        mockData.add(new StampedCloudPoints("obj2", 2, List.of(new CloudPoint(3.0, 4.0))));
        mockData.add(new StampedCloudPoints("obj2", 4, List.of(new CloudPoint(5.0, 6.0))));

        // Use the mock data in the LiDarDataBase
        LiDarDataBase.initializeMockData(mockData);

        // Initialize LiDarWorkerTracker with mock path
        tracker = new LiDarWorkerTracker(1, 2, "mock/path/to/database");
    }

    @Test
    void processEvent_shouldReturnCorrectTrackedObjectsEvent() {
        // Arrange
        DetectObjectsEvent event = new DetectObjectsEvent(
                new StampedDetectedObjects(2, List.of(
                        new DetectedObject("obj1", "Wall"),
                        new DetectedObject("obj2", "Chair")
                ))
        );

        // Act
        TrackedObjectsEvent trackedEvent = tracker.processEvent(event, 4, 2);

        // Assert
        assertNotNull(trackedEvent, "TrackedObjectsEvent should not be null.");
        assertEquals(2, trackedEvent.getTrackedObjects().size(), "TrackedObjects count mismatch.");
        assertEquals("Wall", trackedEvent.getTrackedObjects().get(0).getDescription(), "Description mismatch for object 1.");
        assertEquals("Chair", trackedEvent.getTrackedObjects().get(1).getDescription(), "Description mismatch for object 2.");
    }

    @Test
    void processPendingEvents_shouldProcessEventsAtCorrectTime() {
        // Arrange
        DetectObjectsEvent event1 = new DetectObjectsEvent(
                new StampedDetectedObjects(2, List.of(new DetectedObject("obj1", "Wall")))
        );
        DetectObjectsEvent event2 = new DetectObjectsEvent(
                new StampedDetectedObjects(4, List.of(new DetectedObject("obj2", "Chair")))
        );
        tracker.addToPendingEvents(event1);
        tracker.addToPendingEvents(event2);

        // Act
        List<TrackedObjectsEvent> processedEvents = tracker.processPendingEvents(6);

        // Assert
        assertEquals(2, processedEvents.size(), "Pending events count mismatch.");
        assertEquals("Wall", processedEvents.get(0).getTrackedObjects().get(0).getDescription(), "Description mismatch for event 1.");
        assertEquals("Chair", processedEvents.get(1).getTrackedObjects().get(0).getDescription(), "Description mismatch for event 2.");
    }
}
