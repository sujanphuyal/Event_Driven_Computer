package practice;

public class RailwaySystemTest {
    public static void main(String[] args) {
        int numStations = 5;
        int numTrains = 3;

        RailwayPetriNet railwaySystem = new RailwayPetriNet(numStations, numTrains);

        // Move trains back and forth between stations
        for (int i = 0; i < 10; i++) {
            int trainIndex = (int) (Math.random() * numTrains);
            int targetStation = (int) (Math.random() * numStations);
            railwaySystem.moveTrain(trainIndex, targetStation);
        }
    }
}


