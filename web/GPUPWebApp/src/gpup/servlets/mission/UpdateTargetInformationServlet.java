package gpup.servlets.mission;

import ODT.Mission;
import ODT.Target;
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

@WebServlet(name = "UpdateTargetInformationServlet", urlPatterns = {"/mission/update/target"})
public class UpdateTargetInformationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        String json = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
                Collectors.joining("\n"));
        Target target = new Gson().fromJson(json, Target.class);

        try {
            Mission mission = ServletUtils.getMissionManager(getServletContext()).getMissionByName(target.getMission());
            mission.updateTarget(target);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
         //   response.getOutputStream().print(e.toString());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
