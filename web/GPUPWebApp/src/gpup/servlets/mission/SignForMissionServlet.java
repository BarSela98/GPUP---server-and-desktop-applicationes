package gpup.servlets.mission;

import gpup.servlets.MissionManger;
import gpup.servlets.WorkerManager;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static gpup.constants.Constants.*;

@WebServlet(name = "SignForMissionServlet", urlPatterns = {"/mission/worker/sign"})
public class SignForMissionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
///check if is worker and not sign for mission yet (add ------------------------------------------------------------------)
        String usernameFromSession = SessionUtils.getUsername(request);
        String signFromParameter = request.getParameter(SIGN_UNSIGNED_WORKER);
        String missionNameFromParameter = request.getParameter(MISSIONS_NAME); // if the mission
        synchronized (this) {
            MissionManger missionManger = ServletUtils.getMissionManager(getServletContext());
            WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());  ///check if it is worker
            if(signFromParameter.equals("sign")){
            missionManger.signForMissionByName(workerManager.getWorkerByName(usernameFromSession),missionNameFromParameter);
            }
            else if(signFromParameter.equals("remove")){
                missionManger.removeWorkerForMissionByName(usernameFromSession,missionNameFromParameter);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().print("Successful sign for mission");
        }

    }
}
