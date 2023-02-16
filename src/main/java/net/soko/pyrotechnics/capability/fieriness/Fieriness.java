package net.soko.pyrotechnics.capability.fieriness;

public class Fieriness {
    private float fieriness;

    public Fieriness(int fieriness) {
        this.fieriness = fieriness;
    }

    public float getFieriness() {
        return fieriness;
    }

    public void setFieriness(float fieriness) {
        this.fieriness = fieriness;
    }

    public void addFieriness(float amount) {
        this.fieriness += amount;
        if (this.fieriness < 0) {
            this.fieriness = 0;
        }
    }

    public void decay() {
        this.fieriness = this.fieriness * 0.98f;
        if (fieriness < 1) {
            this.fieriness = 0;
        }
    }

}
