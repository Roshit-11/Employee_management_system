package model;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;

public class AdminModel {

    private static final String FILE_PATH = "users.txt";

    private final List<Admin> admins = new ArrayList<>();
    private final List<Employee> employees = new ArrayList<>();

    public AdminModel() {
    }

    // --------------------------------------------------
    // COMMON: check if username exists (admin or employee)
    // --------------------------------------------------
    public boolean isUserExists(String username) {
        // in-memory check
        for (Admin a : admins) {
            if (a.getUsername().equalsIgnoreCase(username)) return true;
        }
        for (Employee e : employees) {
            if (e.getUsername().equalsIgnoreCase(username)) return true;
        }

        // file check
        File file = new File(FILE_PATH);
        if (!file.exists()) return false;

        String search = "\"username\":\"" + escape(username) + "\"";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(search)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    // --------------------------------------------------
    // ADMIN saving
    // --------------------------------------------------
    public void saveAdmin(Admin admin) throws IOException {
        admins.add(admin);
        String json = toJsonAdmin(admin);
        writeLineToFile(json);
    }

    private String toJsonAdmin(Admin admin) {
        return "{"
                + "\"role\":\"admin\","
                + "\"name\":\""      + escape(admin.getName())     + "\","
                + "\"username\":\""  + escape(admin.getUsername()) + "\","
                + "\"email\":\""     + escape(admin.getEmail())    + "\","
                + "\"password\":\""  + escape(admin.getPassword()) + "\""
                + "}";
    }

    // --------------------------------------------------
    // EMPLOYEE saving
    // --------------------------------------------------
    public void saveEmployee(Employee emp) throws IOException {
        employees.add(emp);
        String json = toJsonEmployee(emp);
        writeLineToFile(json);
    }

    private String toJsonEmployee(Employee emp) {
        return "{"
                + "\"role\":\"employee\","
                + "\"fullName\":\""     + escape(emp.getFullName())    + "\","
                + "\"username\":\""     + escape(emp.getUsername())    + "\","
                + "\"email\":\""        + escape(emp.getEmail())       + "\","
                + "\"phone\":\""        + escape(emp.getPhone())       + "\","
                + "\"department\":\""   + escape(emp.getDepartment())  + "\","
                + "\"employeeType\":\"" + escape(emp.getEmployeeType())+ "\","
                + "\"address\":\""      + escape(emp.getAddress())     + "\","
                + "\"password\":\""     + escape(emp.getPassword())    + "\""
                + "}";
    }

    // --------------------------------------------------
    // Common helpers
    // --------------------------------------------------
    private void writeLineToFile(String json) throws IOException {
        File file = new File(FILE_PATH);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(json);
            bw.newLine();
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }

    public List<Admin> getAdmins() {
        return admins;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
    public String authenticateUser(String username, String password) {
    File file = new File(FILE_PATH);

    if (!file.exists()) {
        return null;
    }

    String userPattern = "\"username\":\"" + escape(username) + "\"";
    String passPattern = "\"password\":\"" + escape(password) + "\"";

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {

            // Does line contain matching username + password?
            if (line.contains(userPattern) && line.contains(passPattern)) {

                // Determine role
                if (line.contains("\"role\":\"admin\"")) {
                    return "admin";
                } 
                if (line.contains("\"role\":\"employee\"")) {
                    return "employee";
                }

                return "unknown";
            }
        }
    } catch (IOException ex) {
        ex.printStackTrace();
    }

    return null; // no match
    }
    // ===== Temporary (in-memory) attendance storage (shared across all windows) =====
private static final List<AttendanceRecord> attendanceRecords = new ArrayList<>();
// FIFO queue for attendance events (DSA)
private static final Queue<AttendanceRecord> attendanceQueue = new ArrayDeque<>();

// ✅ No double punch-in per user per day
public static synchronized boolean punchIn(String username, LocalDate date, LocalTime punchInTime) {
    if (username == null || username.isBlank() || date == null || punchInTime == null) return false;

    for (AttendanceRecord r : attendanceRecords) {
        if (username.equals(r.getUsername()) && date.equals(r.getDate())) {
            return false; // already has record for today
        }
    }

    AttendanceRecord rec = new AttendanceRecord(username, date, punchInTime);
    attendanceRecords.add(rec);   // history for admin table
    attendanceQueue.offer(rec);   // FIFO queue implementation
    pushAttendance(rec);          // LIFO stack implementation (manual)
    return true;
}

public static synchronized boolean punchOut(String username, LocalDate date, LocalTime punchOutTime) {
    if (username == null || username.isBlank() || date == null || punchOutTime == null) return false;

    for (int i = attendanceRecords.size() - 1; i >= 0; i--) {
        AttendanceRecord r = attendanceRecords.get(i);
        if (username.equals(r.getUsername()) && date.equals(r.getDate())) {
            if (r.getPunchOut() != null) return false; // already punched out
            r.setPunchOut(punchOutTime);
            return true;
        }
    }
    return false; // no record found (no punch-in)
}

public static synchronized List<AttendanceRecord> getAllAttendanceRecords() {
    return new ArrayList<>(attendanceRecords);
}

// Process attendance in FIFO order (DSA demo)
public static synchronized AttendanceRecord processNextAttendance() {
    return attendanceQueue.poll();
}

// Optional helper: view queue size
public static synchronized int getAttendanceQueueSize() {
    return attendanceQueue.size();
}

// =====================
// MANUAL STACK (LIFO)
// =====================
private static final int MAX_STACK_SIZE = 2000;
private static final AttendanceRecord[] attendanceStack = new AttendanceRecord[MAX_STACK_SIZE];
private static int top = -1;

private static boolean isStackEmpty() {
    return top == -1;
}

private static boolean isStackFull() {
    return top == MAX_STACK_SIZE - 1;
}

// PUSH - add record to the top of stack
private static boolean pushAttendance(AttendanceRecord record) {
    if (record == null) return false;
    if (isStackFull()) return false;
    attendanceStack[++top] = record;
    return true;
}

// POP - remove record from the top of stack
private static AttendanceRecord popAttendance() {
    if (isStackEmpty()) return null;
    AttendanceRecord r = attendanceStack[top];
    attendanceStack[top] = null; // clear reference
    top--;
    return r;
}

// PEEK - view record at the top without removing
private static AttendanceRecord peekAttendance() {
    if (isStackEmpty()) return null;
    return attendanceStack[top];
}

private static int getStackSizeInternal() {
    return top + 1;
}

// For table / display: newest punch-ins first (top -> bottom)
public static synchronized List<AttendanceRecord> getAttendanceStackAsList() {
    List<AttendanceRecord> list = new ArrayList<>();
    for (int i = top; i >= 0; i--) {
        if (attendanceStack[i] != null) list.add(attendanceStack[i]);
    }
    return list;
}

// Optional: demo helpers
public static synchronized AttendanceRecord peekLastPunchIn() {
    return peekAttendance();
}

public static synchronized AttendanceRecord popLastPunchIn() {
    return popAttendance();
}

public static synchronized int getAttendanceStackSize() {
    return getStackSizeInternal();
}
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
public static boolean enqueueLeave(LeaveRequest req) {
    if (req == null || isLeaveQueueFull()) return false;

    leaveRear = (leaveRear + 1) % MAX_LEAVE_QUEUE_SIZE;
    leaveQueue[leaveRear] = req;
    leaveSize++;
    return true;
}

// DEQUEUE (remove from front)
public static LeaveRequest dequeueLeave() {
    if (isLeaveQueueEmpty()) return null;

    LeaveRequest req = leaveQueue[leaveFront];
    leaveQueue[leaveFront] = null;
    leaveFront = (leaveFront + 1) % MAX_LEAVE_QUEUE_SIZE;
    leaveSize--;
    return req;
}

// Convert queue -> list in FIFO order (oldest first)
public static java.util.List<LeaveRequest> getAllLeavesFIFO() {
    java.util.List<LeaveRequest> list = new java.util.ArrayList<>();
    if (isLeaveQueueEmpty()) return list;

    int idx = leaveFront;
    for (int i = 0; i < leaveSize; i++) {
        list.add(leaveQueue[idx]);
        idx = (idx + 1) % MAX_LEAVE_QUEUE_SIZE;
    }
    return list;
}

// Employee view: only current user's requests

public static java.util.List<LeaveRequest> getLeavesForUserFIFO(String username) {
    java.util.List<LeaveRequest> all = getAllLeavesFIFO();
    java.util.List<LeaveRequest> mine = new java.util.ArrayList<>();
    for (LeaveRequest r : all) {
        if (r.getUsername().equals(username)) mine.add(r);
    }
    return mine;
}

// ===============================
// Leave queue helpers for Admin UI
// ===============================

// Peek (do not remove) the oldest PENDING request, FIFO order
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

// Update status/remarks for a request already stored in the queue
public static synchronized boolean updateLeaveDecision(LeaveRequest target, String newStatus, String remarks) {
    if (target == null || newStatus == null || newStatus.isBlank()) return false;

    // object is stored by reference in the queue, so update is enough
    target.setStatus(newStatus);
    target.setRemarks(remarks == null ? "" : remarks);
    return true;
}

// Admin table: show all leaves FIFO
public static synchronized java.util.List<LeaveRequest> getAllLeavesForAdminFIFO() {
    return getAllLeavesFIFO();
}

// Remove a specific request from the manual leave queue while preserving FIFO order
public static synchronized boolean removeLeaveRequestFromQueue(LeaveRequest target) {
    if (target == null || isLeaveQueueEmpty()) return false;

    int n = leaveSize; // capture current size
    boolean removed = false;

    for (int i = 0; i < n; i++) {
        LeaveRequest r = dequeueLeave();
        if (r == null) continue;

        // Remove only the first matching reference
        if (!removed && r == target) {
            removed = true;
            continue;
        }

        // Keep the rest in the same order
        enqueueLeave(r);
    }

    return removed;
}
}
