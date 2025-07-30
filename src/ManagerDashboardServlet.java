package project2;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ManagerDashboardServlet")
public class ManagerDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<LeaveRequest> leaveList = new ArrayList<>();
        List<LeaveRequest> historyList = new ArrayList<>();

        String searchedEmail = request.getParameter("email"); // from search form

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
            		"jdbc:mysql://mysql-db:3306/employee_db", "root", "root");

            // --- Upcoming leave requests ---
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM leave_requests WHERE from_date >= CURDATE()");
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
                leaveList.add(leave);
            }

            // --- Search history if email is provided ---
            if (searchedEmail != null && !searchedEmail.trim().isEmpty()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM leave_requests WHERE email = ? ORDER BY from_date DESC");
                ps.setString(1, searchedEmail);
                ResultSet rs2 = ps.executeQuery();
                while (rs2.next()) {
                    LeaveRequest leave = new LeaveRequest();
                    leave.setId(rs2.getInt("id"));
                    leave.setName(rs2.getString("name"));
                    leave.setEmail(rs2.getString("email"));
                    leave.setLeaveType(rs2.getString("leave_type"));
                    leave.setFromDate(rs2.getDate("from_date"));
                    leave.setToDate(rs2.getDate("to_date"));
                    leave.setReason(rs2.getString("reason"));
                    leave.setStatus(rs2.getString("status"));
                    leave.setManagerId(rs2.getString("manager_id"));
                    leave.setManagerName(rs2.getString("manager_name"));
                    leave.setDecisionDate(rs2.getString("decision_date"));
                    historyList.add(leave);
                }

                request.setAttribute("searchedEmail", searchedEmail);
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Always show leaveList; show historyList only if searched
        request.setAttribute("leaveList", leaveList);
        request.setAttribute("historyList", historyList);

        request.getRequestDispatcher("leave_table.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String leaveId = request.getParameter("id");
        String newStatus = request.getParameter("status");

        HttpSession session = request.getSession(false);
        String managerId = (String) session.getAttribute("id");
        String managerName = (String) session.getAttribute("name");
        java.sql.Date decisionDate = new java.sql.Date(System.currentTimeMillis());

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
            		"jdbc:mysql://mysql-db:3306/employee_db", "root", "root");

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE leave_requests SET status = ?, manager_id = ?, manager_name = ?, decision_date = ? WHERE id = ?");
            ps.setString(1, newStatus);
            ps.setString(2, managerId);
            ps.setString(3, managerName);
            ps.setDate(4, decisionDate);
            ps.setInt(5, Integer.parseInt(leaveId));

            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // After update, redirect to show updated leave list
        response.sendRedirect("ManagerDashboardServlet");
    }
}
