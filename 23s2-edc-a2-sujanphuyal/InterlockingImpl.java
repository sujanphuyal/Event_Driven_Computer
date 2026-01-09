import java.util.*;
public class InterlockingImpl implements Interlocking {
    private Map<String, Train> trains = new HashMap<>();
    private Map<Integer, String> trackOccupancy = new HashMap<>();

    private static final Set<Integer> southEntryPoints = new HashSet<>(Arrays.asList(1, 3));
    private static final Set<Integer> southExitPoints = new HashSet<>(Arrays.asList(4, 8, 9, 11));
    private static final Set<Integer> northEntryPoints = new HashSet<>(Arrays.asList(4, 9, 10, 11));
    private static final Set<Integer> northExitPoints = new HashSet<>(Arrays.asList(2, 3));

    @Override
    public void addTrain(String trainName, int entryTrackSection, int destinationTrackSection) {
        if (trains.containsKey(trainName)) {
            throw new IllegalArgumentException("Train name already in use.");
        }
        if (trackOccupancy.containsKey(entryTrackSection)) {
            throw new IllegalStateException("Entry track section is already occupied.");
        }
        if (!isValidEntryAndExit(entryTrackSection, destinationTrackSection)) {
            throw new IllegalArgumentException("Invalid entry or exit section.");
        }

        Train newTrain = new Train(trainName, entryTrackSection, destinationTrackSection);
        trains.put(trainName, newTrain);
        trackOccupancy.put(entryTrackSection, trainName);
    }

    @Override
    public int moveTrains(String[] trainNames) {
        int movedTrains = 0;
        for (String trainName : trainNames) {
            Train train = trains.get(trainName);
            if (train == null) {
                throw new IllegalArgumentException("Train " + trainName + " does not exist.");
            }
            int nextSection = getNextSection(train);
            if (canMoveToSection(nextSection)) {
                trackOccupancy.remove(train.getCurrentSection());
                train.setCurrentSection(nextSection);
                trackOccupancy.put(nextSection, trainName);
                movedTrains++;
            }
            if (train.getDestinationSection() == nextSection) {
                trains.remove(trainName);
                trackOccupancy.remove(nextSection);
            }
        }
        return movedTrains;
    }

    @Override
    public String getSection(int trackSection) {
        return trackOccupancy.get(trackSection);
    }

    @Override
    public int getTrain(String trainName) {
        Train train = trains.get(trainName);
        return train != null ? train.getCurrentSection() : -1;
    }

    private boolean canMoveToSection(int nextSection) {
        return !trackOccupancy.containsKey(nextSection);
    }

    private boolean isValidEntryAndExit(int entry, int exit) {
        return (southEntryPoints.contains(entry) && southExitPoints.contains(exit)) ||
                (northEntryPoints.contains(entry) && northExitPoints.contains(exit));
    }

    private int getNextSection(Train train) {
        if (train.isSouthbound()) {
            return train.getCurrentSection() + 1;
        } else {
            return train.getCurrentSection() - 1;
        }
    }

    // The main method for demonstration purposes
    public static void main(String[] args) {
        InterlockingImpl interlockingSystem = new InterlockingImpl();

        try {
            // Adding trains to the system
            interlockingSystem.addTrain("Train1", 1, 11);
            interlockingSystem.addTrain("Train2", 3, 8);

            // Moving trains
            int movedTrains = interlockingSystem.moveTrains(new String[]{"Train1", "Train2"});
            System.out.println("Moved trains: " + movedTrains);

            // Querying train and track positions
            System.out.println("Train Train1 is at section: " + interlockingSystem.getTrain("Train1"));
            System.out.println("Section 1 is occupied by: " + interlockingSystem.getSection(1));

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static class Train {
        private final String trainName;
        private int currentSection;
        private final int destinationSection;
        private final boolean southbound;

        public Train(String trainName, int currentSection, int destinationSection) {
            this.trainName = trainName;
            this.currentSection = currentSection;
            this.destinationSection = destinationSection;
            this.southbound = southEntryPoints.contains(currentSection);
        }

        public String getTrainName() {
            return trainName;
        }

        public int getCurrentSection() {
            return currentSection;
        }

        public void setCurrentSection(int currentSection) {
            this.currentSection = currentSection;
        }

        public int getDestinationSection() {
            return destinationSection;
        }

        public boolean isSouthbound() {
            return southbound;
        }
    }
}
