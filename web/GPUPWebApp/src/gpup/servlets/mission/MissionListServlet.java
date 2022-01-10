package gpup.servlets.mission;

import ODT.Mission;
import com.google.gson.Gson;
import gpup.servlets.MissionManger;
import gpup.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
@WebServlet(name = "MissionListServlet", urlPatterns = {"/missionlist"})
public class MissionListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            MissionManger missionManager = ServletUtils.getMissionManager(getServletContext());
            Collection<Mission> missionsList = missionManager.getMissionList().values();
            System.out.println(missionsList);
            String json = gson.toJson(missionsList);
            out.println(json);
            out.flush();
        }
    }
}