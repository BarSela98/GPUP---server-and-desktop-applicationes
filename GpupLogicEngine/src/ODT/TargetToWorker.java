package ODT;

import engine.Target;

public class TargetToWorker {
    private Target target;
    private String nameOfWorker;
    public TargetToWorker(Target target, String nameOfWorker) {
        this.target = target;
        this.nameOfWorker = nameOfWorker;
    }
    public Target getTarget() {
        return target;
    }
    public void setTarget(Target target) {
        this.target = target;
    }
    public String getNameOfWorker() {
        return nameOfWorker;
    }
    public void setNameOfWorker(String nameOfWorker) {
        this.nameOfWorker = nameOfWorker;
    }
}
