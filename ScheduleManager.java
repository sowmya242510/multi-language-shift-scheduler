package com.company.shiftscheduler;


import java.util.*;

class Employee {
    String name;
    String preferredShift;
    int daysWorked;
    Set<String> assignedDays;

    public Employee(String name, String preferredShift) {
        this.name = name;
        this.preferredShift = preferredShift;
        this.daysWorked = 0;
        this.assignedDays = new HashSet<>();
    }

    public boolean canWork(String day) {
        return daysWorked < 5 && !assignedDays.contains(day);
    }

    public void assignDay(String day) {
        assignedDays.add(day);
        daysWorked++;
    }
}

public class ScheduleManager {

    static final String[] DAYS = {
            "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"
    };

    static final String[] SHIFTS = {
            "Morning", "Afternoon", "Evening"
    };

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.print("Enter number of employees (minimum 6 recommended): ");
        int n = scanner.nextInt();
        scanner.nextLine();

        // Validating to prevent infinite loop
        if (n < 6) {
            System.out.println("Error: At least 6 employees are required to meet scheduling requirements.");
            return;
        }

        List<Employee> employees = new ArrayList<>();

        //  Input Section
        for (int i = 0; i < n; i++) {
            System.out.print("Employee Name: ");
            String name = scanner.nextLine();

            System.out.print("Preferred Shift (Morning/Afternoon/Evening): ");
            String shift = scanner.nextLine();

            employees.add(new Employee(name, shift));
        }

        //  Scheduling Data Structure
        Map<String, Map<String, List<Employee>>> schedule = new HashMap<>();

        for (String day : DAYS) {
            schedule.put(day, new HashMap<>());
            for (String shift : SHIFTS) {
                schedule.get(day).put(shift, new ArrayList<>());
            }
        }

        //  Scheduling Logic
        for (String day : DAYS) {

            for (String shift : SHIFTS) {

                List<Employee> assigned = schedule.get(day).get(shift);

                for (Employee emp : employees) {
                    if (emp.preferredShift.equalsIgnoreCase(shift)
                            && emp.canWork(day)
                            && assigned.size() < 2) {

                        assigned.add(emp);
                        emp.assignDay(day);
                    }
                }

                
                int safetyCounter = 0;

                while (assigned.size() < 2 && safetyCounter < 100) {

                    Employee emp = employees.get(random.nextInt(employees.size()));

                    if (emp.canWork(day) && !assigned.contains(emp)) {
                        assigned.add(emp);
                        emp.assignDay(day);
                    }

                    safetyCounter++;
                }

                
                if (assigned.size() < 2) {
                    System.out.println("Warning: Could not fully staff " + shift + " shift on " + day);
                }
            }
        }

       
        System.out.println("\n================ FINAL WEEKLY SCHEDULE ================\n");

        for (String day : DAYS) {
            System.out.println("----------- " + day + " -----------");

            for (String shift : SHIFTS) {
                System.out.print(shift + " : ");

                List<Employee> assigned = schedule.get(day).get(shift);

                if (assigned.isEmpty()) {
                    System.out.print("No employees assigned");
                } else {
                    for (Employee emp : assigned) {
                        System.out.print(emp.name + "  ");
                    }
                }

                System.out.println();
            }

            System.out.println();
        }

        scanner.close();
    }
}
