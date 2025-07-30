package project2;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AdminUserServlet")
public class AdminUserServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().println("AdminUserServlet is working. Use POST for user operations.");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
            		"jdbc:mysql://mysql-db:3306/employee_db", "root", "root");

            PreparedStatement ps;
            ResultSet rs;

            switch (action) {
                case "Search":
                    StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
                    List<String> paramTypes = new ArrayList<>();
                    List<Object> paramValues = new ArrayList<>();

                    if (idStr != null && !idStr.isEmpty()) {
                        sql.append(" AND id = ?");
                        paramTypes.add("int");
                        paramValues.add(Integer.parseInt(idStr));
                    }
                    if (email != null && !email.isEmpty()) {
                        sql.append(" AND email = ?");
                        paramTypes.add("string");
                        paramValues.add(email);
                    }
                    if (name != null && !name.isEmpty()) {
                        sql.append(" AND name = ?");
                        paramTypes.add("string");
                        paramValues.add(name);
                    }
					/*
					 * if (password != null && !password.isEmpty()) {
					 * sql.append(" AND password = ?"); paramTypes.add("string");
					 * paramValues.add(password); }
					 */
                    if (role != null && !role.isEmpty()) {
                        sql.append(" AND role = ?");
                        paramTypes.add("string");
                        paramValues.add(role);
                    }

                    ps = con.prepareStatement(sql.toString());
                    for (int i = 0; i < paramTypes.size(); i++) {
                        if ("int".equals(paramTypes.get(i))) {
                            ps.setInt(i + 1, (Integer) paramValues.get(i));
                        } else {
                            ps.setString(i + 1, paramValues.get(i).toString());
                        }
                    }

                    rs = ps.executeQuery();
                    if (rs.next()) {
                        User searchUser = new User(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getString("role")
                        );
                        request.setAttribute("searchUser", searchUser);
                    } else {
                        request.setAttribute("message", "User not found.");
                    }
                    break;

                case "Add":
                    if (idStr != null && name != null && email != null && password != null && role != null &&
                            !idStr.isEmpty() && !name.isEmpty() && !email.isEmpty() &&
                            !password.isEmpty() && !role.isEmpty()) {

                        ps = con.prepareStatement("SELECT * FROM users WHERE id = ? OR email = ?");
                        ps.setInt(1, Integer.parseInt(idStr));
                        ps.setString(2, email);
                        rs = ps.executeQuery();

                        if (rs.next()) {
                            request.setAttribute("message", "User already exists with same ID or Email.");
                        } else {
                            String hashedPassword = PasswordUtil.hashPassword(password);

                            ps = con.prepareStatement("INSERT INTO users (id, name, email, password, role) VALUES (?, ?, ?, ?, ?)");
                            ps.setInt(1, Integer.parseInt(idStr));
                            ps.setString(2, name);
                            ps.setString(3, email);
                            ps.setString(4, hashedPassword);
                            ps.setString(5, role);
                            ps.executeUpdate();
                            request.setAttribute("message", "User added successfully.");
                        }
                    } else {
                        request.setAttribute("message", "All fields are required for adding.");
                    }
                    break;


                case "Update":
                    if ((idStr != null && !idStr.isEmpty()) || (email != null && !email.isEmpty())) {
                        String lookupSql = "SELECT * FROM users WHERE " +
                                (idStr != null && !idStr.isEmpty() ? "id = ?" : "email = ?");
                        ps = con.prepareStatement(lookupSql);
                        if (idStr != null && !idStr.isEmpty())
                            ps.setInt(1, Integer.parseInt(idStr));
                        else
                            ps.setString(1, email);
                        rs = ps.executeQuery();

                        if (rs.next()) {
                            int dbId = rs.getInt("id");
                            String updateSql = "UPDATE users SET ";
                            List<String> updates = new ArrayList<>();
                            List<Object> values = new ArrayList<>();

                            if (name != null && !name.isEmpty()) {
                                updates.add("name = ?");
                                values.add(name);
                            }
                            if (password != null && !password.isEmpty()) {
                                updates.add("password = ?");
                                values.add(PasswordUtil.hashPassword(password));
                            }
                            if (role != null && !role.isEmpty()) {
                                updates.add("role = ?");
                                values.add(role);
                            }
                            if (email != null && !email.isEmpty()) {
                                updates.add("email = ?");
                                values.add(email);
                            }

                            if (!updates.isEmpty()) {
                                updateSql += String.join(", ", updates) + " WHERE id = ?";
                                ps = con.prepareStatement(updateSql);
                                for (int i = 0; i < values.size(); i++) {
                                    ps.setObject(i + 1, values.get(i));
                                }
                                ps.setInt(values.size() + 1, dbId);
                                int rows = ps.executeUpdate();
                                if (rows > 0) {
                                    request.setAttribute("message", "User updated successfully.");
                                } else {
                                    request.setAttribute("message", "Update failed.");
                                }
                            } else {
                                request.setAttribute("message", "No fields to update.");
                            }
                        } else {
                            request.setAttribute("message", "Update failed: User not found.");
                        }
                    } else {
                        request.setAttribute("message", "ID or Email is required to update.");
                    }
                    break;

                case "Remove":
                    if ((idStr != null && !idStr.isEmpty()) || (email != null && !email.isEmpty())) {
                        String lookupSql = "SELECT * FROM users WHERE " +
                                (idStr != null && !idStr.isEmpty() ? "id = ?" : "email = ?");
                        ps = con.prepareStatement(lookupSql);
                        if (idStr != null && !idStr.isEmpty())
                            ps.setInt(1, Integer.parseInt(idStr));
                        else
                            ps.setString(1, email);
                        rs = ps.executeQuery();

                        if (rs.next()) {
                            String mismatch = "";
                            if (name != null && !name.isEmpty() && !name.equals(rs.getString("name")))
                                mismatch += "name ";
                            if (role != null && !role.isEmpty() && !role.equals(rs.getString("role")))
                                mismatch += "role ";
                            if (!mismatch.isEmpty()) {
                                request.setAttribute("message", "Remove failed: User with mismatched " + mismatch + "did not exist.");
                            } else {
                                User user = new User(
                                        rs.getInt("id"),
                                        rs.getString("name"),
                                        rs.getString("email"),
                                        rs.getString("password"),
                                        rs.getString("role")
                                );
                                request.setAttribute("confirmRemove", true);
                                request.setAttribute("removeUser", user);

                                if ("true".equals(request.getParameter("confirmed"))) {
                                    ps = con.prepareStatement("DELETE FROM users WHERE id = ?");
                                    ps.setInt(1, user.getId());
                                    ps.executeUpdate();
                                    request.setAttribute("message", "User removed successfully.");
                                }
                            }
                        } else {
                            request.setAttribute("message", "Remove failed: User not found.");
                        }
                    } else {
                        request.setAttribute("message", "ID or Email is required for deletion.");
                    }
                    break;

                default:
                    request.setAttribute("message", "Unknown action.");
            }

            request.setAttribute("id", idStr);
            request.setAttribute("email", email);
            request.setAttribute("name", name);
            request.setAttribute("password", password);
            request.setAttribute("role", role);

            request.getRequestDispatcher("AdminDashboardServlet").forward(request, response);
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Error: " + e.getMessage());
            request.getRequestDispatcher("AdminDashboardServlet").forward(request, response);
        }
    }
}
