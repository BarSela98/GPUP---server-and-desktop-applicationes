package gpup.servlets.user;

import gpup.constants.Constants;
import gpup.servlets.UserManager;
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

@WebServlet(name = "LightweightLoginServlet", urlPatterns = {"/loginShortResponse"})
public class LightweightLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession == null) { //user is not logged in yet

            String usernameFromParameter = request.getParameter(USERNAME);
            String roleFromParameter = request.getParameter(ROLE);
            String threadFromParameter = request.getParameter(AMOUNT_OF_THREAD);
            /*
            no username in session and no username in parameter - not standard situation. it's a conflict
            stands for conflict in server state
            */
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("wrong login - user name incorrect");
            }
            else if (roleFromParameter == null || roleFromParameter.isEmpty() || (!roleFromParameter.equals("Admin") && !roleFromParameter.equals("Worker"))) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("you have to choose correct role");
            }
            else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();

                /*
                One can ask why not enclose all the synchronizations inside the userManager object ?
                Well, the atomic action we need to perform here includes both the question (isUserExists) and (potentially) the insertion
                of a new user (addUser). These two actions needs to be considered atomic, and synchronizing only each one of them, solely, is not enough.
                (of course there are other more sophisticated and performable means for that (atomic objects etc) but these are not in our scope)

                The synchronized is on this instance (the servlet).
                As the servlet is singleton - it is promised that all threads will be synchronized on the very same instance (crucial here)

                A better code would be to perform only as little and as necessary things we need here inside the synchronized block and avoid
                do here other not related actions (such as response setup. this is shown here in that manner just to stress this issue
                 */
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                        // stands for unauthorized as there is already such user with this name
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    }
                    else {
                        //add the new user to the users list
                        if (roleFromParameter.equals("Worker")){
                            if (threadFromParameter == null || threadFromParameter.isEmpty()) {
                                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                response.getWriter().write("you have to choose amount of thread for worker");
                            }
                            else if(Integer.parseInt(threadFromParameter) > 5 || Integer.parseInt(threadFromParameter) < 1){
                                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                response.getWriter().write("you can't login with this amount of thread for worker");
                            }
                            else {
                                WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());
                                workerManager.addWorker(usernameFromParameter, Integer.parseInt(threadFromParameter));
                                request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                                response.setStatus(HttpServletResponse.SC_OK);
                                response.getOutputStream().print(usernameFromParameter+" login");
                                userManager.addUser(usernameFromParameter,roleFromParameter);
                            }
                        }
                        else{ // admin
                            userManager.addUser(usernameFromParameter,roleFromParameter);
                            request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getOutputStream().print(usernameFromParameter+" login");
                        }
                        //set the username in a session so it will be available on each request
                        //the true parameter means that if a session object does not exists yet
                        //create a new one
                    }
                }
            }
        } else {
            //user is already logged in
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("you are already login");
        }
    }

}