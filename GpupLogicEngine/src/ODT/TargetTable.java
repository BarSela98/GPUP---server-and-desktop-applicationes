package ODT;

import javafx.scene.control.CheckBox;

public class TargetTable  extends Target{
    private CheckBox checkBoxPath;
    private CheckBox checkBoxTask;
    private Integer directRequiredForTableCol;
    private Integer totalRequiredForTableCol;
    private Integer directDependsOnTableCol;
    private Integer totalDependsOnTableCol;
    private Integer serialSetTableCol;

    public  TargetTable(Target t) {
    }

}
