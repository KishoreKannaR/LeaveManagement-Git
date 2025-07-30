package project2;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Basic input sanitization
        if (email == null || !email.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            response.getWriter().println("Invalid credentials.");
            return;
        }

        if (password == null || password.length() < 4) {
            response.getWriter().println("Invalid credentials.");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
            		"jdbc:mysql://mysql-db:3306/employee_db", "root", "root");

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM employee_db.users WHERE email=?");
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                if (PasswordUtil.checkPassword(password, hashedPassword)) {
                    String role = rs.getString("role").trim().toLowerCase();
                    String name = rs.getString("name");
                    String id = rs.getString("id");

                    // Save login info in session
                    HttpSession session = request.getSession();
                    session.setAttribute("name", name);
                    session.setAttribute("email", email);
                    session.setAttribute("role", role);
                    session.setAttribute("id", id);

                    // Redirect based on role
                    if (role.equals("employee")) {
                        response.sendRedirect("employeeDashboard.jsp");
                    } else if (role.equals("manager")) {
                        response.sendRedirect("managerDashboard.jsp");
                    } else if (role.equals("admin")) {
                        response.sendRedirect("adminDashboard.jsp");
                    } else {
                        out.println("<h3>Invalid role.</h3>");
                    }
                } else {
                    out.println("<h3>Invalid credentials.</h3>");
                }
            } else {
                out.println("<h3>Invalid credentials.</h3>");
            }

        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>Database connection failed. Please try again later.</h3>");
            // Optional debug print:
            // e.printStackTrace(out);
        } catch (ClassNotFoundException e) {
            out.println("<h3 style='color:red;'>JDBC Driver not found.</h3>");
        } catch (Exception e) {
            out.println("<h3 style='color:red;'>Unexpected error occurred.</h3>");
            // Optional debug print:
            // e.printStackTrace(out);
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ignored) {}
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<h3>LoginServlet is working. Please POST login data.</h3>");
    }
}
