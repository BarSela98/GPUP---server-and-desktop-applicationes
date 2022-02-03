package engine;

import ODT.information.CircuitDetectionInfo;
import ODT.information.Information;
import ODT.information.PathBetweenTwoTargetsInfo;
import target.TargetIsExists;
import utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EngineImp {
    private Map<String, Target> targetMap;

    public EngineImp(Map<String, Target> targetMap) {
        this.targetMap = targetMap;
    }
    public Map<String, Target> getTargetMap() {
        return targetMap;
    }
    public void setTargetMap(Map<String, Target> targetMap) {
        this.targetMap = targetMap;
    }
    /**
     * Specific target information
     *
     * @return all the information between the targets:
     * target names
     * all the path between the targets with a specific dependence
     * @throws TargetIsExists on if target isn't exists
     */
    public PathBetweenTwoTargetsInfo findAPathBetweenTwoTargets(String t1, String t2, Utility.Dependence dependence) throws Exception {
        Target target1 = targetMap.get(t1);
        Target target2 = targetMap.get(t2);
        Intx i = new Intx();
        i.x = 0;

        List<Targets> list = new ArrayList<>();
        list.add(new Targets());

        // check if the two targets are in the map
        if (null == target1)
            throw new TargetIsExists(t1);
        if (null == target2)
            throw new TargetIsExists(t2);

        if (dependence == Utility.Dependence.REQUIRED_FOR && target1.getSetRequiredFor().size() != 0)
            findPathBetweenTwoTargetsHelper(target1, target2, list, i, dependence);
        else if (dependence == Utility.Dependence.DEPENDS_ON && target1.getSetDependsOn().size() != 0)
            findPathBetweenTwoTargetsHelper(target1, target2, list, i, dependence);

        return new PathBetweenTwoTargetsInfo(t1, t2, dependence.name(), list);
    }
    /** Circuit detection info
     *  @return all the information between the targets:
     *  target names
     *  all the cycle paths
     * @tRERhrows TargetIsExists on if target isn't exists
     */
    public Information circuitDetection(String name)throws Exception {
        PathBetweenTwoTargetsInfo info = findAPathBetweenTwoTargets(name,name,Utility.Dependence.DEPENDS_ON);
        return new CircuitDetectionInfo(name , info.getPaths());
    }
    /**
     * Specific target information - helper (recursion)
     */
    public void findPathBetweenTwoTargetsHelper(Target t1, Target t2, List<Targets> listSt, Intx index, Utility.Dependence dependence) {

        Set<String> tOneSet;

        if (Utility.Dependence.DEPENDS_ON == dependence)
            tOneSet = t1.getSetDependsOn();
        else
            tOneSet = t1.getSetRequiredFor();


        if (tOneSet.size() == 0) {
            listSt.get(index.x).getTargetsList().remove(listSt.get(index.x).getTargetsList().size() - 1);
        } else {
            for (String st : tOneSet) {  /// search in all DEPENDS_ON or REQUIRED_FOR for each target
                if (st.equals(t2.getName())) {
                    listSt.add(new Targets()); /// the next list
                    listSt.get(index.x).setFind(true); // find
                    listSt.get(listSt.size() - 1).getTargetsList().addAll(listSt.get(index.x).getTargetsList());
                    ++index.x;
                } else if (!listSt.get(index.x).getTargetsList().contains(st)) { /// skip on direct dependency and handle cycle
                    listSt.get(index.x).getTargetsList().add(st);
                    findPathBetweenTwoTargetsHelper(targetMap.get(st), t2, listSt, index, dependence);
                }
            }

            if (listSt.get(index.x).getTargetsList().size() != 0)
                listSt.get(index.x).getTargetsList().remove(listSt.get(index.x).getTargetsList().size() - 1);
        }
    }
    public Map<String, Target> getMap() {
        return targetMap;
    }
    public void whatIf(String target, List<String> newList, Utility.Dependence dependence) {
        Set<String> tOneSet;
        if (!newList.contains(target.toUpperCase())) {
            newList.add(target.toUpperCase());
            if (Utility.Dependence.DEPENDS_ON == dependence)
                tOneSet = targetMap.get(target.toUpperCase()).getSetDependsOn();
            else
                tOneSet = targetMap.get(target.toUpperCase()).getSetRequiredFor();

            if (tOneSet.size() != 0) {
                for (String st2 : tOneSet) {
                    whatIf(targetMap.get(st2).getName(), newList, dependence);
                }
            }
        }
    }
}

