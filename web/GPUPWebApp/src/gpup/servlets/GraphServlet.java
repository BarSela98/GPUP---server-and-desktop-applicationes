package gpup.servlets;

import ODT.Graph;
import com.google.gson.Gson;
import gpup.utils.ServletUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static gpup.constants.Constants.GRAPHNAME;

@WebServlet(name = "GraphServlet", urlPatterns = {"/select/graph"})
public class GraphServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String graphNameFromParameter = request.getParameter(GRAPHNAME);
        if (graphNameFromParameter == null || graphNameFromParameter.isEmpty())
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        else {
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                GraphManger graphManger = ServletUtils.getGraphManager(getServletContext());
                Graph graph = graphManger.getGraphByName(graphNameFromParameter);
                String json = gson.toJson(graph);
                out.println(json);
                out.flush();
                response.setStatus(HttpServletResponse.SC_OK);
            }
            catch (Exception e){
                response.getOutputStream().print(e.toString());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}