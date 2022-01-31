package gpup.servlets.worker;

import ODT.TargetToWorker;
import com.google.gson.Gson;
import gpup.servlets.UserManager;
import gpup.servlets.WorkerManager;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@WebServlet(name = "TargetToWorkerServlet", urlPatterns = {"/mission/worker/add/target"})
public class TargetToWorkerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (usernameFromSession == null || usernameFromSession.isEmpty() ||
                userManager.getUserRole(usernameFromSession) == null || !userManager.getUserRole(usernameFromSession).equals("Worker")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("something wrong - please login / you are not a worker");
        } else {
            WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());
            String json = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
                    Collectors.joining("\n"));
            System.out.println(json);
            TargetToWorker targetToWorkerServlet = new Gson().fromJson(json, TargetToWorker.class);
            workerManager.getWorkerByName(targetToWorkerServlet.getNameOfWorker()).addTargetToList(targetToWorkerServlet.getTarget());
            response.getWriter().write("send target to worker successfully");
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}