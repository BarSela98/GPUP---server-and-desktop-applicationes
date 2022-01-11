package gpup.servlets.mission.worker;

import gpup.servlets.MissionManger;
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
        String missionNameFromParameter = request.getParameter(MISSIONS_NAME); // if the mission
        String statusFromParameter = request.getParameter(WORKER_STATUS_IN_MISSION); // if the mission
        synchronized (this) {
            MissionManger missionManger = ServletUtils.getMissionManager(getServletContext());
            WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());  ///check if it is worker
            WorkerObject worker = workerManager.getWorkerByName(usernameFromSession);
            worker.changeStatusOfWorkerInMission(statusFromParameter, missionNameFromParameter);
            try {
                if(statusFromParameter.equals("PAUSE"))
                    status = false;
                if(statusFromParameter.equals("DO"))
                    status = true;
                missionManger.getMissionByName(missionNameFromParameter).getWorkerList().replace(usernameFromSession,status);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().print("Successful change new status (" + WORKER_STATUS_IN_MISSION + ") for mission " + missionNameFromParameter);
        }
    }
}
