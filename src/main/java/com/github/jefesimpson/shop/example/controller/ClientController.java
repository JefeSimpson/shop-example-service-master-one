package com.github.jefesimpson.shop.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jefesimpson.shop.example.configuration.Constants;
import com.github.jefesimpson.shop.example.configuration.MapperFactory;
import com.github.jefesimpson.shop.example.model.Client;
import com.github.jefesimpson.shop.example.model.Employee;
import com.github.jefesimpson.shop.example.model.ModelPermission;
import com.github.jefesimpson.shop.example.service.ClientService;
import com.github.jefesimpson.shop.example.service.EmployeeService;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import io.javalin.http.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ClientController implements AuthorizationController<Client> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientController.class);
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final MapperFactory mapperFactory;

    public ClientController(ClientService clientService, EmployeeService employeeService, MapperFactory mapperFactory) {
        this.clientService = clientService;
        this.employeeService = employeeService;
        this.mapperFactory = mapperFactory;
    }

    @Override
    public void create(Context context) {
//        LOGGER.info(String.format("Sender {%s} started to create client {%s} ", employee, target));
//        LOGGER.info(String.format("Sender {%s} successfully created client {%s} ", employee, target));
//        LOGGER.info(String.format("Sender {%s} is not authorized to create client {%s}. Throwing Forbidden", employee, target));
//        LOGGER.info(String.format("Sender {%s} started to create client {%s} ", client, target));
//        LOGGER.info(String.format("Sender {%s} successfully created client {%s} ", client, target));
//        LOGGER.info(String.format("Sender {%s} is not authorized to create client {%s}. Throwing Forbidden", client, target));

        try {
            if (employeeSenderChecker(context)) {
                Employee employee = employeeSender(context);
                Client target = mapperFactory.objectMapper(ModelPermission.CREATE).readValue(context.body(), Client.class);
                if (employeeService.access(employee, target).contains(ModelPermission.CREATE)) {
                    clientService.create(target);
                    context.status(Constants.CREATED_201);
                }
                else {
                    throw new ForbiddenResponse();
                }
            }
            else {
                if(clientSenderChecker(context)) {
                    throw new ForbiddenResponse();
                }
                Client client = clientSender(context);
                Client target = mapperFactory.objectMapper(ModelPermission.CREATE).readValue(context.body(), Client.class);
                if (clientService.access(client, target).contains(ModelPermission.CREATE)) {
                    clientService.create(target);
                    context.status(Constants.CREATED_201);
                }
                else {
                    throw new ForbiddenResponse();
                }
            }

        } catch (JsonProcessingException | SQLException e) {
            e.printStackTrace();
            throw new BadRequestResponse();
        }
    }

    @Override
    public void delete(Context context, int id) {
//        LOGGER.info(String.format("Sender {%s} started to delete client {%s} ", client, target));
//        LOGGER.info(String.format("Sender {%s} successfully deleted client {%s} ", client, target));
//        LOGGER.info(String.format("Sender {%s} is not authorized to delete client {%s}. Throwing Forbidden", client, target));
//        LOGGER.info(String.format("Sender {%s} started to delete client {%s} ", employee, target));
//        LOGGER.info(String.format("Sender {%s} successfully deleted client {%s} ", employee, target));
//        LOGGER.info(String.format("Sender {%s} is not authorized to delete client {%s}. Throwing Forbidden", employee, target));
        try {
            if(employeeSenderChecker(context)) {
                Employee employee = employeeSender(context);
                Client target = clientService.findById(id);
                if (employeeService.access(employee, target).contains(ModelPermission.DELETE)) {
                    clientService.deleteById(id);
                    context.status(Constants.NO_CONTENT_204);
                }
                else {
                    throw new ForbiddenResponse();
                }
            }
            else {
                if(clientSenderChecker(context)) {
                    Client client = clientSender(context);
                    Client target = clientService.findById(id);
                    if (clientService.access(client, target).contains(ModelPermission.DELETE)) {
                        clientService.deleteById(id);
                        context.status(Constants.NO_CONTENT_204);
                    }
                    else {
                        throw new ForbiddenResponse();
                    }
                }
                else {
                    throw new UnauthorizedResponse();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BadRequestResponse();
        }
    }

    @Override
    public void update(Context context, int id) {
//        LOGGER.info(String.format("Sender {%s} started to update client {%s} ", client, target));
//        LOGGER.info(String.format("Sender {%s} successfully updated client {%s} ", client, target));
//        LOGGER.info(String.format("Sender {%s} is not authorized to update client {%s}. Throwing Forbidden", client, target));
//        LOGGER.info(String.format("Sender {%s} started to update client {%s} ", employee, target));
//        LOGGER.info(String.format("Sender {%s} successfully updated client {%s} ", employee, target));
//        LOGGER.info(String.format("Sender {%s} is not authorized to update client {%s}. Throwing Forbidden", employee, target));
        try {
            if(employeeSenderChecker(context)) {
                Employee employee = employeeSender(context);
                Client target = clientService.findById(id);
                if (employeeService.access(employee, target).contains(ModelPermission.UPDATE)) {
                    context.result(mapperFactory.objectMapper(ModelPermission.UPDATE).writeValueAsString(target));

                    Client updated = mapperFactory.objectMapper(ModelPermission.UPDATE).readValue(context.body(), Client.class);
                    updated.setId(id);
                    clientService.update(updated);
                    context.result(mapperFactory.objectMapper(ModelPermission.UPDATE).writeValueAsString(updated));
                }
                else {
                    throw new ForbiddenResponse();
                }
            }
            else {
                if(clientSenderChecker(context)) {
                    Client client = clientSender(context);
                    Client target = clientService.findById(id);
                    if(clientService.access(client, target).contains(ModelPermission.UPDATE)) {
                        context.result(mapperFactory.objectMapper(ModelPermission.UPDATE).writeValueAsString(target));

                        Client updated = mapperFactory.objectMapper(ModelPermission.UPDATE).readValue(context.body(), Client.class);
                        updated.setId(id);
                        clientService.update(updated);
                        context.result(mapperFactory.objectMapper(ModelPermission.UPDATE).writeValueAsString(updated));
                    }
                    else {
                        throw new ForbiddenResponse();
                    }
                }
                else {
                    throw new UnauthorizedResponse();
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new BadRequestResponse();
        }
    }

    @Override
    public void getAll(Context context) {
        try {
            if(employeeSenderChecker(context)) {
                Employee employee = employeeSender(context);
                LOGGER.info(String.format("Sender {%s} started to getAll", employee));

                List<Client> clients = clientService.all()
                        .stream()
                        .filter(target -> employeeService.access(employee, target).contains(ModelPermission.READ))
                        .collect(Collectors.toList());
                LOGGER.info(String.format("Sender {%s} successfully gotAll", employee));
                context.result(mapperFactory.objectMapper(ModelPermission.READ).writeValueAsString(clients));
            }
            else {
                throw new UnauthorizedResponse();
            }
        }catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new InternalServerErrorResponse();
        }
    }

    @Override
    public void getOne(Context context, int id) {
        try {
            if(employeeSenderChecker(context)) {
                Employee employee = employeeSender(context);
                Client target = clientService.findById(id);
                LOGGER.info(String.format("Sender {%s} started to getOne client {%s} ", employee, target));
                if (employeeService.access(employee, target).contains(ModelPermission.READ)) {
                    LOGGER.info(String.format("Sender {%s} successfully gotOne client {%s} ", employee, target));
                    context.result(mapperFactory.objectMapper(ModelPermission.READ).writeValueAsString(target));
                }
                else {
                    LOGGER.info(String.format("Sender {%s} is not authorized to getOne client {%s}. Throwing Forbidden", employee, target));
                    throw new ForbiddenResponse();
                }
            }
            else {
                if(clientSenderChecker(context)) {
                    Client client = clientSender(context);
                    Client target = clientService.findById(id);
                    LOGGER.info(String.format("Sender {%s} started to getOne client {%s} ", client, target));
                    if (clientService.access(client, target).contains(ModelPermission.READ)) {
                        LOGGER.info(String.format("Sender {%s} successfully gotOne client {%s} ", client, target));
                        context.result(mapperFactory.objectMapper(ModelPermission.READ).writeValueAsString(target));
                    }
                    else {
                        LOGGER.info(String.format("Sender {%s} is not authorized to getOne client {%s}. Throwing Forbidden", client, target));
                        throw new ForbiddenResponse();
                    }
                }
                else {
                    throw new UnauthorizedResponse();
                }
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            throw new InternalServerErrorResponse();
        }
    }

    @Override
    public ClientService clientService() {
        return clientService;
    }

    @Override
    public EmployeeService employeeService() {
        return employeeService;
    }
}
