package gpup.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

@WebServlet(name = "LoadFileServlet", urlPatterns = {"/file/xml/load"})
public class LoadFileServlet extends HttpServlet {

}
