/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.time.LocalDate;
/**
 *
 * @author roshitlamichhane
 */
public class LeaveRequest {
  private final String username;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final String leaveType;
    private final String reason;

    private String status;   // Pending / Approved / Rejected
    private String remarks;  // admin remarks

    public LeaveRequest(String username, LocalDate fromDate, LocalDate toDate,
                        String leaveType, String reason) {
        this.username = username;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.leaveType = leaveType;
        this.reason = reason;
        this.status = "Pending";
        this.remarks = "";
    }

    public String getUsername() { return username; }
    public LocalDate getFromDate() { return fromDate; }
    public LocalDate getToDate() { return toDate; }
    public String getLeaveType() { return leaveType; }
    public String getReason() { return reason; }

    public String getStatus() { return status; }
    public String getRemarks() { return remarks; }

    public void setStatus(String status) { this.status = status; }
    public void setRemarks(String remarks) { this.remarks = remarks; }  
}
