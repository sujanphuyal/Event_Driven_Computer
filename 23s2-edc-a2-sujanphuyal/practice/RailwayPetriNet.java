package practice;

public class RailwayPetriNet {
    private Place[] stations;
    private Transition[] moveTransitions;
    private Train[] trains;

    public RailwayPetriNet(int numStations, int numTrains) {
        stations = new Place[numStations];
        moveTransitions = new Transition[numStations];
        trains = new Train[numTrains];

        for (int i = 0; i < numStations; i++) {
            stations[i] = new Place(0); // Initialize stations with no train
            moveTransitions[i] = new Transition();
        }

        for (int i = 0; i < numTrains; i++) {
            trains[i] = new Train(i, stations.length);
        }
    }

    public void moveTrain(int trainIndex, int targetStationIndex) {
        Train train = trains[trainIndex];
        if (train.isAtStation(targetStationIndex) && moveTransitions[targetStationIndex].canFire(train.getCurrentPlace())) {
            train.move(targetStationIndex);
            System.out.println("Train " + train.getIndex() + " moved to Station " + targetStationIndex);
        } else {
            System.out.println("Train " + train.getIndex() + " can't move to Station " + targetStationIndex);
        }
    }
}
