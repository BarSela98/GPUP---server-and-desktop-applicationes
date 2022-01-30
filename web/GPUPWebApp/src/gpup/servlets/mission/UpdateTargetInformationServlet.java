package gpup.servlets.mission;

import engine.Target;
import com.google.gson.Gson;
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
        response.setContentType("text/plain;charset=UTF-8");
        String workerName = request.getParameter(USERNAME);

        String json = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
                Collectors.joining("\n"));
        Target target = new Gson().fromJson(json, Target.class);
        ServletUtils.getMissionManager(getServletContext()).updateTarget(target);
        WorkerObject w = ServletUtils.getWorkerManager(getServletContext()).getWorkerByName(workerName);
        w.addToCompleteTarget(target);
        response.setStatus(HttpServletResponse.SC_OK);

        response.getWriter().write(target.getName() + " (Target) update in mission and complete target in "+workerName+" (worker)");
        response.getWriter().flush();


        System.out.println("finish -" + target.getName());
    }
}
