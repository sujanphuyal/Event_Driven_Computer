package practice;

public class Place {
    private int tokens;

    public Place(int initialTokens) {
        this.tokens = initialTokens;
    }

    public int getTokens() {
        return tokens;
    }

    public void addTokens(int amount) {
        tokens += amount;
    }

    public void removeTokens(int amount) {
        tokens -= amount;
    }
}

