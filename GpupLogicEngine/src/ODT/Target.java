package ODT;

import java.util.Set;

public class Target {

    /** ctor */
    public  Target(String name , String userData , Set<String> setDependsOn , Set<String>setRequiredFor) {
        this.name = name;
        this.userData = userData;
        this.setDependsOn = setDependsOn;
        this.setRequiredFor = setRequiredFor;
    }

    /** Get name
     * @return name of target
     */
    public String getName() {
        return name;
    }

    /** Get user data
     * @return data of target
     */
    public String getUserData() {
        return userData;
    }

    /** Get all depends-on
     * @return set of depends-on string
     */
    public Set<String> getSetDependsOn() {
        return setDependsOn;
    }

    /** Get required-for
     * @return set of required-for string
     */
    public Set<String> getSetRequiredFor() {
        return setRequiredFor;
    }

    /** Get type
     * @return type of target
     */
    public Type getType() {
        return type;
    }

    /** Get status
     * @return status of target
     */
    public Status getStatus() {
        return status;
    }

    /** Add to set depends-on
     * @param st - target name to add
     */
    public void addToSetDependsOn(String st) {setDependsOn.add(st);}

    /** Add to set required-for
     * @param st - target name to add
     */
    public void addToSetRequiredFor(String st) {setRequiredFor.add(st);}

    /** Set type
     * @param t- new target type
     */
    public void SetType(Type t) {
        this.type = t;
    }

    public void SetUserData(String s){this.userData = s;}

    protected void SetStatus(Status status) {
        this.status = status;
    }


    public enum Type {INDEPENDENTS, LEAF, MIDDLE, ROOT}
    public enum Status {Waiting,Success,Warning ,Skipped ,Failure,Frozen}
    private String userData;
    private  String name;
    private Type type;
    private Status status = Status.Frozen;
    private  Set<String> setDependsOn;
    private  Set<String> setRequiredFor;

}
