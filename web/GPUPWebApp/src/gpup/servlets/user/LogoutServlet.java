package gpup.servlets.user;

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

import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        MissionManger missionManager = ServletUtils.getMissionManager(getServletContext());
        WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());
        WorkerObject worker = workerManager.getWorkerByName(usernameFromSession);
        if (usernameFromSession != null) {
            userManager.removeUser(usernameFromSession); /////////////////////////////////
            SessionUtils.clearSession(request);
            response.getWriter().write(usernameFromSession+" logout");
            response.getWriter().flush();
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else{
            response.getWriter().write("you have login before logout");
            response.getWriter().flush();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}