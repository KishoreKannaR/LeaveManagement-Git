package project2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Demo
 */
@WebServlet("/Demo")
public class Demo extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    HttpSession session = request.getSession();
	    String email = (String) session.getAttribute("email");
	    String name = (String) session.getAttribute("name");

	    String leaveType = request.getParameter("leaveType");
	    String fromDate = request.getParameter("fromDate");
	    String toDate = request.getParameter("toDate");
	    String reason = request.getParameter("reason");

	    // Retrieve user_id from session if available
	    Integer userId = (Integer) session.getAttribute("user_id");
	    if (userId == null) {
	        userId = 1; // fallback if not in session (temporary)
	    }

	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        Connection con = DriverManager.getConnection("jdbc:mysql://mysql-db:3306/employee_db", "root", "root");

	        PreparedStatement ps = con.prepareStatement(
	            "INSERT INTO leave_requests(user_id, leave_type, from_date, to_date, reason, status, email, name) " +
	            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
	        );

	        ps.setInt(1, userId);
	        ps.setString(2, leaveType);
	        ps.setString(3, fromDate);
	        ps.setString(4, toDate);
	        ps.setString(5, reason);
	        ps.setString(6, "Pending"); // default
	        ps.setString(7, email);
	        ps.setString(8, name);

	        int rows = ps.executeUpdate();
	        con.close();

	        if (rows > 0) {
	            // ✅ Redirect to LeaveStatusServlet to view the updated list
	        	request.getRequestDispatcher("LeaveStatusServlet").forward(request, response);
	        } else {
	            response.getWriter().println("Failed to submit leave request.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.getWriter().println("Error: " + e.getMessage());
	    }
	}
}
