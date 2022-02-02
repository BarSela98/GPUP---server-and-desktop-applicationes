package gpup.servlets.mission;

import engine.Mission;
import engine.Target;
import gpup.servlets.MissionManger;
import gpup.servlets.UserManager;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static gpup.constants.Constants.MISSIONS_NAME;
import static gpup.constants.Constants.MISSIONS_STATUS;


@WebServlet(name = "ChangeStatusOfMissionServlet", urlPatterns = {"/mission/setstatus"})
public class ChangeStatusOfMissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        try {
        String nameOfMission = request.getParameter(MISSIONS_NAME);
        String statusOfMission = request.getParameter(MISSIONS_STATUS);
        if(usernameFromSession == null || usernameFromSession.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("please login");
        }
        else if(!userManager.getUserRole(usernameFromSession).equals("Admin")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("only admin can select a graph");
        }

        else if (nameOfMission == null || nameOfMission.isEmpty() && statusOfMission == null || statusOfMission.isEmpty())
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        else {

            //    public enum statusOfMission {PAUSE ,  , WAITING, INPROGRESS , run pause resume}
                MissionManger missionManger = ServletUtils.getMissionManager(getServletContext());
                Mission mission = missionManger.getMissionByName(nameOfMission);

            System.out.println(mission.getStatusOfMission());
            System.out.println((mission.getStatusOfMission() == Mission.statusOfMission.DONE && !mission.checkIfAllTargetsSuccess()));
                if (statusOfMission.equals("Scratch") && (mission.getStatusOfMission() == Mission.statusOfMission.STOP || mission.getStatusOfMission() == Mission.statusOfMission.DONE)){
                    for(Target target :mission.getTargets()){
                        target.setStatus(Target.Status.Frozen);
                    }
                    mission.setAmountOfCompleteTarget(0);
                    mission.setTargetInProgress(0);
                    mission.setTargetWaiting(0);
                    mission.getWaitingTargetToExecute().clear();
                    mission.getAmountOfCompleteTarget();
                    missionManger.setStatusOfMissionByName(nameOfMission, "run");
                    mission.doMission();
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("run from scratch");
                    response.getWriter().flush();
                }
                else if (statusOfMission.equals("Incremental") &&
                        (mission.getStatusOfMission() == Mission.statusOfMission.STOP
                        || (mission.getStatusOfMission() == Mission.statusOfMission.DONE && !mission.checkIfAllTargetsSuccess()))){
                    String newName = mission.getNameOfMission()+missionManger.incrementalSize(nameOfMission);
                    List<Target> newList = new ArrayList<>();
                    Mission newMission = new Mission(mission);
                    mission.setTargets(newList);
                    newMission.setNameOfMission(newName);
                    newMission.setProgress("-");
                    newMission.setWorkerList(new HashMap<>());
                    newMission.setAvailableWorker(0);
                    newMission.setSignWorkerSize(0);
                    for (Target target : newMission.getTargets()){
                        Target t = new Target(target, newName);
                        if (target.getStatus() == Target.Status.Failure || target.getStatus() == Target.Status.Skipped)
                            target.setStatus(Target.Status.Frozen);
                        newList.add(t);
                    }
                    newMission.setTargets(newList);
                    newMission.setStatusOfMission(Mission.statusOfMission.INPROGRESS);

                    missionManger.addMission(newMission);
                    newMission.doMission();
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("run - Incremental");
                    response.getWriter().flush();
                }


                else if (mission.getStatusOfMission() == Mission.statusOfMission.DONE){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Mission: "+nameOfMission+" already done");
                    response.getWriter().flush();
                }
                else if (mission.getStatusOfMission() == Mission.statusOfMission.STOP ){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Mission: "+nameOfMission+" already stop");
                response.getWriter().flush();
                }
                else


                if (statusOfMission.equals("pause") || statusOfMission.equals("run") || statusOfMission.equals("resume") || statusOfMission.equals("stop")){
                    missionManger.setStatusOfMissionByName(nameOfMission, statusOfMission);
                    if (statusOfMission.equals("run"))
                        mission.doMission();
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Mission: "+nameOfMission+" change status to "+ statusOfMission);
                    response.getWriter().flush();
                }
                else{
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Mission: "+nameOfMission+" can't change status to "+statusOfMission+" !!!!");
                    response.getWriter().flush();
                }
            }
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.toString());
            response.getWriter().flush();
        }
    }
}
