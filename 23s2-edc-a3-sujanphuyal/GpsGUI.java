import nz.sodium.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GpsGUI extends JFrame {

    public static final int NUM_TRACKERS = 10;
    private static final long FIVE_MINUTES_IN_MILLISECONDS = 5 * 60 * 1000;

    public GpsGUI(Stream<GpsEvent>[] eventStreams) {
        setLayout(new GridLayout(2, 1));

        // Simplified Tracking Displays
        JPanel trackersPanel = new JPanel(new GridLayout(NUM_TRACKERS, 1));
        add(trackersPanel);

        // Initialize simplified tracker displays
        JTextField[] trackerFields = new JTextField[NUM_TRACKERS];
        for (int i = 0; i < NUM_TRACKERS; i++) {
            trackerFields[i] = new JTextField();
            trackerFields[i].setEditable(false);
            trackersPanel.add(trackerFields[i]);
        }

        // Initialize buffers for past events
        List<LinkedList<TimedGpsEvent>> eventBuffers = new ArrayList<>();
        for (int i = 0; i < NUM_TRACKERS; i++) {
            eventBuffers.add(new LinkedList<>());
        }

        for (int i = 0; i < NUM_TRACKERS; i++) {
            final int trackerNum = i;

            // Apply FRP to process and display simplified tracking data
            eventStreams[i].map(e -> {
                long currentTime = System.currentTimeMillis();
                TimedGpsEvent timedEvent = new TimedGpsEvent(e, currentTime);

                // Update buffer for this tracker
                LinkedList<TimedGpsEvent> buffer = eventBuffers.get(trackerNum);
                buffer.add(timedEvent);
                buffer.removeIf(te -> currentTime - te.timestamp > FIVE_MINUTES_IN_MILLISECONDS);

                // Calculate total distance travelled in the last 5 minutes
                double totalDistance = 0.0;
                TimedGpsEvent prevEvent = null;
                for (TimedGpsEvent te : buffer) {
                    if (prevEvent != null) {
                        totalDistance += calculateDistance(prevEvent.event, te.event);
                    }
                    prevEvent = te;
                }

                // Return the updated text for the tracker field
                return "Tracker " + trackerNum + " | Lat: " + e.latitude + " | Lon: " + e.longitude +
                        " | Distance in last 5 minutes: " + totalDistance + " meters";
            }).listen(text -> {
                trackerFields[trackerNum].setText(text);

                System.out.println("GUI updated for Tracker " + trackerNum + ": " + text);

            });
        }

        // Combined Display and Control Panel
        JPanel combinedPanel = new JPanel(new BorderLayout());
        add(combinedPanel);

        // Combined Display
        JTextArea combinedDisplay = new JTextArea();
        combinedDisplay.setEditable(false);
        combinedPanel.add(new JScrollPane(combinedDisplay), BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new GridLayout(1, 5));
        combinedPanel.add(controlPanel, BorderLayout.SOUTH);

        JTextField latField = new JTextField();
        JTextField lonField = new JTextField();
        JButton setRestrictionButton = new JButton("Set Restriction");
        JLabel latLabel = new JLabel("Lat: ");
        JLabel lonLabel = new JLabel("Lon: ");

        controlPanel.add(latLabel);
        controlPanel.add(latField);
        controlPanel.add(lonLabel);
        controlPanel.add(lonField);
        controlPanel.add(setRestrictionButton);

        // Combine all input streams into a single stream
        Stream<GpsEvent> allStreams = eventStreams[0];
        for (int i = 1; i < eventStreams.length; i++) {
            allStreams = allStreams.orElse(eventStreams[i]);
        }

        // Initialize restriction settings
        CellSink<Double> latRestriction = new CellSink<>(0.0);
        CellSink<Double> lonRestriction = new CellSink<>(0.0);

        // Set restriction button listener
        setRestrictionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double lat = Double.parseDouble(latField.getText());
                    double lon = Double.parseDouble(lonField.getText());

                    System.out.println("Latitude: " + lat);
                    System.out.println("Longitude: " + lon);

                    latRestriction.send(lat);
                    lonRestriction.send(lon);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GpsGUI.this, "Invalid latitude or longitude", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Apply FRP to filter and display GPS events in range
        allStreams.snapshot(latRestriction, (gpsEvent, lat) -> {
            return new Triple<>(gpsEvent, lat, null);
        }).snapshot(lonRestriction, (triple, lon) -> {
            GpsEvent gpsEvent = triple.first;
            double lat = triple.second;
            return new Triple<>(gpsEvent, lat, lon);
        }).filter(triple -> {
            GpsEvent gpsEvent = triple.first;
            double lat = triple.second;
            double lon = triple.third;
            return gpsEvent.latitude >= lat - 0.5 && gpsEvent.latitude <= lat + 0.5 &&
                    gpsEvent.longitude >= lon - 0.5 && gpsEvent.longitude <= lon + 0.5;
        }).map(triple -> {
            GpsEvent gpsEvent = triple.first;
            return gpsEvent.toString();
        }).listen(text -> {
            combinedDisplay.append(text + "\n");

            System.out.println("Combined Display updated: " + text);

        });
    }

    public double calculateDistance(GpsEvent event1, GpsEvent event2) {
        // Calculate the distance between two GpsEvent objects
        // You can use the Haversine formula to calculate the distance between two points on the Earth's surface
        double lat1 = Math.toRadians(event1.latitude);
        double lon1 = Math.toRadians(event1.longitude);
        double lat2 = Math.toRadians(event2.latitude);
        double lon2 = Math.toRadians(event2.longitude);
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371 * 1000; // Earth's radius in meters
        return c * r;
    }

    public static void main(String[] args) {
        // Initialize GPS Service
        GpsService gpsService = new GpsService();

        // Retrieve streams of GPS events
        Stream<GpsEvent>[] eventStreams = gpsService.getEventStreams();

        // Initialize GUI and pass the event streams
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GpsGUI gui = new GpsGUI(eventStreams);
                gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gui.setTitle("GPS Tracker");
                gui.pack();
                gui.setVisible(true);
            }
        });
    }
}

class TimedGpsEvent {
    public final GpsEvent event;
    public final long timestamp;

    public TimedGpsEvent(GpsEvent event, long timestamp) {
        this.event = event;
        this.timestamp = timestamp;
    }
}

class Triple<T, U, V> {
    public final T first;
    public final U second;
    public final V third;

    public Triple(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
