package project2;

import java.sql.Date;

public class LeaveRequest {
    private int id;
    private String name;
    private String email;
    private String leaveType;
    private Date fromDate;
    private Date toDate;
    private String reason;
    private String status;
    private String managerName;
    private String managerId;
    private String decisionDate;

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getLeaveType() {
        return leaveType;
    }
    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Date getFromDate() {
        return fromDate;
    }
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getManagerName() {
        return managerName;
    }
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
	public String getManagerId() {
        return managerId;
    }
	public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
	public String getDecisionDate() {
        return decisionDate;
    }
	public void setDecisionDate(String decisionDate) {
        this.decisionDate = decisionDate;
    }
}

