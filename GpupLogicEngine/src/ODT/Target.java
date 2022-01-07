package ODT;

import java.util.Map;
import java.util.Set;

public class Target {
    public Target(String name, Set<String> setDependsOn, Set<String> setRequiredFor) {
        this.name = name;
        this.setDependsOn = setDependsOn;
        this.setRequiredFor = setRequiredFor;
    }

    public Target() {
    }

    public enum Type {INDEPENDENTS, LEAF, MIDDLE, ROOT}
    public enum Status {Waiting,Success,Warning ,Skipped ,Failure,Frozen}
    private String userData;
    private  String name;
    private Type type;
    private Status status = Status.Frozen;
    private  Set<String> setDependsOn;
    private  Set<String> setRequiredFor;
    private int runTime=0;
    private float successChance=0;
    private float warningChance=0;
    private Map<String, Target> targetMap;
    private String simTimeString;
    private String path;
    private boolean isInQueue;
    private boolean isRunning;
    private long startWaitingTime;
    private String waitingTime;
    private String failReason;
    private boolean notSelected;
    private boolean compile=false;
    private String compileDest;
    private String source;

}
