package gpup.servlets.worker;

import gpup.servlets.MissionManger;
import gpup.servlets.UserManager;
import gpup.servlets.WorkerManager;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import object.WorkerObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static gpup.constants.Constants.*;

@WebServlet(name = "ChangeStatusForWorkerInMissionServlet", urlPatterns = {"/mission/worker/status"})
public class ChangeStatusForWorkerInMissionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        Boolean status =false;
        response.setContentType("text/plain;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String missionNameFromParameter = request.getParameter(MISSIONS_NAME); // if the mission
        String statusFromParameter = request.getParameter(WORKER_STATUS_IN_MISSION); // if the mission

        if (usernameFromSession == null || usernameFromSession.isEmpty() ||
                userManager.getUserRole(usernameFromSession) == null || !userManager.getUserRole(usernameFromSession).equals("Worker")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("something wrong - only worker can change is status for mission / please login");
        }

        else if (missionNameFromParameter == null || missionNameFromParameter.isEmpty() && statusFromParameter == null || statusFromParameter.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("please insert right parameter");
        }
        else if (! statusFromParameter.equals("DO") && ! statusFromParameter.equals("PAUSE")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("please insert right parameter2");
        }

        else{
        synchronized (this) {
            MissionManger missionManger = ServletUtils.getMissionManager(getServletContext());
            WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());  ///check if it is worker
            WorkerObject worker = workerManager.getWorkerByName(usernameFromSession);
            if(worker.getStatusOfWorkerInMissionMap().containsKey(missionNameFromParameter)) {
                worker.changeStatusOfWorkerInMission(statusFromParameter, missionNameFromParameter);
                try {
                    if (statusFromParameter.equals("PAUSE"))
                        status = false;
                    else if (statusFromParameter.equals("DO"))
                        status = true;
                    missionManger.getMissionByName(missionNameFromParameter).getWorkerList().get(usernameFromSession).setStatus(status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.setStatus(HttpServletResponse.SC_OK);
                response.getOutputStream().print("Successful change new status (" + WORKER_STATUS_IN_MISSION + ") for mission " + missionNameFromParameter);
            }
            else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("please sign for this mission before");
            }
        }
    }
    }
}
