package gpup.servlets.worker;

import ODT.TargetInWorkerAndAmountOfThread;
import com.google.gson.Gson;
import engine.Target;
import gpup.servlets.UserManager;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import object.WorkerObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "TargetCompleteListServlet", urlPatterns = {"/worker/target/complete"})
public class TargetCompleteListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (usernameFromSession == null || usernameFromSession.isEmpty() ||
                userManager.getUserRole(usernameFromSession) == null || !userManager.getUserRole(usernameFromSession).equals("Worker")) {
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("something wrong - please login / you are not a worker");
        } else {
            response.setContentType("application/json");
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                WorkerObject worker = ServletUtils.getWorkerManager(getServletContext()).getWorkerByName(usernameFromSession);
                List<Target> list = new ArrayList<>();
                List<Target> targetComplete = worker.getCompleteTarget();
                List<Target> targetInProgress = worker.getTargetInProgress();
                list.addAll(targetComplete);
                list.addAll(targetInProgress);

                String json = gson.toJson(new TargetInWorkerAndAmountOfThread(list, worker.getThreadsNum() - worker.getCurThreads()));
                out.println(json);
                out.flush();
            }
        }
    }
}