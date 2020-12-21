package com.github.jefesimpson.shop.example.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.github.jefesimpson.shop.example.model.Employee;
import com.github.jefesimpson.shop.example.model.EmployeeDepartment;
import com.github.jefesimpson.shop.example.service.ClientService;
import com.github.jefesimpson.shop.example.service.EmployeeService;
import io.javalin.http.BadRequestResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeDeserializer extends StdDeserializer<Employee> implements ValidationChecker{
    public EmployeeDeserializer(EmployeeService employeeService) {
        super(Employee.class);
        this.employeeService = employeeService;
    }
    private final EmployeeService employeeService;

    @Override
    public Employee deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        String name = root.get(Employee.COLUMN_NAME).asText();
        String surname = root.get(Employee.COLUMN_SURNAME).asText();
        EmployeeDepartment department = EmployeeDepartment.valueOf(root.get(Employee.COLUMN_DEPARTMENT).asText());
        String email = root.get(Employee.COLUMN_EMAIL).asText();
        String phone = root.get(Employee.COLUMN_PHONE).asText();
        String login = root.get(Employee.COLUMN_LOGIN).asText();
        String password = root.get(Employee.COLUMN_PASSWORD).asText();

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        if(emailChecker(email) && phoneChecker(phone)) {
            if (!(root.get(Employee.COLUMN_DATE_EXTERMINATION) instanceof NullNode) && !(root.get(Employee.COLUMN_TOKEN) instanceof NullNode)) {
                LocalDate date = LocalDate.parse(root.get(Employee.COLUMN_DATE_EXTERMINATION).asText());
                String token = root.get(Employee.COLUMN_TOKEN).asText();
                String tokenHash = BCrypt.hashpw(token, BCrypt.gensalt());
                return new Employee(0, name, surname, department, email, phone, login, passwordHash, tokenHash, date);
            } else {
                return new Employee(0, name, surname, department, email, phone, login, passwordHash, null, null);
            }
        } else {
            throw new BadRequestResponse();
        }

    }

    @Override
    public boolean phoneChecker(String phone) {
        if(employeeService.isPhoneUnique(phone)) {
            if(phone.charAt(0) == '8' && phone.charAt(1) == '7' && phone.matches("[0-9]+") && phone.length() == 11) {
                return true;
            }
            else {
                throw new BadRequestResponse("phone isn't valid.");
            }
        }
        else {
            throw new BadRequestResponse("phone isn't unique. Use another phone");
        }
    }

    @Override
    public boolean emailChecker(String email) {
        if(employeeService.isEmailUnique(email)) {
            String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
            Pattern emailPat = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = emailPat.matcher(email);
            return matcher.find();
        }
        else {
            throw new BadRequestResponse("email isn't unique. Use another email");
        }
    }

    @Override
    public ClientService clientService() {
        return null;
    }

    @Override
    public EmployeeService employeeService() {
        return employeeService;
    }
}
