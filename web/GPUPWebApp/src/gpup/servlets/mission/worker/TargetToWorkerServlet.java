package gpup.servlets.mission.worker;

import ODT.TargetToWorker;
import com.google.gson.Gson;
import gpup.servlets.WorkerManager;
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

@WebServlet(name = "TargetToWorkerServlet", urlPatterns = {"/mission/worker/add/target"})
public class TargetToWorkerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        WorkerManager workerManager = ServletUtils.getWorkerManager(getServletContext());

        String json = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
                Collectors.joining("\n"));
        //String json = request.getInputStream().toString();
        TargetToWorker targetToWorkerServlet = new Gson().fromJson(json, TargetToWorker.class);
        workerManager.getWorkerByName(targetToWorkerServlet.getNameOfWorker()).addTargetToList(targetToWorkerServlet.getTarget());
        response.setStatus(HttpServletResponse.SC_OK);
    }
}