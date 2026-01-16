package controller;

import java.util.LinkedList;
import java.util.List;
import model.AdminModel;
import model.Employee;
import model.AttendanceRecord;

public class EmployeeSortController {

    /**
     * Load employees from file and sort based on ComboBox choice.
     */
    public List<Employee> getEmployeesSorted(String choice) {

        LinkedList<Employee> employees = AdminModel.loadEmployeesFromFileAsLinkedList();
        if (choice == null) return employees;

        if (choice.equals("Sort by Name (A–Z)") || choice.equals("Sort by Name (A-Z)")) {
            selectionSortByNameAZ(employees);
        } else if (choice.equals("Sort by Department (A–Z)") || choice.equals("Sort by Department (A-Z)")) {
            insertionSortByDepartmentAZ(employees);
        } else if (choice.equals("Sort by Address (A–Z)") || choice.equals("Sort by Address (A-Z)")) {
            mergeSortByAddressAZ(employees); //  Merge Sort here
        }

        return employees;
    }

    // ==================================================
    // Selection Sort — Full Name (A–Z)
    // ==================================================
    private void selectionSortByNameAZ(List<Employee> list) {
        int n = list.size();

        for (int step = 0; step < n - 1; step++) {
            int minIndex = step;

            for (int next = step + 1; next < n; next++) {
                String a = list.get(next) == null ? "" : list.get(next).getFullName();
                String b = list.get(minIndex) == null ? "" : list.get(minIndex).getFullName();

                if (a == null) a = "";
                if (b == null) b = "";

                if (a.trim().compareToIgnoreCase(b.trim()) < 0) {
                    minIndex = next;
                }
            }

            Employee temp = list.get(step);
            list.set(step, list.get(minIndex));
            list.set(minIndex, temp);
        }
    }

    // ==================================================
    // Insertion Sort — Department (A–Z)
    // ==================================================
    private void insertionSortByDepartmentAZ(List<Employee> list) {
        for (int i = 1; i < list.size(); i++) {
            Employee key = list.get(i);
            String keyDept = key == null ? "" : key.getDepartment();
            if (keyDept == null) keyDept = "";

            int j = i - 1;

            while (j >= 0) {
                Employee cur = list.get(j);
                String curDept = cur == null ? "" : cur.getDepartment();
                if (curDept == null) curDept = "";

                if (curDept.trim().compareToIgnoreCase(keyDept.trim()) > 0) {
                    list.set(j + 1, cur);
                    j--;
                } else {
                    break;
                }
            }
            list.set(j + 1, key);
        }
    }

    // ==================================================
    // Merge Sort — Address (A–Z)
    // ==================================================
    private void mergeSortByAddressAZ(List<Employee> list) {
        if (list == null || list.size() < 2) return;

        // Copy to array for simpler merge sort implementation
        Employee[] arr = list.toArray(new Employee[0]);

        mergeSortAddress(arr, 0, arr.length - 1);

        // Write back to list
        for (int i = 0; i < arr.length; i++) {
            list.set(i, arr[i]);
        }
    }

    private void mergeSortAddress(Employee[] arr, int left, int right) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;

        mergeSortAddress(arr, left, mid);
        mergeSortAddress(arr, mid + 1, right);

        mergeAddress(arr, left, mid, right);
    }

    private void mergeAddress(Employee[] arr, int left, int mid, int right) {

        int n1 = mid - left + 1;
        int n2 = right - mid;

        Employee[] L = new Employee[n1];
        Employee[] R = new Employee[n2];

        for (int i = 0; i < n1; i++) L[i] = arr[left + i];
        for (int j = 0; j < n2; j++) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            String addrL = safeAddress(L[i]);
            String addrR = safeAddress(R[j]);

            if (addrL.compareToIgnoreCase(addrR) <= 0) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    private String safeAddress(Employee e) {
        if (e == null) return "";
        String a = e.getAddress();
        return a == null ? "" : a.trim();
    }
    public List<Employee> binarySearchByName(List<Employee> list, String key) {

    List<Employee> result = new LinkedList<>();

    if (list == null || list.isEmpty() || key == null || key.isBlank()) {
        return result;
    }

    String target = key.trim().toLowerCase();

    int left = 0;
    int right = list.size() - 1;

    while (left <= right) {
        int mid = left + (right - left) / 2;

        Employee midEmp = list.get(mid);
        String midName = midEmp.getFullName() == null
                ? ""
                : midEmp.getFullName().toLowerCase();

        // Match found
        if (midName.contains(target)) {

            // Collect all matching neighbors (important!)
            int i = mid;

            // Go left
            while (i >= 0 &&
                   list.get(i).getFullName().toLowerCase().contains(target)) {
                i--;
            }
            i++;

            // Go right
            while (i < list.size() &&
                   list.get(i).getFullName().toLowerCase().contains(target)) {
                result.add(list.get(i));
                i++;
            }
            break;
        }

        // Standard binary search comparison
        if (midName.compareTo(target) < 0) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }

    return result;
}
}