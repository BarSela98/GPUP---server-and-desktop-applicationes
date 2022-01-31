package gpup.servlets.mission;

import engine.Mission;
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
                if (missionManger.getMissionByName(nameOfMission).getStatusOfMission() == Mission.statusOfMission.DONE){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Mission: "+nameOfMission+" already done");
                    response.getWriter().flush();
                }
                else if (missionManger.getMissionByName(nameOfMission).getStatusOfMission() == Mission.statusOfMission.STOP ){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Mission: "+nameOfMission+" already stop");
                response.getWriter().flush();
                }

                else if (statusOfMission.equals("pause") || statusOfMission.equals("run") || statusOfMission.equals("resume") || statusOfMission.equals("stop")){
                    missionManger.setStatusOfMissionByName(nameOfMission, statusOfMission);
                    if (statusOfMission.equals("run"))
                        missionManger.getMissionByName(nameOfMission).doMission();
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
