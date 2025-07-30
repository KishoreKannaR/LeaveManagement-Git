<%@ page import="java.util.*, project2.LeaveRequest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Leave Applications</title>
</head>
<body>
    <h2>Leave Applications</h2>
    
    <h3>Pending List</h3>

<%
    List<LeaveRequest> leaveList = (List<LeaveRequest>) request.getAttribute("leaveList");
    boolean hasPending = false;
    for (LeaveRequest leave : leaveList) {
        if ("Pending".equals(leave.getStatus())) {
            hasPending = true;
            break;
        }
    }
    if (hasPending) {
%>
<table border="1">
    <tr>
        <th>ID</th><th>Name</th><th>Email</th><th>Leave Type</th>
        <th>From</th><th>To</th><th>Reason</th><th>Status</th><th>Action</th>
    </tr>
    <%
        for (LeaveRequest leave : leaveList) {
            if ("Pending".equals(leave.getStatus())) {
    %>
    <tr>
        <td><%= leave.getId() %></td>
        <td><%= leave.getName() %></td>
        <td><%= leave.getEmail() %></td>
        <td><%= leave.getLeaveType() %></td>
        <td><%= leave.getFromDate() %></td>
        <td><%= leave.getToDate() %></td>
        <td><%= leave.getReason() %></td>
        <%-- <td><%= leave.getStatus() %></td> --%>
        <td style="color: orange;">⏳ Pending</td>
        <td>
            <form method="post" action="ManagerDashboardServlet">
                <input type="hidden" name="id" value="<%= leave.getId() %>">
                <select name="status">
                    <option value="Pending" <%= "Pending".equals(leave.getStatus()) ? "selected" : "" %>>Pending</option>
                    <option value="Approved">Approved</option>
                    <option value="Rejected">Rejected</option>
                </select>
                <input type="submit" value="Update">
            </form>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>
<%
    } else {
%>
<p>No pending leave requests.</p>
<%
    }
%>

    
    <h3>Approved/Rejected List</h3>

<%

    boolean hasResolved = false;
    for (LeaveRequest leave : leaveList) {
        if ("Approved".equals(leave.getStatus()) || "Rejected".equals(leave.getStatus())) {
            hasResolved = true;
            break;
        }
    }
    if (hasResolved) {
%>
<table border="1">
    <tr>
        <th>ID</th><th>Name</th><th>Email</th><th>Leave Type</th>
        <th>From</th><th>To</th><th>Reason</th><th>Status</th><th>Action</th>
    </tr>
    <%
        for (LeaveRequest leave : leaveList) {
            if ("Approved".equals(leave.getStatus()) || "Rejected".equals(leave.getStatus())) {
    %>
    <tr>
        <td><%= leave.getId() %></td>
        <td><%= leave.getName() %></td>
        <td><%= leave.getEmail() %></td>
        <td><%= leave.getLeaveType() %></td>
        <td><%= leave.getFromDate() %></td>
        <td><%= leave.getToDate() %></td>
        <td><%= leave.getReason() %></td>
        <%-- <td><%= leave.getStatus() %></td> --%>
        <td style="color: <%= leave.getStatus().equals("Approved") ? "green" : "red" %>;">
            <%= leave.getStatus().equals("Approved") ? "✅ Approved" : "❌ Rejected" %>
        <td>
            <form method="post" action="ManagerDashboardServlet">
                <input type="hidden" name="id" value="<%= leave.getId() %>">
                <select name="status">
                    <option value="Pending" <%= "Pending".equals(leave.getStatus()) ? "selected" : "" %>>Pending</option>
                    <option value="Approved">Approved</option>
                    <option value="Rejected">Rejected</option>
                </select>
                <input type="submit" value="Update">
            </form>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>
<%
    } else {
%>
<p>No Accepted/Rejected leave requests.</p>
<%
    }
%>

<h3>🔍 Search Employee Leave History</h3>
<div style="display: flex; gap: 10px; align-items: center; margin-bottom: 20px;">
    <form method="get" action="ManagerDashboardServlet">
        <input type="email" name="email" placeholder="Enter employee email" required />
        <button type="submit">Search</button>
    </form>

    <form method="get" action="ManagerDashboardServlet">
        <input type="submit" value="Clear Search" />
    </form>
</div>


<% 
    List<LeaveRequest> historyList = (List<LeaveRequest>) request.getAttribute("historyList");
    String searchedEmail = (String) request.getAttribute("searchedEmail");
%>

<% if (searchedEmail != null) { %>
    <h3>📜 Leave History for <%= searchedEmail %></h3>
    <% if (historyList != null && !historyList.isEmpty()) { %>
        <table border="1">
            <tr>
                <th>ID</th><th>Name</th><th>Email</th><th>Leave Type</th>
                <th>From</th><th>To</th><th>Reason</th><th>Status</th><th>Validated by</th>
            </tr>
            <% for (LeaveRequest l : historyList) { %>
            <tr>
                <td><%= l.getId() %></td>
                <td><%= l.getName() %></td>
                <td><%= l.getEmail() %></td>
                <td><%= l.getLeaveType() %></td>
                <td><%= l.getFromDate() %></td>
                <td><%= l.getToDate() %></td>
                <td><%= l.getReason() %></td>
                <td><%= l.getStatus() %></td>
                <td><%= l.getManagerName() + " (" + l.getManagerId() + ")"  %><br>
                on <%= l.getDecisionDate() %>                
                </td>
            </tr>
            <% } %>
        </table>
    <% } else { %>
        <p>No leave history found for <strong><%= searchedEmail %></strong>.</p>
    <% } %>
<% } %>

    
    
</body>
</html>
