package xml;

import engine.Target;
import generated2.GPUPConfiguration;
import generated2.GPUPDescriptor;
import generated2.GPUPTarget;
import generated2.GPUPTargetDependencies;
import target.DependsOnConflict;
import target.RequiredForConflict;
import target.TargetIsExists;
import target.UniqueTarget;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.*;


public class Xmlimpl implements Xml {
    private final GPUPDescriptor gpupDescriptor;
    private final static String JAXB_XML_PACKAGE_NAME = "generated2";

    public Xmlimpl(InputStream inputStream) throws Exception {
        inputStream.reset();
        gpupDescriptor = deserializeFrom(inputStream);
    }


    private static GPUPDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (GPUPDescriptor) u.unmarshal(in);
    }
    public Map<String, Target> makeAMap() throws Exception {
        Target newTarget;
        Map<String, Target> targetsMap = new HashMap<>();

        for (GPUPTarget p : gpupDescriptor.getGPUPTargets().getGPUPTarget()) {
            if (targetsMap.containsKey(p.getName().toUpperCase()) || targetsMap.containsKey(p.getName())) {
                throw new UniqueTarget(p.getName());
            } else {
                Set<String> newSetDependency = new HashSet<>();
                Set<String> newSetRequiredFor = new HashSet<>();

                if (p.getGPUPTargetDependencies() == null)
                    newTarget = new Target(p.getName().toUpperCase(), p.getGPUPUserData(), newSetDependency, newSetRequiredFor);

                else {
                    for (GPUPTargetDependencies.GPUGDependency p2 : p.getGPUPTargetDependencies().getGPUGDependency()) {
                        if (p2.getType().equals("dependsOn"))
                            newSetDependency.add(p2.getValue().toUpperCase());
                        else if (p2.getType().equals("requiredFor"))
                            newSetRequiredFor.add(p2.getValue().toUpperCase());
                    }
                    newTarget = new Target(p.getName().toUpperCase(), p.getGPUPUserData(), newSetDependency, newSetRequiredFor);
                }
                targetsMap.put(newTarget.getName().toUpperCase(), newTarget);
            }
        }
            organizeTheDependencies(targetsMap);
            makeTypeForTargets(targetsMap);
            return targetsMap;
    }
    public void organizeTheDependencies(Map<String, Target> targetMap) throws Exception {
        Set<String> setOfKey = targetMap.keySet();

        for (String targetKey: setOfKey) // organize all the map
        {
            // organize the RequiredFor of all target
            for (String st2 : targetMap.get(targetKey.toUpperCase()).getSetDependsOn()) {
                if (setOfKey.contains(st2.toUpperCase())) { // check if the target is exists in the xml file
                    if (!targetMap.get(st2.toUpperCase()).getSetDependsOn().contains(targetKey.toUpperCase())) // check if there is a conflict
                        targetMap.get(st2.toUpperCase()).addToSetRequiredFor(targetKey.toUpperCase());
                    else throw new DependsOnConflict(targetKey,st2);
                }
                else{
                    throw new TargetIsExists(st2);
                }

            }
            // organize the DependsOn of all target
            for (String st2 : targetMap.get(targetKey.toUpperCase()).getSetRequiredFor()) {
                if (setOfKey.contains(st2.toUpperCase())) { // check if the target is exists in the xml file
                    if (!targetMap.get(st2.toUpperCase()).getSetRequiredFor().contains(targetKey.toUpperCase())) // check if there is a conflict
                        targetMap.get(st2.toUpperCase()).addToSetDependsOn(targetKey.toUpperCase());
                    else throw new RequiredForConflict(targetKey,st2);
                }
                else{
                    throw new TargetIsExists(st2);
                }
            }
        }
    }
    public void makeTypeForTargets(Map<String, Target> targetMap) {
        targetMap.forEach((k,t) ->
                {
                    if (t.getSetRequiredFor().size() == 0 && t.getSetDependsOn().size() == 0)
                        t.SetType(Target.Type.INDEPENDENTS);
                    else if (t.getSetDependsOn().size() == 0)
                        t.SetType(Target.Type.LEAF);
                    else if (t.getSetRequiredFor().size() == 0)
                        t.SetType(Target.Type.ROOT);
                    else
                        t.SetType(Target.Type.MIDDLE);
                }
        );
    }
    public int getPriceForSimulation(){
        for(GPUPConfiguration.GPUPPricing.GPUPTask t: gpupDescriptor.getGPUPConfiguration().getGPUPPricing().getGPUPTask()){
            if (t.getName().equals("Simulation"))
                return t.getPricePerTarget();
        }
        return 0;
    }
    public int getPriceForCompilation(){
        for(GPUPConfiguration.GPUPPricing.GPUPTask t: gpupDescriptor.getGPUPConfiguration().getGPUPPricing().getGPUPTask()){
            if (t.getName().equals("Compilation"))
                return t.getPricePerTarget();
        }
        return 0;
    }
    public String getGraphName(){
        return gpupDescriptor.getGPUPConfiguration().getGPUPGraphName();
    }





    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

}