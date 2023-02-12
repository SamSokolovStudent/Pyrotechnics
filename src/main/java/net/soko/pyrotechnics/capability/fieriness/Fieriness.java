package net.soko.pyrotechnics.capability.fieriness;

public class Fieriness {
    private int fieriness;
    private int fierinessIncrement;
    public static final int MIN_FIERINESS = 0;
    public static final int MAX_FIERINESS = 1024;


    public Fieriness(int fieriness) {
        this.fieriness = fieriness;
    }

    public int getFieriness() {
        return fieriness;
    }

    public void setFieriness(int fieriness) {
        this.fieriness = fieriness;
    }

    public void addFieriness(int amount) {
        this.fieriness = Math.min(fieriness + amount, MAX_FIERINESS);
    }

    public void subtractFieriness(int amount) {
        this.fieriness = Math.max(fieriness - amount, MIN_FIERINESS);
    }

    // Fieriness increment is the total amount of fieriness that will be added to the chunk over time
    // This is used to smooth out the fieriness changes
    public void increaseFierinessIncrement(int amount) {
        this.fierinessIncrement += amount;
    }

    public void decreaseFierinessIncrement(int amount) {
        this.fierinessIncrement -= amount;
    }

    public int getFierinessIncrement() {
        return fierinessIncrement;
    }

}
