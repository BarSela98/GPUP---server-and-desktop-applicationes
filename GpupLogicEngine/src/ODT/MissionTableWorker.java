package ODT;

import engine.Mission;

public class MissionTableWorker extends MissionInTable {
    public enum StatusOfWorkerInMission {UNSIGNED,SIGNUP , DO , STOP, PAUSE}
    private Boolean sign;
    private StatusOfWorkerInMission statusOfWorkerInMission;
    private int targetComplete;
    private String task;
    public MissionTableWorker(MissionInTable missionInTable) {
        super(missionInTable);
        sign = false;
        targetComplete= 0;
        statusOfWorkerInMission = StatusOfWorkerInMission.UNSIGNED;
        if (missionInTable.getCompilation() != null)
            task = "Compilation";
        else  if (missionInTable.getSimulation() != null)
            task = "Simulation";
    }
    public MissionTableWorker(Mission m) {
        super(new MissionInTable(m));
        sign = false;
        targetComplete= 0;
        statusOfWorkerInMission = StatusOfWorkerInMission.UNSIGNED;
        if (m.getCompilation() != null)
            task = "Compilation";
        else  if (m.getSimulation() != null)
            task = "Simulation";
    }
    public void changeInformation(MissionTableWorker m) {
        this.getCheckBox().setSelected(m.getCheckBox().isSelected());
        this.statusOfWorkerInMission = m.getStatusOfWorkerInMission();
        this.sign = m.getSign();

    }
    public Boolean getSign() {
        return sign;
    }
    public void setSign(Boolean sign) {
        this.sign = sign;
    }
    public StatusOfWorkerInMission getStatusOfWorkerInMission() {
        return statusOfWorkerInMission;
    }
    public void setStatusOfWorkerInMission(StatusOfWorkerInMission statusOfWorkerInMission) {
        this.statusOfWorkerInMission = statusOfWorkerInMission;
    }
    public int getTargetComplete() {
        return targetComplete;
    }
    public void setTargetComplete(int targetComplete) {
        this.targetComplete = targetComplete;
    }
    public String getTask() {
        return task;
    }
    public void setTask(String task) {
        this.task = task;
    }
}
