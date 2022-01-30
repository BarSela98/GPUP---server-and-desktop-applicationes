package engine;


import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;

import java.util.Random;
import java.util.Set;

//
public class Target implements Serializable,Runnable {
    public Target() {

    }

    public void updateInfo(Target tar) {
        this.status = tar.status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public enum Type {INDEPENDENTS, LEAF, MIDDLE, ROOT}
    public enum Status {Waiting, Success, Warning, Skipped, Failure, Frozen}
    private String userData;
    private String name;
    private Type type;
    private Status status = Status.Frozen;
    private Set<String> setDependsOn;
    private Set<String> setRequiredFor;
    private int runTime = 0;
    private float successChance = 0;
    private float warningChance = 0;
    private String simTimeString="00:00:00";
    private String path;
    private boolean isInQueue;
    private boolean isRunning;
    private long startWaitingTime;
    private String waitingTime;
    private String failReason;
    private boolean notSelected;
    private boolean compile = false;
    private String compileDest;
    private String source;
    private String Mission;
    private int price;

    /**
     * ctor
     */
    public Target(String name, String userData, Set<String> setDependsOn, Set<String> setRequiredFor) {
        this.name = name;
        this.userData = userData;
        this.setDependsOn = setDependsOn;
        this.setRequiredFor = setRequiredFor;
    }
    public Target(String name, Set setDependsOn, Set setRequiredFor) {
        this.name = name;
        this.setDependsOn = setDependsOn;
        this.setRequiredFor = setRequiredFor;
    }


    public String getMission() {
        return Mission;
    }
    public void setMission(String mission) {
        Mission = mission;
    }

    /** Get name
     * @return name of target
     */
    public String getName () {
        return name;
    }

    /** Get user data
     * @return data of target
     */
    public String getUserData () {
        return userData;
    }

    /** Get all depends-on
     * @return set of depends-on string
     */
    public Set<String> getSetDependsOn () {
        return setDependsOn;
    }

    /** Get required-for
     * @return set of required-for string
     */
    public Set<String> getSetRequiredFor () {
        return setRequiredFor;
    }

    /** Get type
     * @return type of target
     */
    public Type getType () {
        return type;
    }

    /** Get status
     * @return status of target
     */
    public Status getStatus () {
        return status;
    }

    /** Add to set depends-on
     * @param st - target name to add
     */
    public void addToSetDependsOn (String st){
        setDependsOn.add(st);
    }

    /** Add to set required-for
     * @param st - target name to add
     */
    public void addToSetRequiredFor (String st){
        setRequiredFor.add(st);
    }

    /** Set type
     * @param t- new target type
     */
    public void SetType (Type t){
        this.type = t;
    }
    public void SetUserData (String s){
        this.userData = s;
    }
    protected void SetStatus (Status status){
        this.status = status;
    }
    public boolean isRunning () {
        return isRunning;
    }
    public void setRunning ( boolean running){
        isRunning = running;
    }
    public void setUserData (String userData){
        this.userData = userData;
    }
    public void setName (String name){
        this.name = name;
    }
    public void setType (Type type){
        this.type = type;
    }
    public void setStatus (Status status){
        this.status = status;
    }
    public void setArrayDependsOn (Set setDependsOn){
        this.setDependsOn = setDependsOn;
    }
    public void setRequiredFor (Set setRequiredFor){
        this.setRequiredFor = setRequiredFor;
    }
    public int getRunTime () {
        return runTime;
    }
    public void setRunTime ( int runTime){
        this.runTime = runTime;
    }
    public float getSuccessChance () {
        return successChance;
    }
    public void setSuccessChance ( float successChance){
        this.successChance = successChance;
    }
    public float getWarningChance () {
        return warningChance;
    }
    public void setWarningChance ( float warningChance){
        this.warningChance = warningChance;
    }
    public String getSimTimeString () {
        return simTimeString;
    }
    public void setSimTimeString (String simTimeString){
        this.simTimeString = simTimeString;
    }
    public String getPath () {
        return path;
    }
    public void setPath (String path){
        this.path = path;
    }
    public boolean isInQueue () {
        return isInQueue;
    }
    public void setInQueue ( boolean inQueue){
        isInQueue = inQueue;
    }
    public long getStartWaitingTime () {
        return startWaitingTime;
    }
    public void setStartWaitingTime ( long startWaitingTime){
        this.startWaitingTime = startWaitingTime;
    }
    public String getWaitingTime () {
        long temp= System.currentTimeMillis()-startWaitingTime;
        long millis = temp % 1000;
        long second = (temp / 1000) % 60;
        long minute = (temp / (1000 * 60)) % 60;
        long hour = (temp / (1000 * 60 * 60)) % 24;
        waitingTime=String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
        return waitingTime;
    }
    public void setWaitingTime (String waitingTime){
        this.waitingTime = waitingTime;
    }
    public String getFailReason () {
        return failReason;
    }
    public void setFailReason (String failReason){
        this.failReason = failReason;
    }
    public boolean isNotSelected () {
        return notSelected;
    }
    public void setNotSelected ( boolean notSelected){
        this.notSelected = notSelected;
    }
    public boolean isCompile () {
        return compile;
    }
    public void setCompile ( boolean compile){
        this.compile = compile;
    }
    public String getCompileDest () {
        return compileDest;
    }
    public void setCompileDest (String compileDest){
        this.compileDest = compileDest;
    }
    public String getSource () {
        return source;
    }
    public void setSource (String source){
        this.source = source;
    }
    public void run () {
        //comment blocks in this part needs to be moved to engine
        isRunning=true;
        try {
            if (compile) {//compile task
                String temp = "";
                char c = '\\';
                for (int i = 0; i < userData.length(); i++) {
                    if (userData.charAt(i) == '.') {
                        temp += c;
                    } else {
                        temp += userData.charAt(i);
                    }
                }

                long startTime = System.currentTimeMillis();//sim target and keep time of sim
                Runtime rt = Runtime.getRuntime();
                String src = source + c + temp + ".java";
                String[] strings = {"javac", "-d", compileDest, "-cp", compileDest, src};
                Process p = rt.exec(strings);
                p.waitFor();//wait for process to finish
                int res = p.exitValue();
                long simTime = System.currentTimeMillis() - startTime;
                //turn simTime to string
                long millis = simTime % 1000;
                long second = (simTime / 1000) % 60;
                long minute = (simTime / (1000 * 60)) % 60;
                long hour = (simTime / (1000 * 60 * 60)) % 24;
                simTimeString = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
                if (res != 0) {//compilation failed
                    this.status = Status.Failure;
                } else {//success
                    this.status = Status.Success;
                }

            } else {//simulation task
                long startTime = System.currentTimeMillis();//sim target and keep time of sim
                Thread.sleep((long) runTime);
                long simTime = System.currentTimeMillis() - startTime;
                //turn simTime to string
                long millis = simTime % 1000;
                long second = (simTime / 1000) % 60;
                long minute = (simTime / (1000 * 60)) % 60;
                long hour = (simTime / (1000 * 60 * 60)) % 24;
                simTimeString = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
                Random r = new Random();
                float successRand = r.nextFloat();
                float warningRand = r.nextFloat();
                if (successChance >= successRand) {
                    if (warningChance >= warningRand) {
                        this.status = Status.Warning;
                    } else {
                        this.status = Status.Success;
                    }
                } else {
                    this.status = Status.Failure;
                }
                path=path+"\\" +name;
                File f = new File(path);
                f.createNewFile();
                FileWriter w = new FileWriter(path);
                w.write("Target name: " + this.name + "\n\r" +
                        "Target result: " + this.status.name() + "\n\r" +
                        "Target time :  \n\r");
                System.out.println("Target name: " + this.name + "\n\r" +
                        "Target result: " + this.status.name() + "\n\r" +
                        "Target time :  \n\r");
                w.close();
            /*
            if (!this.setRequiredFor.isEmpty()) {
                for (String s : setRequiredFor) {
                    if (targetMap.get(s).status == Status.Frozen)
                        targetMap.get(s).status = Status.Waiting;
                }
            }
             */
            }/*

        isRunning = false;
        isInQueue = false;
        engineImpl.decrementWorkingThreads();
        Size s= new Size();
        s.subSize();
        infor = new infoThread(infoThread.InOrOut.OUT, System.currentTimeMillis() , engineImpl.getWorkingThreads(), s.getSize() );
        */
        isRunning=false;
        } catch (Exception e) {

        }

    }

}
