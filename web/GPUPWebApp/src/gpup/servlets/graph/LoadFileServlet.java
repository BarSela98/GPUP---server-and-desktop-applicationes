package gpup.servlets.graph;

import ODT.Graph;
import gpup.servlets.GraphManger;
import gpup.servlets.UserManager;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;
import graph.GraphIsExists;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import xml.Xmlimpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
@WebServlet("/file/xml/load")
public class LoadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        if (usernameFromSession == null || usernameFromSession.isEmpty() || userManager.getUserRole(usernameFromSession) == null){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("please login before upload file");
        }

        else if(!userManager.getUserRole(usernameFromSession).equals("Admin")){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("only admin can load a file");
        }


        else {
            GraphManger graphManager = ServletUtils.getGraphManager(getServletContext());
            PrintWriter out = response.getWriter();
            Collection<Part> parts = request.getParts();
            if (parts != null)
            for (Part part : parts) {
                if (checkFileExtension(part.getSubmittedFileName())) {
                    try {
                        Xmlimpl xmlFile = new Xmlimpl(part.getInputStream());
                        graphManager.addGraph(new Graph(xmlFile, usernameFromSession));

                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("Add new graph successfully: \n" +
                                "creator: " + usernameFromSession + "\n" +
                                "graph name: " + xmlFile.getGraphName() + "\n");
                        out.flush();

                    } catch (GraphIsExists e) {
                        out.write(e.toString());
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    } catch (IllegalArgumentException e) {
                        out.write(e.toString());
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    } catch (Exception e) {
                        out.write(e.toString());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                } else {
                    out.write("please choose xml file");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
            else{
                out.write("please insert file");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    public static boolean checkFileExtension(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName must not be null!");
        }
        String extension = "";
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1);
        }

        return extension.equals("xml");
    }
}
