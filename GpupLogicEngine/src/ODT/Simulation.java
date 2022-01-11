package ODT;

public class Simulation{
        private int priceOfSimulation;
        private int time;
        boolean random;
        float success;
        float warning;

    public Simulation(int priceOfSimulation, int time, boolean random, float success, float warning) {
        this.priceOfSimulation = priceOfSimulation;
        this.time = time;
        this.random = random;
        this.success = success;
        this.warning = warning;
    }

    public int getPriceOfSimulation() {
        return priceOfSimulation;
    }
    public void setPriceOfSimulation(int priceOfSimulation) {
        this.priceOfSimulation = priceOfSimulation;
    }

    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }

    public boolean isRandom() {
        return random;
    }
    public void setRandom(boolean random) {
        this.random = random;
    }

    public float getSuccess() {
        return success;
    }
    public void setSuccess(float success) {
        this.success = success;
    }

    public float getWarning() {
        return warning;
    }
    public void setWarning(float warning) {
        this.warning = warning;
    }
}
