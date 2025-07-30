package project2;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);  // Don't create new
        if (session != null) {
            session.invalidate();  // Invalidate current session
        }

        response.sendRedirect("index.jsp");  // Redirect to login page
    }
}