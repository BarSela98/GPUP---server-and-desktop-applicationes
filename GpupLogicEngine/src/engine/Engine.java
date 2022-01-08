package engine;

import ODT.Target;
import ODT.information.Information;

import java.util.List;
import java.util.Map;

public interface Engine{
    enum Dependence{DEPENDS_ON , REQUIRED_FOR}
    Map<String, Target> getMap();
    public void whatIf(String t1, List<String>  newList, Dependence dependence);
    Information findAPathBetweenTwoTargets(String t1,String t2 , Dependence d) throws Exception;
    Information circuitDetection(String name)throws Exception;

}
