package ODT;

import java.util.ArrayList;
import java.util.List;

public class Mission {
    public Mission(String nameOfMission, String nameOfCreator, List<Target> targetToExecute) {
        this.nameOfMission = nameOfMission;
        this.nameOfCreator = nameOfCreator;
        this.targetToExecute = targetToExecute;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "nameOfMission='" + nameOfMission + '\'' +
                ", nameOfCreator='" + nameOfCreator + '\'' +
                ", amountOfTarget=" + amountOfTarget +
                ", amountOfRoot=" + amountOfRoot +
                ", amountOfMiddle=" + amountOfMiddle +
                ", amountOfIndependents=" + amountOfIndependents +
                ", priceOfMission=" + priceOfMission +
                ", workers=" + workers +
                ", statusOfMission=" + statusOfMission +
                ", targetToExecute=" + targetToExecute +
                '}';
    }

    public enum StatusOfMission {Done,Waiting,inProgress}

    private String nameOfMission;
    private String nameOfCreator;
    private int amountOfTarget;
    private int amountOfRoot;
    private int amountOfMiddle;
    private int amountOfIndependents;
    private int priceOfMission;
    private int workers;
    private StatusOfMission statusOfMission;
    private List<Target> targetToExecute = new ArrayList<>();

    public String getNameOfMission() {
        return nameOfMission;
    }
    public void setNameOfMission(String nameOfMission) {
        this.nameOfMission = nameOfMission;
    }

    public String getNameOfCreator() {
        return nameOfCreator;
    }
    public void setNameOfCreator(String nameOfCreator) {
        this.nameOfCreator = nameOfCreator;
    }

    public int getAmountOfTarget() {
        return amountOfTarget;
    }
    public void setAmountOfTarget(int amountOfTarget) {
        this.amountOfTarget = amountOfTarget;
    }

    public int getAmountOfRoot() {
        return amountOfRoot;
    }
    public void setAmountOfRoot(int amountOfRoot) {
        this.amountOfRoot = amountOfRoot;
    }

    public int getAmountOfMiddle() {
        return amountOfMiddle;
    }
    public void setAmountOfMiddle(int amountOfMiddle) {
        this.amountOfMiddle = amountOfMiddle;
    }

    public int getAmountOfIndependents() {
        return amountOfIndependents;
    }
    public void setAmountOfIndependents(int amountOfIndependents) {
        this.amountOfIndependents = amountOfIndependents;
    }

    public int getPriceOfMission() {
        return priceOfMission;
    }
    public void setPriceOfMission(int priceOfMission) {
        this.priceOfMission = priceOfMission;
    }

    public int getWorkers() {
        return workers;
    }
    public void setWorkers(int workers) {
        this.workers = workers;
    }

    public StatusOfMission getStatusOfMission() {
        return statusOfMission;
    }
    public void setStatusOfMission(StatusOfMission statusOfMission) {
        this.statusOfMission = statusOfMission;
    }


}
