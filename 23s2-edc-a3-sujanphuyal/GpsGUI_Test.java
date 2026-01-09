import nz.sodium.Stream;
import org.junit.Before;
import org.junit.Test;
import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.*;

public class GpsGUI_Test {
    private GpsGUI gui;
    private Stream<GpsEvent>[] eventStreams;
    private GpsService gpsService;

    @Before
    public void setUp() throws Exception {
        gpsService = new GpsService();
        eventStreams = gpsService.getEventStreams();
        gui = new GpsGUI(eventStreams);
    }


    @Test
    public void testGpsGUIInitialization() {
        assertNotNull(gui);
        assertEquals(2, gui.getContentPane().getComponentCount());
    }

    @Test
    public void testTrackersPanel() {
        Component trackersPanel = gui.getContentPane().getComponent(0);
        assertTrue(trackersPanel instanceof JPanel);
        assertEquals(GpsGUI.NUM_TRACKERS, ((JPanel) trackersPanel).getComponentCount());
    }

    @Test
    public void testTrackerFields() {
        JPanel trackersPanel = (JPanel) gui.getContentPane().getComponent(0);
        for (int i = 0; i < GpsGUI.NUM_TRACKERS; i++) {
            JTextField trackerField = (JTextField) trackersPanel.getComponent(i);
            assertFalse(trackerField.isEditable());
        }
    }

    @Test
    public void testCombinedPanel() {
        Component combinedPanel = gui.getContentPane().getComponent(1);
        assertTrue(combinedPanel instanceof JPanel);
        assertEquals(2, ((JPanel) combinedPanel).getComponentCount());
    }

    @Test
    public void testCombinedDisplay() {
        JScrollPane scrollPane = (JScrollPane) ((JPanel) gui.getContentPane().getComponent(1)).getComponent(0);
        assertTrue(scrollPane.getViewport().getView() instanceof JTextArea);
        JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
        assertFalse(textArea.isEditable());
    }

    @Test
    public void testControlPanel() {
        JPanel controlPanel = (JPanel) ((JPanel) gui.getContentPane().getComponent(1)).getComponent(1);
        assertTrue(controlPanel instanceof JPanel);
        assertEquals(5, controlPanel.getComponentCount());
    }

    @Test
    public void testLatLonFieldsAndLabels() {
        JPanel controlPanel = (JPanel) ((JPanel) gui.getContentPane().getComponent(1)).getComponent(1);

        // Testing Latitude Label and Field
        Component latLabel = controlPanel.getComponent(0);
        assertTrue(latLabel instanceof JLabel);
        assertEquals("Lat: ", ((JLabel) latLabel).getText());

        Component latField = controlPanel.getComponent(1);
        assertTrue(latField instanceof JTextField);

        // Testing Longitude Label and Field
        Component lonLabel = controlPanel.getComponent(2);
        assertTrue(lonLabel instanceof JLabel);
        assertEquals("Lon: ", ((JLabel) lonLabel).getText());

        Component lonField = controlPanel.getComponent(3);
        assertTrue(lonField instanceof JTextField);
    }

    @Test
    public void testSetRestrictionButton() {
        JButton setRestrictionButton = (JButton) ((JPanel) ((JPanel) gui.getContentPane().getComponent(1)).getComponent(1)).getComponent(4);
        assertTrue(setRestrictionButton instanceof JButton);
        assertEquals("Set Restriction", setRestrictionButton.getText());
    }

    // Test for Distance Calculation
    @Test
    public void testDistanceCalculation() {
        // Directly test the calculateDistance method
        GpsEvent event1 = new GpsEvent("Tracker1", 40.7128, -74.0060, 30.0);
        GpsEvent event2 = new GpsEvent("Tracker2", 48.8566, 2.3522, 100.0);
        double distance = gui.calculateDistance(event1, event2);

        // Check the distance against an expected value
        // The expected value should be pre-calculated and known
        double expectedDistance = 5837000; // Example value, replace with actual expected value
        assertEquals(expectedDistance, distance, 1000.0); // Allowing some margin for error
    }


}
