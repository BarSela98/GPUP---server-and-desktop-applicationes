package ODT;

import engine.EngineImp;
import xml.Xmlimpl;

import java.util.Map;

public class Graph {
    private String graphName;
    private String nameOfCreator;
    private int amountOfTargets;
    private int amountOfRoots;
    private int amountOfMiddles;
    private int amountOfIndependents;
    private int amountOfLevies;
    private int priceForSimulation;
    private int priceForCompilation;
    private Map<String, Target> targetMap;
    private EngineImp engine;

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public String getNameOfCreator() {
        return nameOfCreator;
    }

    public void setNameOfCreator(String nameOfCreator) {
        this.nameOfCreator = nameOfCreator;
    }

    public int getAmountOfTargets() {
        return amountOfTargets;
    }

    public void setAmountOfTargets(int amountOfTargets) {
        this.amountOfTargets = amountOfTargets;
    }

    public int getAmountOfRoots() {
        return amountOfRoots;
    }

    public void setAmountOfRoots(int amountOfRoots) {
        this.amountOfRoots = amountOfRoots;
    }

    public int getAmountOfMiddles() {
        return amountOfMiddles;
    }

    public void setAmountOfMiddles(int amountOfMiddles) {
        this.amountOfMiddles = amountOfMiddles;
    }

    public int getAmountOfIndependents() {
        return amountOfIndependents;
    }

    public void setAmountOfIndependents(int amountOfIndependents) {
        this.amountOfIndependents = amountOfIndependents;
    }

    public int getAmountOfLevies() {
        return amountOfLevies;
    }

    public void setAmountOfLevies(int amountOfLevies) {
        this.amountOfLevies = amountOfLevies;
    }

    public int getPriceForSimulation() {
        return priceForSimulation;
    }

    public void setPriceForSimulation(int priceForSimulation) {
        this.priceForSimulation = priceForSimulation;
    }

    public int getPriceForCompilation() {
        return priceForCompilation;
    }

    public void setPriceForCompilation(int priceForCompilation) {
        this.priceForCompilation = priceForCompilation;
    }

    public Map<String, Target> getTargetMap() {
        return targetMap;
    }

    public void setTargetMap(Map<String, Target> targetMap) {
        this.targetMap = targetMap;
    }

    public Graph(Xmlimpl file, String nameOfCreator)throws Exception{
        Map<String, Target> targetMapTemp;
        targetMapTemp = file.makeAMap();// check if the XML file is proper and crate map (key - target name, val - target) from file
        targetMap = targetMapTemp;
        targetsInformation();
        priceForSimulation = file.getPriceForSimulation();
        priceForCompilation = file.getPriceForCompilation();
        graphName = file.getGraphName();
        this.nameOfCreator = nameOfCreator;
        engine = new EngineImp(targetMap);
    }
    public void targetsInformation() throws Exception{

        amountOfTargets = targetMap.entrySet().size();          // count all the targets in the map

        amountOfLevies = (int) targetMap.entrySet()                                     // count all the levies targets in the map
                .stream()
                .filter(e -> e.getValue().getType().equals(Target.Type.LEAF))
                .count();

        amountOfRoots = (int) targetMap.entrySet()                                      // count all the roots targets in the map
                .stream()
                .filter(e -> e.getValue().getType().equals(Target.Type.ROOT))
                .count();

        amountOfMiddles = (int) targetMap.entrySet()                                    // count all the middles targets in the map
                .stream()
                .filter(e -> e.getValue().getType().equals(Target.Type.MIDDLE))
                .count();

        amountOfIndependents = (int) targetMap.entrySet()                               // count all the independents targets in the map
                .stream()
                .filter(e -> e.getValue().getType().equals(Target.Type.INDEPENDENTS))
                .count();
    }

    public EngineImp getEngine() {
        return engine;
    }
}
