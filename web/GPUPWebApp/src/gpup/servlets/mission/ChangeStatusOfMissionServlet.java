package gpup.servlets.mission;

import gpup.servlets.MissionManger;
import gpup.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static gpup.constants.Constants.MISSIONS_NAME;
import static gpup.constants.Constants.MISSIONS_STATUS;


@WebServlet(name = "ChangeStatusOfMissionServlet", urlPatterns = {"/mission/setstatus"})
public class ChangeStatusOfMissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        try {
        String nameOfMission = request.getParameter(MISSIONS_NAME);
        String statusOfMission = request.getParameter(MISSIONS_STATUS);
        if (nameOfMission == null || nameOfMission.isEmpty() && statusOfMission == null || statusOfMission.isEmpty())
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        else {
                MissionManger missionManger = ServletUtils.getMissionManager(getServletContext());
                missionManger.setStatusOfMissionByName(nameOfMission,statusOfMission);
                response.setStatus(HttpServletResponse.SC_OK);
                System.out.println(missionManger.getMissionByName(nameOfMission).getIsRunning());
            }
        }
        catch (Exception e) {
      //      e.printStackTrace();
        }
    }
}
