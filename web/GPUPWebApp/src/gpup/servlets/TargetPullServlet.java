package gpup.servlets;

import gpup.constants.Constants;
import gpup.utils.ServletUtils;
import gpup.utils.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static gpup.constants.Constants.*;

@WebServlet(name = "TargetPullServlet", urlPatterns = {"/targetPullServlet"})
public class TargetPullServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        System.out.println("where am i");

        MissionManger MissionManager = ServletUtils.getMissionManager(getServletContext());
        request.
        int threadsNum=Integer.parseInt(request.getParameter(THREADS_NUM));
        String m=request.getParameter(MISSIONS);
        List<String> missions=new ArrayList<>();
        String temp="";
        //check that missions not null
        for(int i=0;i<m.length();i++){
            if(m.charAt(i)==','){
                missions.add(temp);
                temp="";
            }
            else{
                temp+=m.charAt(i);
            }
        }


}
