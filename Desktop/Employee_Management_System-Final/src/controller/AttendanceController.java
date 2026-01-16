package controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import model.AttendanceRecord;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;

public class AttendanceController {

    // Temporary (in-memory) attendance storage (shared across all windows)
    private static final List<AttendanceRecord> attendanceRecords = new ArrayList<>();
  
    // MANUAL STACK (LIFO)
    private static final int MAX_STACK_SIZE = 2000;
    private static final AttendanceRecord[] attendanceStack = new AttendanceRecord[MAX_STACK_SIZE];
    private static int top = -1;

    // -------------------------
    // Punch In / Punch Out
    // -------------------------

    // No double punch-in per user per day
    public static synchronized boolean punchIn(String username, LocalDate date, LocalTime punchInTime) {
        if (username == null || username.isBlank() || date == null || punchInTime == null) return false;

        for (AttendanceRecord r : attendanceRecords) {
            if (username.equals(r.getUsername()) && date.equals(r.getDate())) {
                return false; // already has record for today
            }
        }

        AttendanceRecord rec = new AttendanceRecord(username, date, punchInTime);
        attendanceRecords.add(rec);   // history
        pushAttendance(rec);          // LIFO stack (manual)
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

    // -------------------------
    // Manual Stack Helpers
    // -------------------------

    private static boolean isStackEmpty() {
        return top == -1;
    }

    private static boolean isStackFull() {
        return top == MAX_STACK_SIZE - 1;
    }

    // PUSH - add record to top of stack
    private static boolean pushAttendance(AttendanceRecord record) {
        if (record == null) return false;
        if (isStackFull()) return false;
        attendanceStack[++top] = record;
        return true;
    }

    // POP - remove record from top of stack
    private static AttendanceRecord popAttendance() {
        if (isStackEmpty()) return null;
        AttendanceRecord r = attendanceStack[top];
        attendanceStack[top] = null;
        top--;
        return r;
    }

    // PEEK - view record at top (no remove)
    private static AttendanceRecord peekAttendance() {
        if (isStackEmpty()) return null;
        return attendanceStack[top];
    }

    private static int getStackSizeInternal() {
        return top + 1;
    }

    // For table display: newest first (top -> bottom)
    public static synchronized List<AttendanceRecord> getAttendanceStackAsList() {
        List<AttendanceRecord> list = new ArrayList<>();
        for (int i = top; i >= 0; i--) {
            if (attendanceStack[i] != null) list.add(attendanceStack[i]);
        }
        return list;
    }

    // Optional demo helpers
    public static synchronized AttendanceRecord peekLastPunchIn() {
        return peekAttendance();
    }

    public static synchronized AttendanceRecord popLastPunchIn() {
        return popAttendance();
    }

    public static synchronized int getAttendanceStackSize() {
        return getStackSizeInternal();
    }
    // Count how many employees are currently present today
// Present = punched in today AND not punched out yet
public static synchronized int countPresentToday() {
    LocalDate today = LocalDate.now();
    HashSet<String> workingUsers = new HashSet<>();

    List<AttendanceRecord> records = getAttendanceStackAsList();
    for (AttendanceRecord r : records) {
        if (r == null) continue;
        if (r.getDate() == null) continue;

        if (!today.equals(r.getDate())) continue;

        if (r.getPunchIn() != null && r.getPunchOut() == null) {
            String u = r.getUsername();
            if (u != null && !u.isBlank()) {
                workingUsers.add(u.trim().toLowerCase());
            }
        }
    }
    return workingUsers.size();
}
    
    public static List<AttendanceRecord> linearSearchAttendance(List<AttendanceRecord> list, String key) {
    List<AttendanceRecord> result = new LinkedList<>();

    if (list == null || key == null || key.isBlank()) {
        return result;
    }

    String target = key.trim().toLowerCase();

    for (AttendanceRecord r : list) {
        if (r == null) continue;

        String u = (r.getUsername() == null) ? "" : r.getUsername().trim().toLowerCase();
        String d = (r.getDate() == null) ? "" : r.getDate().toString().trim().toLowerCase();

        // match either username or date
        if (u.contains(target) || d.contains(target)) {
            result.add(r);
        }
    }

    return result;
}
}
