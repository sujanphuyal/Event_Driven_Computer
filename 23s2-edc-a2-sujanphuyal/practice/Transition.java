package practice;

public class Transition {
    public boolean canFire(Place inputPlace) {
        // Implement your logic to check if the transition can fire
        return inputPlace.getTokens() > 0;
    }
}

