package practice;

public class Train {
    private int index;
    private int currentStation;

    public Train(int index, int numStations) {
        this.index = index;
        this.currentStation = (int) (Math.random() * numStations); // Random initial station
    }

    public int getIndex() {
        return index;
    }

    public int getCurrentStation() {
        return currentStation;
    }

    public boolean isAtStation(int stationIndex) {
        return currentStation == stationIndex;
    }

    public Place getCurrentPlace() {
        return new Place(currentStation);
    }

    public void move(int newStation) {
        currentStation = newStation;
    }
}

