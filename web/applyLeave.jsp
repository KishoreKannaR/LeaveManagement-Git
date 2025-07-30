<%@ page import="javax.servlet.http.*,javax.servlet.*,java.util.List,project2.LeaveRequest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session == null || session.getAttribute("role") == null || !"employee".equals(session.getAttribute("role"))) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<%-- <%
System.out.println(">>> JSP loaded: applyLeave.jsp");
%>
 --%>
<%-- <jsp:include page="LeaveStatusServlet" /> --%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Apply for Leave</title>
</head>
<body>

<h2>Apply for Leave</h2>

<form action="Demo" method="post">
  <label>Leave Type:</label>
  <select name="leaveType">
    <option value="sick">Sick</option>
    <option value="casual">Casual</option>
    <option value="earned">Earned</option>
  </select><br><br>

  <label>From Date:</label>
  <input type="date" name="fromDate" required><br><br>

  <label>To Date:</label>
  <input type="date" name="toDate" required><br><br>

  <label>Reason:</label><br>
  <textarea name="reason" required rows="4" cols="40"></textarea><br><br>

  <input type="submit" value="Submit Leave Request">
</form>
<!-- <a href="LeaveStatusServlet" target="_blank">Test Leave Status Servlet</a> -->


<h3>📌 Track Leave Request</h3>
<table border="1" cellpadding="8">
  <tr>
    <th>Start Date</th>
    <th>End Date</th>
    <th>Reason</th>
    <th>Leave Type</th>
    <th>Status</th>
    <th>Validated By</th>
  </tr>
  <%
    List<project2.LeaveRequest> trackLeaves = (List<project2.LeaveRequest>) request.getAttribute("trackLeaves");
    if (trackLeaves != null) {
      for (project2.LeaveRequest l : trackLeaves) {
  %>
  <tr>
    <td><%= l.getFromDate() %></td>
    <td><%= l.getToDate() %></td>
    <td><%= l.getReason() %></td>
    <td><%= l.getLeaveType() %></td>
    <td style="color: <%= l.getStatus().equals("Approved") ? "green" : (l.getStatus().equals("Rejected") ? "red" : "orange") %>;">
      <%= l.getStatus().equals("Approved") ? "✅ Approved" : (l.getStatus().equals("Rejected") ? "❌ Rejected" : "⏳ Pending") %>
    </td>
    <td><%= l.getManagerName() %></td>
  </tr>
  <% }} %>
</table>

<h3>📖 Leave History</h3>
<table border="1" cellpadding="8">
  <tr>
    <th>Start Date</th>
    <th>End Date</th>
    <th>Reason</th>
    <th>Leave Type</th>
    <th>Status</th>
    <th>Validated By</th>
  </tr>
  <%
    List<project2.LeaveRequest> pastLeaves = (List<project2.LeaveRequest>) request.getAttribute("pastLeaves");
    if (pastLeaves != null) {
      for (project2.LeaveRequest l : pastLeaves) {
  %>
  <tr>
    <td><%= l.getFromDate() %></td>
    <td><%= l.getToDate() %></td>
    <td><%= l.getReason() %></td>
    <td><%= l.getLeaveType() %></td>
    <td style="color: <%= l.getStatus().equals("Approved") ? "green" : (l.getStatus().equals("Rejected") ? "red" : "orange") %>;">
      <%= l.getStatus().equals("Approved") ? "✅ Approved" : (l.getStatus().equals("Rejected") ? "❌ Rejected" : "⏳ Pending") %>
    </td>
    <td><%= l.getManagerName() %></td>
  </tr>
  <% }} %>
</table>


</body>
</html>
