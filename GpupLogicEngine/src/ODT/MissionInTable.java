package ODT;

import engine.Mission;
import engine.Target;
import javafx.scene.control.CheckBox;
import utility.Utility;

import java.util.List;

public class MissionInTable extends Mission {
    private CheckBox checkBox;
    private Boolean sign = false;

    public MissionInTable(String nameOfMission, String nameOfCreator, List<Target> targets, Utility.WhichTask whichTask, Utility.TypeOfRunning typeOfRunning, Compilation compilation) {
        super(nameOfMission, nameOfCreator, targets, whichTask, typeOfRunning, compilation);
        checkBox = new CheckBox();
    }
    public MissionInTable(String nameOfMission, String nameOfCreator, List<Target> targets, Utility.WhichTask whichTask, Utility.TypeOfRunning typeOfRunning, Simulation simulation) {
        super(nameOfMission, nameOfCreator, targets, whichTask, typeOfRunning, simulation);
        checkBox = new CheckBox();
    }
    public MissionInTable(Mission missionsName) {
        super(missionsName);
        checkBox = new CheckBox();
    }

    public Boolean getSign() {
        return sign;
    }
    public void setSign(Boolean sign) {
        this.sign = sign;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public Mission getMissionFromTable(){
        if (super.getWhichTask() == Utility.WhichTask.COMPILATION)
        return new Mission(super.getNameOfMission(), super.getNameOfCreator(), super.getTargets(), super.getWhichTask(), super.getTypeOfRunning(), super.getCompilation());
        else
            return new Mission(super.getNameOfMission(), super.getNameOfCreator(), super.getTargets(), super.getWhichTask(), super.getTypeOfRunning(), super.getSimulation());
    }
    public void changeInformation(MissionInTable missionInTable) {
        checkBox.setSelected(missionInTable.getCheckBox().isSelected());
        this.sign = missionInTable.getSign();
    }
}
