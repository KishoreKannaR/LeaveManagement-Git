package project2;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/GetAdminAndManagerNamesServlet")
public class GetAdminAndManagerNamesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        List<Map<String, String>> resultList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
            		"jdbc:mysql://mysql-db:3306/employee_db", "root", "root");

            String sql = "SELECT name, role FROM users WHERE role = 'admin' OR role = 'manager'";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, String> user = new HashMap<>();
                user.put("name", rs.getString("name"));
                user.put("role", rs.getString("role"));
                resultList.add(user);
            }

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Build JSON manually (you can use Gson if allowed)
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < resultList.size(); i++) {
            Map<String, String> user = resultList.get(i);
            json.append("{")
                .append("\"name\":\"").append(user.get("name")).append("\",")
                .append("\"role\":\"").append(user.get("role")).append("\"")
                .append("}");
            if (i < resultList.size() - 1) json.append(",");
        }
        json.append("]");

        out.print(json.toString());
        out.flush();
    }
}
