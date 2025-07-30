<%@ page import="javax.servlet.http.*,javax.servlet.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session == null || session.getAttribute("role") == null || !"manager".equals(session.getAttribute("role"))) {
        response.sendRedirect("login.html");
        return;
    }
%>
<html>
<head>
    <title>Manager Dashboard</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/fullcalendar@5.11.3/main.min.css">
    <script src="https://cdn.jsdelivr.net/npm/fullcalendar@5.11.3/main.min.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            overflow: hidden;
        }

        .fc-event.normal-event {
            background-color: #007BFF !important;
            color: white !important;
            border: 1px solid #004A99 !important;
        }

        .fc-event.holiday-event {
            background-color: #FF4C4C !important;
            color: white !important;
            border: 1px solid #CC0000 !important;
        }

        .fc .fc-daygrid-day.fc-day-today {
            background-color: #ffebcc !important;
            border: 2px solid black !important;
            color: #000 !important;
            font-weight: bold;
        }

        .fc .fc-daygrid-day.fc-day-today .fc-daygrid-day-number {
            background-color: #ff9900;
            color: white;
            padding: 3px 3px;
            border-radius: 50%;
        }

        #container {
            display: flex;
            height: 100vh;
        }

        #left-panel {
            flex: 2;
            padding: 20px;
            border-right: 1px solid #ddd;
            overflow-y: auto;
        }

        #right-panel {
            flex: 0 0 320px;
            padding: 10px;
            display: flex;
            flex-direction: column;
            align-items: stretch;
            background: #f4f4f4;
        }

        #calendar-container {
            height: 260px;
            margin-bottom: 6px;
        }

        #calendar {
            width: 100%;
            height: 100%;
            font-size: 11px;
        }

        button {
            padding: 6px 10px;
            font-size: 13px;
            margin: 4px 0;
            cursor: pointer;
            border: none;
            border-radius: 4px;
        }

        #fullscreenToggle {
            background: #444;
            color: white;
        }

        #manageEventBtn {
            background-color: #007BFF;
            color: white;
        }

        #event-list {
            background: #fff;
            padding: 10px;
            border-radius: 5px;
            margin-top: 6px;
            max-height: 120px;
            overflow-y: auto;
            font-size: 13px;
        }

        #eventList {
            padding-left: 20px;
        }

        #eventModal {
            display: none;
            position: fixed;
            z-index: 999;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
            width: 300px;
            resize: both;
            overflow: auto;
            cursor: move;
        }

        #eventModal input,
        #eventModal textarea,
        #eventModal select {
            width: 100%;
            margin-bottom: 10px;
            padding: 6px;
        }

        #eventModal button {
            margin-right: 5px;
        }

        #calendar.fullscreen {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 90vw;
            height: 90vh;
            background: white;
            z-index: 1000;
            box-shadow: 0 0 20px rgba(0,0,0,0.3);
        }

        #eventModalHeader {
            font-weight: bold;
            cursor: move;
            margin-bottom: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        #closeModalBtn {
            background: none;
            border: none;
            font-size: 18px;
            color: #333;
            cursor: pointer;
        }
    </style>
</head>
<body>
 <%--    <h2>Welcome, <%= session.getAttribute("name") %>!</h2>
    <p>This is your Manager Dashboard.</p>
    <a href="ManagerDashboardServlet">Leave Apply Status</a><br>
    <a href="LogoutServlet">Logout</a> --%>
  <div id="container">
    <div id="left-panel">
        <h2>Welcome, <%= session.getAttribute("name") %>!</h2>
        <p>This is your Manager Dashboard.</p>
       <a href="ManagerDashboardServlet">Leave Apply Status</a> |
        <a href="LogoutServlet">Logout</a>
        <h3>Manager Tools</h3>
        <p>You can manage leave applications of the employee.</p>
    </div>

    <div id="right-panel">
        <div id="calendar-container">
            <div id="calendar"></div>
        </div>
        <button id="fullscreenToggle">🔍 Expand Calendar</button>
        <button id="manageEventBtn">📅 Manage Events</button>

        <div id="event-list">
            <h4>Upcoming Events</h4>
            <ul id="eventList"></ul>
        </div>
    </div>
</div>

<div id="eventModal">
    <div id="eventModalHeader">
        Manage Event
        <button id="closeModalBtn" onclick="closeModal()">✖</button>
    </div>
    Title<br><input type="text" id="title" placeholder="Title">
    Start Date<br><input type="date" id="startDate" placeholder="Start Date">
    End Date<br><input type="date" id="endDate" placeholder="End Date">
    Description<br><textarea id="description" placeholder="Event Description"></textarea>
    <label for="createdBy">Created by:</label>
    <input type="text" id="createdBy" value="<%= session.getAttribute("name") %>" readonly />


    <label for="visibleTo">Visible To:</label>
    <select id="visibleTo">
        <option value="All">All</option>
        <option value="Employee">Employee</option>
        <option value="Manager">Manager</option>
        <option value="Admin">Admin</option>
    </select>
    <button onclick="submitEvent('add')">Add</button>
    <button onclick="submitEvent('update')">Update</button>
    <button onclick="submitEvent('delete')">Delete</button>
</div>


<script>
    document.addEventListener('DOMContentLoaded', function () {
        const calendarEl = document.getElementById('calendar');
        const calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            height: '100%',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: ''
            },
            events: {
                url: '/project2/GetEventsServlet',
                failure: function () {
                    alert('Error fetching events');
                }
            },
            nowIndicator: true,
            dayMaxEventRows: true,
            selectable: false,
            eventDisplay: 'block'
        });
        calendar.render();

        document.getElementById('fullscreenToggle').addEventListener('click', () => {
            calendarEl.classList.toggle('fullscreen');
            calendar.updateSize();
        });

        document.getElementById('manageEventBtn').addEventListener('click', () => {
            document.getElementById('eventModal').style.display = 'block';
        });
        
        console.log("Is eventList in DOM?", document.getElementById("eventList"));
        console.log("All ULs:", document.querySelectorAll("ul"));
        
        
        // Load upcoming events into side list
        fetch('/project2/GetEventsServlet')
            .then(response => response.json())
            .then(events => {
            	console.log("Fetched events:", events);
 
                const list = document.getElementById("eventList");
                console.log("List element:", list); // should NOT be null
                list.innerHTML = '';
                console.log("List element:", list); // should NOT be null
                events.forEach(event => {
                    const li = document.createElement('li');
                    let title = event.title || "Untitled";
                    console.log("List element:", list); // should NOT be null

                 // Normalize date strings
                 let start = (typeof event.start === 'string') ? event.start : (event.start?.toISOString?.().split('T')[0] || '');
                 let end = (typeof event.end === 'string') ? event.end : (event.end?.toISOString?.().split('T')[0] || '');

                 // Display format
                 if (start && end && start !== end) {
                     li.textContent = `${title} (${start} to ${end})`;
                 } else if (start) {
                     li.textContent = `${title} (${start})`;
                 } else {
                     li.textContent = `${title}`;
                 }
                    list.appendChild(li);
                });
                console.log("List element:", list); // should NOT be null
            });

        // Dynamically populate createdBy dropdown
        fetch('/project2/GetAdminAndManagerNamesServlet')
            .then(res => res.json())
            .then(data => {
                const select = document.getElementById("createdBy");
                select.innerHTML = '<option value="">Select</option>';
                data.forEach(user => {
                    const option = document.createElement("option");
                    option.value = user.name;
                    option.textContent = `${user.name} (${user.role})`;
                    select.appendChild(option);
                });
            })
            .catch(err => {
                console.error("Error loading user list:", err);
                document.getElementById("createdBy").innerHTML = "<option>Error</option>";
            });
    });

    function closeModal() {
        document.getElementById('eventModal').style.display = 'none';
    }

    function submitEvent(action) {
        const title = document.getElementById("title").value;
        const start = document.getElementById("startDate").value.trim();
        let end = document.getElementById("endDate").value.trim();
        if (end === "") {
            end = start; // Use start as end if end is empty
        }
        const description = document.getElementById("description").value.trim();
        const createdBy = document.getElementById("createdBy").value.trim();
        const visibleTo = document.getElementById("visibleTo").value;

        if (!title || !start || !createdBy) {
            alert("Please fill in all required fields: Title, Start Date.");
            return;
        }

        const formData = new URLSearchParams();
        formData.append("action", action);
        formData.append("title", title);
        formData.append("start", start);
        formData.append("end", end);
        formData.append("description", description);
        formData.append("createdBy", createdBy);
        formData.append("visibleTo", visibleTo);

        fetch("/project2/EventServlet", {
            method: "POST",
            body: formData,
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            }
        })
        .then(response => {
            if (response.ok) {
                alert(`${action.toUpperCase()} successful`);
                location.reload();
            } else {
                alert("Something went wrong.");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Error processing event.");
        });

        closeModal();
    }

    // Draggable modal
    const modal = document.getElementById("eventModal");
    const header = document.getElementById("eventModalHeader");
    let offsetX, offsetY;

    header.onmousedown = function (e) {
        offsetX = e.clientX - modal.offsetLeft;
        offsetY = e.clientY - modal.offsetTop;

        document.onmousemove = function (e) {
            modal.style.left = (e.clientX - offsetX) + "px";
            modal.style.top = (e.clientY - offsetY) + "px";
        }

        document.onmouseup = function () {
            document.onmousemove = null;
            document.onmouseup = null;
        }
    };
</script>

    
</body>
</html>
