package project2;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/EventServlet")
public class EventServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        String title = request.getParameter("title");
        String start = request.getParameter("start");
        String end = request.getParameter("end");
        String description = request.getParameter("description");
        String createdBy = request.getParameter("createdBy");
        String visibleTo = request.getParameter("visibleTo");

        // Ensure end is not null
        if (end == null || end.trim().isEmpty()) {
            end = start;
        }

        // Ensure description is not null
        if (description == null) {
            description = "";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
            		"jdbc:mysql://mysql-db:3306/employee_db", "root", "root");

            if ("add".equals(action)) {
                String sql = "INSERT INTO calendar_events (title, start_date, end_date, description, created_by, visible_to) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, title);
                    ps.setString(2, start);
                    ps.setString(3, end);
                    ps.setString(4, description);
                    ps.setString(5, createdBy);
                    ps.setString(6, visibleTo);
                    ps.executeUpdate();
                }
                response.getWriter().write("Event added successfully");

            } else if ("update".equals(action)) {
                String sql = "UPDATE calendar_events SET end_date = ?, description = ?, created_by = ?, visible_to = ? WHERE title = ? AND start_date = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, end);
                    ps.setString(2, description);
                    ps.setString(3, createdBy);
                    ps.setString(4, visibleTo);
                    ps.setString(5, title);
                    ps.setString(6, start);
                    ps.executeUpdate();
                }
                response.getWriter().write("Event updated successfully");

            } else if ("delete".equals(action)) {
                String sql = "DELETE FROM calendar_events WHERE title = ? AND start_date = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, title);
                    ps.setString(2, start);
                    ps.executeUpdate();
                }
                response.getWriter().write("Event deleted successfully");

            } else {
                response.getWriter().write("Invalid action");
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}
