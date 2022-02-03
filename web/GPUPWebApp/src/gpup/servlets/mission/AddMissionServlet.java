package gpup.servlets.mission;

import engine.Mission;
import com.google.gson.Gson;
import gpup.servlets.MissionManger;
import gpup.servlets.UserManager;
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
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet(name = "AddMissionServlet", urlPatterns = {"/mission/add"})
public class AddMissionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        MissionManger missionManger = ServletUtils.getMissionManager(getServletContext());
        String json = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
                Collectors.joining("\n"));
        Mission mission = new Gson().fromJson(json, Mission.class);

        /// request before login
        if(usernameFromSession == null || usernameFromSession.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("please login");
        }
        /// worker request add mission but only admin can add mission
        else if(!userManager.getUserRole(usernameFromSession).equals("Admin")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("only admin can select a graph");
        }
        /// can't add mission with zero price
        else if (mission.getPriceOfAllMission() == 0 ){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("you can't add new mission with zero price");
            response.getWriter().flush();
        }
        //// the name is exists
        else if (missionManger.isMissionNameExists(mission.getNameOfMission())){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("you can't add new mission with this name");
            response.getWriter().flush();
        }
        else{
        missionManger.addMission(mission);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(mission.getNameOfMission()+ " (name of mission) add to mission manager");
        response.getWriter().flush();
        }
    }
}