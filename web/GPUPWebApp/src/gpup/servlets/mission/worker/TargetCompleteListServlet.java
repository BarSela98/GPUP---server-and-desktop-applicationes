package gpup.servlets.mission.worker;

import engine.Target;
import com.google.gson.Gson;
import gpup.servlets.WorkerManager;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "TargetCompleteListServlet", urlPatterns = {"/worker/target/complete"})
public class TargetCompleteListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            WorkerManager workerManger = ServletUtils.getWorkerManager(getServletContext());
            String usernameFromSession = SessionUtils.getUsername(request);
            List<Target> targetComplete = workerManger.getWorkerByName(usernameFromSession).getCompleteTarget();
            System.out.println("------");
            System.out.println(targetComplete);
            System.out.println("------");
            String json = gson.toJson(targetComplete);
            out.println(json);
            out.flush();
        }
    }
}