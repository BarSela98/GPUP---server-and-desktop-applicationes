package gpup.servlets.mission;

import com.google.gson.Gson;
import engine.Mission;
import engine.Target;
import gpup.servlets.MissionManger;
import gpup.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import object.WorkerObject;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static gpup.constants.Constants.USERNAME;

@WebServlet(name = "UpdateTargetInformationServlet", urlPatterns = {"/mission/update/target"})
public class UpdateTargetInformationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        System.out.println("UpdateTargetInformationServlet 1");
        response.setContentType("text/plain;charset=UTF-8");
        String workerName = request.getParameter(USERNAME);
        String json = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
                Collectors.joining("\n"));
        Target target = new Gson().fromJson(json, Target.class);
        MissionManger m = ServletUtils.getMissionManager(getServletContext());
        m.updateTarget(target);
        WorkerObject workerObject = ServletUtils.getWorkerManager(getServletContext()).getWorkerByName(workerName);
        workerObject.addToCompleteTarget(target);
        try {
            Mission mission = ServletUtils.getMissionManager(getServletContext()).getMissionByName(target.getMission());
            boolean bool = workerObject.isAvailable(target.getMission());
            if (bool){
                mission.getWorkerList().get(workerName).setStatus(bool);
                mission.setAvailableWorker( mission.getAvailableWorker()+1);
            }
            if (mission.getTargetInProgress() > 0)
                mission.setTargetInProgress(mission.getTargetInProgress()-1);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(target.getName() + " (Target) update in mission and complete target in "+workerName+" (worker)");
            response.getWriter().flush();
            System.out.println("UpdateTargetInformationServlet 2");

        }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
            response.getWriter().flush();
        }

    }
}
