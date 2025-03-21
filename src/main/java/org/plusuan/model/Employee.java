package org.plusuan.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Employee implements Serializable {
    private int id;
    private String name;
    private String position;
    private double salary;
    private LocalDate hireDate;
    private String department;
}
