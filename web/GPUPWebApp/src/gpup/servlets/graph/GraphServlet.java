package gpup.servlets.graph;

import ODT.Graph;
import com.google.gson.Gson;
import gpup.servlets.GraphManger;
import gpup.servlets.UserManager;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static gpup.constants.Constants.GRAPHNAME;
/**
 * send graph by name from parameter (json object)
 */
@WebServlet(name = "GraphServlet", urlPatterns = {"/select/graph"})
public class GraphServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String graphNameFromParameter = request.getParameter(GRAPHNAME);

        /// wrong parameter
        if (graphNameFromParameter == null || graphNameFromParameter.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("graph name from parameter wrong");
        }
        /// request before login
        else if(usernameFromSession == null || usernameFromSession.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("please login");
        }
        /// worker request graph (only admin can request graph)
        else if(!userManager.getUserRole(usernameFromSession).equals("Admin")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("only admin can select a graph");
        }
        else {
            try {
                Gson gson = new Gson();
                GraphManger graphManger = ServletUtils.getGraphManager(getServletContext());
                Graph graph = graphManger.getGraphByName(graphNameFromParameter);
                String json = gson.toJson(graph);
                out.println(json);
                out.flush();
                response.setStatus(HttpServletResponse.SC_OK);
            }
            catch (Exception e){
                out.write(e.toString());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}