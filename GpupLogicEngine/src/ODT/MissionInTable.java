package ODT;

import engine.Mission;
import engine.Target;
import javafx.scene.control.CheckBox;
import utility.Utility;

import java.util.List;

public class MissionInTable extends Mission {
    private CheckBox checkBox;

    public MissionInTable(String nameOfMission, String nameOfCreator,String nameOfGraph, List<Target> targets, Utility.WhichTask whichTask, Utility.TypeOfRunning typeOfRunning, Compilation compilation) {
        super(nameOfMission, nameOfCreator,nameOfGraph, targets, whichTask, typeOfRunning, compilation);
        checkBox = new CheckBox();
    }
    public MissionInTable(String nameOfMission, String nameOfCreator,String nameOfGraph, List<Target> targets, Utility.WhichTask whichTask, Utility.TypeOfRunning typeOfRunning, Simulation simulation) {
        super(nameOfMission, nameOfCreator,nameOfGraph, targets, whichTask, typeOfRunning, simulation);
        checkBox = new CheckBox();
    }
    public MissionInTable(Mission missionsName) {
        super(missionsName);
        checkBox = new CheckBox();
    }
    public void changeMissionInformationAdmin(MissionInTable m) {
        checkBox.setSelected(m.getCheckBox().isSelected());
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public Mission getMissionFromTable(){
        if (super.getWhichTask() == Utility.WhichTask.COMPILATION)
        return new Mission(super.getNameOfMission(), super.getNameOfCreator(),super.getNameOfGraph(), super.getTargets(), super.getWhichTask(), super.getTypeOfRunning(), super.getCompilation());
        else
            return new Mission(super.getNameOfMission(), super.getNameOfCreator(),super.getNameOfGraph(), super.getTargets(), super.getWhichTask(), super.getTypeOfRunning(), super.getSimulation());
    }


}
