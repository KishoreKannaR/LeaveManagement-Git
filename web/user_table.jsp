<%@ page import="java.util.*, project2.User" %>
<html>
<head>
    <title>User Management</title>
    <style>
        .highlight {
            background-color: #ffff99;
        }
    </style>
    <script>
        function getFormData() {
            return {
                id: document.getElementById("id").value.trim(),
                name: document.getElementById("name").value.trim(),
                email: document.getElementById("email").value.trim(),
                password: document.getElementById("password").value.trim(),
                role: document.getElementById("role").value
            };
        }

        function sendRequest(action) {
            const formData = getFormData();

            if (action === "Add") {
                if (!formData.id || !formData.name || !formData.email || !formData.password || !formData.role) {
                    alert("All fields are required for Add operation.");
                    return;
                }

                const step1 = confirm("Are you sure you want to add this user?");
                if (!step1) return;

                const step2 = confirm("Please confirm the entered user details:\n\n" +
                    "ID: " + formData.id + "\n" +
                    "Name: " + formData.name + "\n" +
                    "Email: " + formData.email + "\n" +
                    "Password: ***\n" +
                    "Role: " + formData.role);

                if (!step2) return;
            }

            if (action === "Remove") {
                if (!formData.id && !formData.email) {
                    alert("Please enter either ID or Email to remove a user.");
                    return;
                }

                const extra = "ID: " + (formData.id || "N/A") + "\n" +
                              "Name: " + (formData.name || "N/A") + "\n" +
                              "Email: " + (formData.email || "N/A") + "\n" +
                              "Role: " + (formData.role || "N/A");

                const confirmMsg = "Are you sure you want to remove this user?\n\n" + extra;
                const confirmed = confirm(confirmMsg);
                if (!confirmed) return;
                formData.confirmed = "true";
            }

            formData.action = action;

            fetch("AdminUserServlet", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: new URLSearchParams(formData)
            })
            .then(response => response.text())
            .then(html => {
                document.open();
                document.write(html);
                document.close();
            });
        }

        function clearForm() {
            document.getElementById("id").value = "";
            document.getElementById("name").value = "";
            document.getElementById("email").value = "";
            document.getElementById("password").value = "";
            document.getElementById("role").value = "";

            sendRequest("FetchAll");
        }
    </script>
</head>
<body>
    <h2>User Management</h2>
    <form onsubmit="event.preventDefault();">
        ID: <input type="text" id="id" name="id" value="<%= request.getAttribute("id") != null ? request.getAttribute("id") : "" %>"><br>
        Name: <input type="text" id="name" name="name" value="<%= request.getAttribute("name") != null ? request.getAttribute("name") : "" %>"><br>
        Email: <input type="text" id="email" name="email" value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"><br>
        Password: <input type="text" id="password" name="password" value="<%= request.getAttribute("password") != null ? request.getAttribute("password") : "" %>"><br>
        Role:
        <select id="role" name="role">
            <option value="" <%= request.getAttribute("role") == null || "".equals(request.getAttribute("role")) ? "selected" : "" %>>Select Role</option>
            <option value="employee" <%= "Employee".equals(request.getAttribute("role")) ? "selected" : "" %>>Employee</option>
            <option value="manager" <%= "Manager".equals(request.getAttribute("role")) ? "selected" : "" %>>Manager</option>
            <option value="admin" <%= "Admin".equals(request.getAttribute("role")) ? "selected" : "" %>>Admin</option>
        </select><br><br>

        <button type="button" onclick="sendRequest('Search')">Search</button>
        <button type="button" onclick="sendRequest('Add')">Add</button>
        <button type="button" onclick="sendRequest('Update')">Update</button>
        <button type="button" onclick="sendRequest('Remove')">Remove</button>
        <button type="button" onclick="clearForm()">Clear</button>
    </form>

    <%-- Message Display --%>
    <% if (request.getAttribute("message") != null) { %>
        <p style="color: green;"><%= request.getAttribute("message") %></p>
    <% } %>

    <hr>

    <%
        List<User> userList = (List<User>) request.getAttribute("userList");
        User searchUser = (User) request.getAttribute("searchUser");
        int highlightedId = (searchUser != null) ? searchUser.getId() : -1;
    %>

    <table border="1" cellpadding="5" cellspacing="0">
        <tr style="background-color: #eee;">
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Password</th>
            <th>Role</th>
        </tr>
        <%
            if (userList != null) {
                for (User user : userList) {
                    boolean isHighlighted = user.getId() == highlightedId;
        %>
        <tr class="<%= isHighlighted ? "highlight" : "" %>">
            <td><%= user.getId() %></td>
            <td><%= user.getName() %></td>
            <td><%= user.getEmail() %></td>
            <td><%= user.getPassword() %></td>
            <td><%= user.getRole() %></td>
        </tr>
        <%
                }
            }
        %>
    </table>
</body>
</html>
