package gpup.servlets.worker;

import engine.Target;
import com.google.gson.Gson;
import gpup.servlets.UserManager;
import gpup.servlets.WorkerManager;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
                WorkerManager workerManger = ServletUtils.getWorkerManager(getServletContext());
                List<Target> list = new ArrayList<>();

                List<Target> targetComplete = workerManger.getWorkerByName(usernameFromSession).getCompleteTarget();
                List<Target> targetToExecute = workerManger.getWorkerByName(usernameFromSession).getTargetsToExecute();

                list.addAll(targetComplete);
                list.addAll(targetToExecute);

                String json = gson.toJson(list);
                out.println(json);
                out.flush();
            }
        }
    }
}