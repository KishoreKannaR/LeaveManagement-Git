<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
    <title>Login - Leave Management</title>
</head>
<body>
    <h2>Login</h2>
    <form action="LoginServlet" method="post">
        Email: <input type="text" name="email" required><br><br>
        Password: <input type="password" name="password" required><br><br>
        <input type="submit" value="Login">
    </form>
    <script>
    function togglePassword() {
    	var x = document.getElementById("passwordField");
    	x.type = (x.type === "password") ? "text" : "password";
    	}
    </script>
</body>
</html>