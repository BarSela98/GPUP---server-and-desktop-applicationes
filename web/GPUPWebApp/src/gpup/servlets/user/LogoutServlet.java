package gpup.servlets.user;

import gpup.servlets.UserManager;
import gpup.utils.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

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