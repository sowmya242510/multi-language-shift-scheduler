package main

import (
	"fmt"
	"math/rand"
	"time"
)

type Employee struct {
	Name           string
	PreferredShift string
	DaysWorked     int
	AssignedDays   map[string]bool
}

func (e *Employee) CanWork(day string) bool {
	return e.DaysWorked < 5 && !e.AssignedDays[day]
}

func (e *Employee) Assign(day string) {
	e.AssignedDays[day] = true
	e.DaysWorked++
}

func main() {

	rand.Seed(time.Now().UnixNano())

	days := []string{
		"Monday", "Tuesday", "Wednesday",
		"Thursday", "Friday", "Saturday", "Sunday",
	}

	shifts := []string{"Morning", "Afternoon", "Evening"}

	var n int
	fmt.Print("Enter number of employees (minimum 6 recommended): ")
	fmt.Scan(&n)

	
	if n < 6 {
		fmt.Println("Error: At least 6 employees are required.")
		return
	}

	employees := make([]*Employee, n)

	// Input Section
	for i := 0; i < n; i++ {
		var name, shift string
		fmt.Print("Employee Name: ")
		fmt.Scan(&name)
		fmt.Print("Preferred Shift (Morning/Afternoon/Evening): ")
		fmt.Scan(&shift)

		employees[i] = &Employee{
			Name:           name,
			PreferredShift: shift,
			DaysWorked:     0,
			AssignedDays:   make(map[string]bool),
		}
	}

	// Scheduling Data Structure
	schedule := make(map[string]map[string][]*Employee)

	for _, day := range days {
		schedule[day] = make(map[string][]*Employee)
		for _, shift := range shifts {
			schedule[day][shift] = []*Employee{}
		}
	}

	// Scheduling Logic
	for _, day := range days {

		for _, shift := range shifts {

			assigned := schedule[day][shift]

			//  Assigning preferred employees first
			for _, emp := range employees {
				if emp.PreferredShift == shift &&
					emp.CanWork(day) &&
					len(assigned) < 2 {

					assigned = append(assigned, emp)
					emp.Assign(day)
				}
			}

			//  If less than 2, assigning randomly (with safety limit)
			safetyCounter := 0
			for len(assigned) < 2 && safetyCounter < 100 {

				emp := employees[rand.Intn(len(employees))]

				if emp.CanWork(day) && !contains(assigned, emp) {
					assigned = append(assigned, emp)
					emp.Assign(day)
				}

				safetyCounter++
			}

			// Save back to schedule
			schedule[day][shift] = assigned

			//  If still not enough
			if len(assigned) < 2 {
				fmt.Println("Warning: Could not fully staff", shift, "shift on", day)
			}
		}
	}

	// Output Final Schedule
	fmt.Println("\n================ FINAL WEEKLY SCHEDULE ================\n")

	for _, day := range days {
		fmt.Println("-----------", day, "-----------")

		for _, shift := range shifts {
			fmt.Print(shift, ": ")

			assigned := schedule[day][shift]

			if len(assigned) == 0 {
				fmt.Print("No employees assigned")
			} else {
				for _, emp := range assigned {
					fmt.Print(emp.Name, " ")
				}
			}
			fmt.Println()
		}
		fmt.Println()
	}
}

func contains(list []*Employee, emp *Employee) bool {
	for _, e := range list {
		if e == emp {
			return true
		}
	}
	return false
}
