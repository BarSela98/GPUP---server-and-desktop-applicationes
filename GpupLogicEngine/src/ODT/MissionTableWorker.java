package ODT;

import engine.Mission;

public class MissionTableWorker extends MissionInTable {
    public enum StatusOfWorkerInMission {UNSIGNED,SIGNUP , DO , STOP, PAUSE}
    private Boolean sign;
    private StatusOfWorkerInMission statusOfWorkerInMission;

    public MissionTableWorker(MissionInTable missionInTable) {
        super(missionInTable);
        sign = false;
        statusOfWorkerInMission = StatusOfWorkerInMission.UNSIGNED;
    }
    public MissionTableWorker(Mission m) {
        super(new MissionInTable(m));
        sign = false;
        statusOfWorkerInMission = StatusOfWorkerInMission.UNSIGNED;
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
}
