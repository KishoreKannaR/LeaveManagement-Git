package project2;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/LeaveStatusServlet")
public class LeaveStatusServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* System.out.println(">> LeaveStatusServlet triggered"); */
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        List<LeaveRequest> trackLeaves = new ArrayList<>();
        List<LeaveRequest> pastLeaves = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://mysql-db:3306/employee_db", "root", "root");

            PreparedStatement ps = con.prepareStatement("SELECT * FROM leave_requests WHERE email = ? ORDER BY from_date DESC");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            while (rs.next()) {
                LeaveRequest leave = new LeaveRequest();
                leave.setId(rs.getInt("id"));
                leave.setName(rs.getString("name"));
                leave.setEmail(rs.getString("email"));
                leave.setLeaveType(rs.getString("leave_type"));
                leave.setFromDate(rs.getDate("from_date"));
                leave.setToDate(rs.getDate("to_date"));
                leave.setReason(rs.getString("reason"));
                leave.setStatus(rs.getString("status"));
                leave.setManagerName(rs.getString("manager_name"));

                if (leave.getToDate().after(today)) {
                    trackLeaves.add(leave);  // ✅ future + pending → Track
                } else {
                    pastLeaves.add(leave);   // ✅ approved/rejected or past → History
                }
            }

            con.close();
			/*
			 * System.out.println("trackLeaves size: " + trackLeaves.size());
			 * System.out.println("pastLeaves size: " + pastLeaves.size());
			 */

            request.setAttribute("trackLeaves", trackLeaves);
            request.setAttribute("pastLeaves", pastLeaves);
            request.getRequestDispatcher("applyLeave.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

}
