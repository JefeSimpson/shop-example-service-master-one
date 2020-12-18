package com.github.jefesimpson.shop.example.json.deserializer;

import com.github.jefesimpson.shop.example.model.Employee;
import com.github.jefesimpson.shop.example.service.ClientService;
import com.github.jefesimpson.shop.example.service.EmployeeService;

public interface ValidationChecker {
    boolean phoneChecker(String phone);
    boolean emailChecker(String email);
    ClientService clientService();
    EmployeeService employeeService();
}
