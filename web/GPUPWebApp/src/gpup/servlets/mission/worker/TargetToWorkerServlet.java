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
        System.out.println("size "+workerManager.getWorkerByName(targetToWorkerServlet.getNameOfWorker()).getTargetsToExecute().size());
        System.out.println("before "+workerManager.getWorkerByName(targetToWorkerServlet.getNameOfWorker()).getTargetsToExecute().isEmpty());
        workerManager.getWorkerByName(targetToWorkerServlet.getNameOfWorker()).addTargetToList(targetToWorkerServlet.getTarget());
        System.out.println("size "+workerManager.getWorkerByName(targetToWorkerServlet.getNameOfWorker()).getTargetsToExecute().size());
        System.out.println("after "+workerManager.getWorkerByName(targetToWorkerServlet.getNameOfWorker()).getTargetsToExecute().isEmpty());
        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println("target to worker " +targetToWorkerServlet.getTarget());
    }
}