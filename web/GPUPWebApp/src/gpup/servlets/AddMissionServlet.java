package gpup.servlets;

import ODT.Mission;
import com.google.gson.Gson;
import gpup.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@WebServlet(name = "AddMissionServlet", urlPatterns = {"/mission/add"})
public class AddMissionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        MissionManger missionManger = ServletUtils.getMissionManager(getServletContext());
        String json = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
                Collectors.joining("\n"));
        //String json = request.getInputStream().toString();
        Mission graphsNames = new Gson().fromJson(json, Mission.class);
        missionManger.addMission(graphsNames);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
