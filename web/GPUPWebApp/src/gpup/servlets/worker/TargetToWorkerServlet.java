package gpup.servlets.worker;

import ODT.TargetToWorker;
import com.google.gson.Gson;
import engine.Target;
import gpup.servlets.WorkerManager;
import gpup.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import object.WorkerObject;
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
        TargetToWorker targetToWorkerServlet = new Gson().fromJson(json, TargetToWorker.class);
        Target target = targetToWorkerServlet.getTarget();
        WorkerObject worker = workerManager.getWorkerByName(targetToWorkerServlet.getNameOfWorker());
        worker.addTargetToList(target);
        response.getWriter().write(String.valueOf(worker.isAvailable(target.getMission())));
        response.setStatus(HttpServletResponse.SC_OK);
        }
}
