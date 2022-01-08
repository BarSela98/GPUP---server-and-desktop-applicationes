package utility;

public class Utility {
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public enum StatusOfMission {Done,Waiting,inProgress}
    public enum Dependence {REQUIRED_FOR, DEPENDS_ON}
}
