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

@WebServlet(name = "SignForMissionServlet", urlPatterns = {"/mission/worker/sign"})
public class SignForMissionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
///check if is worker and not sign for mission yet (add ------------------------------------------------------------------)
        String usernameFromSession = SessionUtils.getUsername(request);
        String signFromParameter = request.getParameter(SIGN_UNSIGNED_WORKER);
        String missionNameFromParameter = request.getParameter(MISSIONS_NAME);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession == null || usernameFromSession.isEmpty() ||
                userManager.getUserRole(usernameFromSession) == null){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }

        else if(!userManager.getUserRole(usernameFromSession).equals("Worker")){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("only worker can sign/unsigned for mission");
        }

        else if (missionNameFromParameter == null || missionNameFromParameter.isEmpty() && signFromParameter == null || signFromParameter.isEmpty()){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("please insert right parameter");
        }

        else synchronized (this) {
            MissionManger missionManger = ServletUtils.getMissionManager(getServletContext());
            WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());  ///check if it is worker
            WorkerObject worker = workerManager.getWorkerByName(usernameFromSession);
            if(!missionManger.isMissionNameExists(missionNameFromParameter)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("sorry but this mission name not exists");
            }
            else if(signFromParameter.equals("sign")){
                missionManger.signForMissionByName(worker.getNameOfWorker(),missionNameFromParameter);
                worker.getStatusOfWorkerInMissionMap().put(missionNameFromParameter, WorkerObject.StatusOfWorkerInMission.PAUSE);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getOutputStream().print("Successful sign for mission "+ missionNameFromParameter);

                try {
                    System.out.println(missionNameFromParameter + "\n " + missionManger.getMissionByName(missionNameFromParameter).getTargets());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(signFromParameter.equals("remove")){
                missionManger.removeWorkerForMissionByName(usernameFromSession,missionNameFromParameter);
                worker.getStatusOfWorkerInMissionMap().remove(missionNameFromParameter);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getOutputStream().print("Successful remove for mission");
            }
            else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("please insert right parameter");
            }
        }

    }
}
