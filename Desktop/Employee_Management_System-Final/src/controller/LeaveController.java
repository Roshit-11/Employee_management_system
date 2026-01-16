package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.LeaveRequest;

public class LeaveController {

    // ===============================
    // Manual Leave Request Queue (FIFO)
    // ===============================
    private static final int MAX_LEAVE_QUEUE_SIZE = 500;
    private static final LeaveRequest[] leaveQueue = new LeaveRequest[MAX_LEAVE_QUEUE_SIZE];

    private static int leaveFront = 0;
    private static int leaveRear = -1;
    private static int leaveSize = 0;

    public static boolean isLeaveQueueEmpty() {
        return leaveSize == 0;
    }

    public static boolean isLeaveQueueFull() {
        return leaveSize == MAX_LEAVE_QUEUE_SIZE;
    }

    // ENQUEUE (add to rear)
    public static synchronized boolean enqueueLeave(LeaveRequest req) {
        if (req == null || isLeaveQueueFull()) return false;

        leaveRear = (leaveRear + 1) % MAX_LEAVE_QUEUE_SIZE;
        leaveQueue[leaveRear] = req;
        leaveSize++;
        return true;
    }

    // DEQUEUE (remove from front)
    public static synchronized LeaveRequest dequeueLeave() {
        if (isLeaveQueueEmpty()) return null;

        LeaveRequest req = leaveQueue[leaveFront];
        leaveQueue[leaveFront] = null;
        leaveFront = (leaveFront + 1) % MAX_LEAVE_QUEUE_SIZE;
        leaveSize--;
        return req;
    }

    // Convert queue -> list FIFO (oldest first)
    public static synchronized List<LeaveRequest> getAllLeavesFIFO() {
        List<LeaveRequest> list = new ArrayList<>();
        if (isLeaveQueueEmpty()) return list;

        int idx = leaveFront;
        for (int i = 0; i < leaveSize; i++) {
            list.add(leaveQueue[idx]);
            idx = (idx + 1) % MAX_LEAVE_QUEUE_SIZE;
        }
        return list;
    }

    // Employee view: only current user's requests
    public static synchronized List<LeaveRequest> getLeavesForUserFIFO(String username) {
        List<LeaveRequest> all = getAllLeavesFIFO();
        List<LeaveRequest> mine = new ArrayList<>();
        for (LeaveRequest r : all) {
            if (r != null && r.getUsername().equals(username)) mine.add(r);
        }
        return mine;
    }

    // Peek (do not remove) the oldest PENDING request
    public static synchronized LeaveRequest peekNextPendingLeave() {
        if (isLeaveQueueEmpty()) return null;

        int idx = leaveFront;
        for (int i = 0; i < leaveSize; i++) {
            LeaveRequest r = leaveQueue[idx];
            if (r != null && "Pending".equalsIgnoreCase(r.getStatus())) {
                return r;
            }
            idx = (idx + 1) % MAX_LEAVE_QUEUE_SIZE;
        }
        return null;
    }

    // Update status/remarks (admin decision)
    public static synchronized boolean updateLeaveDecision(LeaveRequest target, String newStatus, String remarks) {
        if (target == null || newStatus == null || newStatus.isBlank()) return false;

        target.setStatus(newStatus);
        target.setRemarks(remarks == null ? "" : remarks);
        return true;
    }

    // Admin table: show all FIFO
    public static synchronized List<LeaveRequest> getAllLeavesForAdminFIFO() {
        return getAllLeavesFIFO();
    }

    // Remove a specific request from queue while preserving FIFO order
    public static synchronized boolean removeLeaveRequestFromQueue(LeaveRequest target) {
        if (target == null || isLeaveQueueEmpty()) return false;

        int n = leaveSize;
        boolean removed = false;

        for (int i = 0; i < n; i++) {
            LeaveRequest r = dequeueLeave();
            if (r == null) continue;

            if (!removed && r == target) {
                removed = true;
                continue;
            }
            enqueueLeave(r);
        }
        return removed;
    }

    // Update fields (employee side) only if Pending and at least one change
    public static synchronized boolean updateLeaveRequestFields(
            LeaveRequest target,
            LocalDate newFromDate,
            LocalDate newToDate,
            String newLeaveType,
            String newReason) {

        if (target == null || newFromDate == null || newToDate == null
                || newLeaveType == null || newReason == null) {
            return false;
        }

        if (!"Pending".equalsIgnoreCase(target.getStatus())) {
            return false;
        }

        boolean changed = false;

        if (!newFromDate.equals(target.getFromDate())) {
            target.setFromDate(newFromDate);
            changed = true;
        }
        if (!newToDate.equals(target.getToDate())) {
            target.setToDate(newToDate);
            changed = true;
        }
        if (!newLeaveType.equalsIgnoreCase(target.getLeaveType())) {
            target.setLeaveType(newLeaveType);
            changed = true;
        }
        if (!newReason.equals(target.getReason())) {
            target.setReason(newReason);
            changed = true;
        }

        return changed;
    }

    // Result codes for employee leave update
    public enum LeaveUpdateResult {
        UPDATED,
        NO_CHANGES,
        NOT_PENDING,
        INVALID
    }

    // Employee-side update wrapper that returns result code
    public static synchronized LeaveUpdateResult updateEmployeeLeaveRequest(
            LeaveRequest target,
            LocalDate newFromDate,
            LocalDate newToDate,
            String newLeaveType,
            String newReason) {

        if (target == null || newFromDate == null || newToDate == null
                || newLeaveType == null || newReason == null) {
            return LeaveUpdateResult.INVALID;
        }

        if (!"Pending".equalsIgnoreCase(target.getStatus())) {
            return LeaveUpdateResult.NOT_PENDING;
        }

        boolean changed = updateLeaveRequestFields(target, newFromDate, newToDate, newLeaveType, newReason);
        return changed ? LeaveUpdateResult.UPDATED : LeaveUpdateResult.NO_CHANGES;
    }
 // Count total leave requests whose status is Pending
    public static synchronized int countPendingLeaves() {
    int count = 0;
    List<LeaveRequest> requests = getAllLeavesFIFO();
    for (LeaveRequest r : requests) {
        if (r == null) continue;
        String st = r.getStatus();
        if (st != null && st.equalsIgnoreCase("Pending")) {
            count++;
        }
    }
    return count;
}
    public static class EmployeeLeaveUpdateResult {
    public final boolean success;
    public final String message;
    public final LeaveUpdateResult code;

    public EmployeeLeaveUpdateResult(boolean success, String message, LeaveUpdateResult code) {
        this.success = success;
        this.message = message;
        this.code = code;
    }
}

public static synchronized EmployeeLeaveUpdateResult validateAndUpdateEmployeeLeave(
        LeaveRequest target,
        String leaveType,
        String reason,
        Integer fromDay, String fromMonthName, Integer fromYear,
        Integer toDay, String toMonthName, Integer toYear) {

    if (target == null) {
        return new EmployeeLeaveUpdateResult(false, "Select a leave request first.", LeaveUpdateResult.INVALID);
    }

    if (target.getStatus() == null || !target.getStatus().equalsIgnoreCase("Pending")) {
        return new EmployeeLeaveUpdateResult(false, "Only PENDING requests can be updated.", LeaveUpdateResult.NOT_PENDING);
    }

    if (leaveType == null || leaveType.isBlank()) {
        return new EmployeeLeaveUpdateResult(false, "Select leave type (Paid/Unpaid).", LeaveUpdateResult.INVALID);
    }

    if (reason == null || reason.isBlank()) {
        return new EmployeeLeaveUpdateResult(false, "Write leave reason.", LeaveUpdateResult.INVALID);
    }

    if (fromDay == null || fromYear == null || toDay == null || toYear == null ||
        fromMonthName == null || fromMonthName.isBlank() ||
        toMonthName == null || toMonthName.isBlank()) {
        return new EmployeeLeaveUpdateResult(false, "Please select valid From/To dates.", LeaveUpdateResult.INVALID);
    }

    int fromMonth = monthNameToNumber(fromMonthName);
    int toMonth = monthNameToNumber(toMonthName);

    LocalDate fromDate;
    LocalDate toDate;

    try {
        fromDate = LocalDate.of(fromYear, fromMonth, fromDay);
        toDate = LocalDate.of(toYear, toMonth, toDay);
    } catch (Exception ex) {
        return new EmployeeLeaveUpdateResult(false, "Invalid date selected.", LeaveUpdateResult.INVALID);
    }

    if (toDate.isBefore(fromDate)) {
        return new EmployeeLeaveUpdateResult(false, "To Date cannot be before From Date.", LeaveUpdateResult.INVALID);
    }

    LeaveUpdateResult res = updateEmployeeLeaveRequest(target, fromDate, toDate, leaveType, reason);

    switch (res) {
        case UPDATED:
            return new EmployeeLeaveUpdateResult(true, "Leave updated successfully.", res);
        case NO_CHANGES:
            return new EmployeeLeaveUpdateResult(false, "No changes detected.", res);
        case NOT_PENDING:
            return new EmployeeLeaveUpdateResult(false, "Request is no longer Pending. Update not allowed.", res);
        case INVALID:
        default:
            return new EmployeeLeaveUpdateResult(false, "Invalid update. Please check your values.", res);
    }
}// ==============================
// Month name -> month number helper
// ==============================
public static int monthNameToNumber(String m) {
    if (m == null) return 1;
    switch (m.trim()) {
        case "January": return 1;
        case "February": return 2;
        case "March": return 3;
        case "April": return 4;
        case "May": return 5;
        case "June": return 6;
        case "July": return 7;
        case "August": return 8;
        case "September": return 9;
        case "October": return 10;
        case "November": return 11;
        case "December": return 12;
        default: return 1;
    }
}

public static String monthNumberToName(int m) {
    switch (m) {
        case 1:  return "January";
        case 2:  return "February";
        case 3:  return "March";
        case 4:  return "April";
        case 5:  return "May";
        case 6:  return "June";
        case 7:  return "July";
        case 8:  return "August";
        case 9:  return "September";
        case 10: return "October";
        case 11: return "November";
        case 12: return "December";
        default: return "January";
    }
}
}