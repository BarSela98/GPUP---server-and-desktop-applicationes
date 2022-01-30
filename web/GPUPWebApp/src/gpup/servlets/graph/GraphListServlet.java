package gpup.servlets.graph;

import com.google.gson.Gson;
import gpup.servlets.GraphManger;
import gpup.utils.ServletUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@WebServlet(name = "GraphListServlet", urlPatterns = {"/graphlist"})
public class GraphListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            GraphManger graphManger = ServletUtils.getGraphManager(getServletContext());
            Set<String> graphMap = graphManger.getGraph().keySet();
            System.out.println(graphMap);
            String json = gson.toJson(graphMap);
            out.println(json);
            out.flush();
        }
    }
}