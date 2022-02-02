package ODT;

import engine.Target;

import java.util.List;

public class TargetInWorkerAndAmountOfThread {
    private  List<Target> TargetInWorker;
    private  int availableThread;
    public TargetInWorkerAndAmountOfThread( List<Target> TargetInWorker ,  int availableThread){
        this.TargetInWorker = TargetInWorker;
        this.availableThread = availableThread;
    }
    public List<Target> getTargetInWorker() {
        return TargetInWorker;
    }
    public void setTargetInWorker(List<Target> targetInWorker) {
        TargetInWorker = targetInWorker;
    }
    public int getAvailableThread() {
        return availableThread;
    }
    public void setAvailableThread(int availableThread) {
        this.availableThread = availableThread;
    }
}
