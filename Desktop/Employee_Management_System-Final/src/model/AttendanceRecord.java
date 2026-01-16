/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.time.LocalDate;
import java.time.LocalTime;
/**
 *
 * @author roshitlamichhane
 */
public class AttendanceRecord {
     private final String username;
    private final LocalDate date;
    private LocalTime punchIn;
    private LocalTime punchOut;

    public AttendanceRecord(String username, LocalDate date, LocalTime punchIn) {
        this.username = username;
        this.date = date;
        this.punchIn = punchIn;
        this.punchOut = null;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getPunchIn() {
        return punchIn;
    }

    public LocalTime getPunchOut() {
        return punchOut;
    }

    public void setPunchOut(LocalTime punchOut) {
        this.punchOut = punchOut;
    }

    public boolean isOpen() {
        return punchIn != null && punchOut == null;
    }
}
