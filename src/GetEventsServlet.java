package project2;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetEventsServlet")
public class GetEventsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray eventsArray = new JSONArray();

        try (Connection conn = DriverManager.getConnection(
        		"jdbc:mysql://mysql-db:3306/employee_db", "root", "root");
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT title, start_date, end_date, 'event' AS type FROM calendar_events " +
                 "UNION " +
                 "SELECT title, start_date, end_date, 'holiday' AS type FROM default_holidays");
             ResultSet rs = stmt.executeQuery()) {
        	while (rs.next()) {
        	    String title = rs.getString("title");
        	    String start = rs.getString("start_date");

        	    if (title != null && !title.trim().isEmpty() && start != null && !start.trim().isEmpty()) {
        	        JSONObject event = new JSONObject();
        	        event.put("title", title.trim());
        	        event.put("start", start.trim());

        	        String end = rs.getString("end_date");
        	        if (end != null && !end.trim().isEmpty()) {
        	            event.put("end", end.trim());
        	        }

        	        String type = rs.getString("type");
        	        event.put("className", "holiday".equals(type) ? "holiday-event" : "normal-event");

        	        eventsArray.put(event);
        	    }
        	}


        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintWriter out = response.getWriter();
        out.print(eventsArray.toString());
        out.flush();
    }
}

