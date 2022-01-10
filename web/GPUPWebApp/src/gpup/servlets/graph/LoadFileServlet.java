package gpup.servlets.graph;

import ODT.Graph;
import gpup.servlets.GraphManger;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import xml.Xmlimpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
@WebServlet("/file/xml/load")
public class LoadFileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        String usernameFromSession = SessionUtils.getUsername(request);
        GraphManger g = new GraphManger();
        GraphManger graphManager = ServletUtils.getGraphManager(getServletContext());
        PrintWriter out = response.getWriter();

        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            try {
                Xmlimpl xml = new Xmlimpl(readFromInputStream(part.getInputStream()));
                graphManager.addGraph(new Graph(xml,usernameFromSession));
            } catch (Exception e) {
             //   response.getOutputStream().print(e.toString());
            }
        }
    }


    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }



}
