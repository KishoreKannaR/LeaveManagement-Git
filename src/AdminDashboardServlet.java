package project2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AdminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<User> userList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
            		"jdbc:mysql://mysql-db:3306/employee_db", "root", "root");

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                
                userList.add(user);
            }
            
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("userList", userList);
        request.getRequestDispatcher("user_table.jsp").forward(request, response);
    }
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    List<User> userList = new ArrayList<>();

	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        Connection con = DriverManager.getConnection(
	        		"jdbc:mysql://mysql-db:3306/employee_db", "root", "root");

	        // Check if a searchUser was passed from AdminUserServlet
	        User searchUser = (User) request.getAttribute("searchUser");

	        if (searchUser != null) {
	            // Only show searched user
	            userList.add(searchUser);
	        } else {
	            // Show all users
	            Statement stmt = con.createStatement();
	            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

	            while (rs.next()) {
	                User user = new User();
	                user.setId(rs.getInt("id"));
	                user.setName(rs.getString("name"));
	                user.setEmail(rs.getString("email"));
	                user.setPassword(rs.getString("password"));
	                user.setRole(rs.getString("role"));
	                userList.add(user);
	            }
	        }

	        con.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        request.setAttribute("message", "Error fetching users.");
	    }

	    // Forward retained input values (if any) to the JSP
	    request.setAttribute("id", request.getAttribute("id"));
	    request.setAttribute("name", request.getAttribute("name"));
	    request.setAttribute("email", request.getAttribute("email"));
	    request.setAttribute("password", request.getAttribute("password"));
	    request.setAttribute("role", request.getAttribute("role"));
	    
	    request.setAttribute("userList", userList);
	    request.getRequestDispatcher("user_table.jsp").forward(request, response);
	}

}
