package com.github.jefesimpson.shop.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jefesimpson.shop.example.configuration.Constants;
import com.github.jefesimpson.shop.example.configuration.MapperFactory;
import com.github.jefesimpson.shop.example.model.Client;
import com.github.jefesimpson.shop.example.model.Employee;
import com.github.jefesimpson.shop.example.model.ModelPermission;
import com.github.jefesimpson.shop.example.model.Order;
import com.github.jefesimpson.shop.example.service.ClientService;
import com.github.jefesimpson.shop.example.service.EmployeeService;
import com.github.jefesimpson.shop.example.service.Service;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.InternalServerErrorResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class OrderController implements AuthorizationController<Order> {
    private final static Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
    private final Service<Order> orderService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final MapperFactory mapperFactory;

    public OrderController(Service<Order> orderService, ClientService clientService, EmployeeService employeeService, MapperFactory mapperFactory) {
        this.orderService = orderService;
        this.clientService = clientService;
        this.employeeService = employeeService;
        this.mapperFactory = mapperFactory;
    }
//
//    @Override
//    public void create(Context context) {
//        try {
//            Client client = clientSenderOrThrowUnauthorized(context);
//            Employee employee = employeeSenderOrThrowUnauthorized(context);
//            Order target = mapperFactory.objectMapper(ModelPermission.CREATE).readValue(context.body(), Order.class);
//
//            if (client != null) {
//                LOGGER.info(String.format("Sender {%s} started to create order {%s} ", client, target));
//                if(clientService.access(client, target).contains(ModelPermission.CREATE)){
//                    orderService.create(target);
//                    LOGGER.info(String.format("Sender {%s} successfully created order {%s} ", client, target));
//                    context.status(Constants.CREATED_201);
//                }
//                else{
//                    LOGGER.info(String.format("Sender {%s} is not authorized to create order {%s}. Throwing Forbidden", client, target));
//                    throw new ForbiddenResponse();
//                }
//            }
//            else if (employee != null) {
//                LOGGER.info(String.format("Sender {%s} started to create order {%s} ", employee, target));
//                if(employeeService.access(employee, target).contains(ModelPermission.CREATE)){
//                    orderService.create(target);
//                    LOGGER.info(String.format("Sender {%s} successfully created order {%s} ", employee, target));
//                    context.status(Constants.CREATED_201);
//                }
//                else{
//                    LOGGER.info(String.format("Sender {%s} is not authorized to create order {%s}. Throwing Forbidden", employee, target));
//                    throw new ForbiddenResponse();
//                }
//            }
//        } catch (JsonProcessingException | SQLException e) {
//            e.printStackTrace();
//            throw new BadRequestResponse();
//        }
//    }
//
//    @Override
//    public void delete(Context context, int id) {
//        try {
//            Employee employee = employeeSenderOrThrowUnauthorized(context);
//            Order target = orderService.findById(id);
//
//            LOGGER.info(String.format("Sender {%s} started to delete order {%s} ", employee, target));
//            if (employeeService.access(employee, target).contains(ModelPermission.DELETE)){
//                orderService.deleteById(id);
//                LOGGER.info(String.format("Sender {%s} successfully deleted order {%s} ", employee, target));
//                context.status(Constants.NO_CONTENT_204);
//            }
//            else{
//                LOGGER.info(String.format("Sender {%s} is not authorized to delete order {%s}. Throwing Forbidden", employee, target));
//                throw new ForbiddenResponse();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new BadRequestResponse();
//        }
//    }
//
//    @Override
//    public void update(Context context, int id) {
//        try {
//            Employee employee = employeeSenderOrThrowUnauthorized(context);
//            Order target = orderService.findById(id);
//            LOGGER.info(String.format("Sender {%s} started to update order {%s} ", employee, target));
//
//            if (employeeService.access(employee, target).contains(ModelPermission.UPDATE)){
//                Order updated = mapperFactory.objectMapper(ModelPermission.UPDATE).readValue(context.body(), Order.class);
//                updated.setId(id);
//                orderService.update(updated);
//
//                LOGGER.info(String.format("Sender {%s} successfully updated order {%s} ", employee, target));
//                context.result(mapperFactory.objectMapper(ModelPermission.UPDATE).writeValueAsString(updated));
//            }
//            else{
//                LOGGER.info(String.format("Sender {%s} is not authorized to update order {%s}. Throwing Forbidden", employee, target));
//                throw new ForbiddenResponse();
//            }
//        } catch (SQLException | JsonProcessingException e) {
//            e.printStackTrace();
//            throw new BadRequestResponse();
//        }
//    }
//
//    @Override
//    public void getAll(Context context) {
//        Employee employee = employeeSenderOrThrowUnauthorized(context);
//        LOGGER.info(String.format("Sender {%s} started to getAll", employee));
//        try {
//            List<Order> orders = orderService.all()
//                    .stream()
//                    .filter(target -> employeeService.access(employee, target).contains(ModelPermission.READ))
//                    .collect(Collectors.toList());
//            LOGGER.info(String.format("Sender {%s} successfully gotAll", employee));
//            context.result(mapperFactory.objectMapper(ModelPermission.READ).writeValueAsString(orders));
//        } catch (SQLException | JsonProcessingException e) {
//            e.printStackTrace();
//            throw new InternalServerErrorResponse();
//        }
//    }
//
//    @Override
//    public void getOne(Context context, int id) {
//        try {
//            Employee employee = employeeSenderOrThrowUnauthorized(context);
//            Client client = clientSenderOrThrowUnauthorized(context);
//            Order target = orderService.findById(id);
//
//            if (client != null) {
//                LOGGER.info(String.format("Sender {%s} started to getOne order {%s} ", client, target));
//                if(clientService.access(client, target).contains(ModelPermission.READ)){
//                    LOGGER.info(String.format("Sender {%s} successfully gotOne order {%s} ", client, target));
//                    context.result(mapperFactory.objectMapper(ModelPermission.READ).writeValueAsString(target));
//                }
//                else{
//                    LOGGER.info(String.format("Sender {%s} is not authorized to getOne order {%s}. Throwing Forbidden", client, target));
//                    throw new ForbiddenResponse();
//                }
//            }
//            else if (employee != null) {
//                LOGGER.info(String.format("Sender {%s} started to getOne order {%s} ", employee, target));
//                if(employeeService.access(employee, target).contains(ModelPermission.READ)){
//                    LOGGER.info(String.format("Sender {%s} successfully gotOne order {%s} ", employee, target));
//                    context.result(mapperFactory.objectMapper(ModelPermission.READ).writeValueAsString(target));
//                }
//                else{
//                    LOGGER.info(String.format("Sender {%s} is not authorized to getOne order {%s}. Throwing Forbidden", employee, target));
//                    throw new ForbiddenResponse();
//                }
//            }
//        } catch (SQLException | JsonProcessingException e) {
//            e.printStackTrace();
//            throw new InternalServerErrorResponse();
//        }
//    }
//
    @Override
    public ClientService clientService() {
        return clientService;
    }

    @Override
    public EmployeeService employeeService() {
        return employeeService;
    }

    @Override
    public void create(Context context) {

    }

    @Override
    public void delete(Context context, int id) {

    }

    @Override
    public void update(Context context, int id) {

    }

    @Override
    public void getAll(Context context) {

    }

    @Override
    public void getOne(Context context, int id) {

    }
}
