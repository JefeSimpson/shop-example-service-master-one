package com.github.jefesimpson.shop.example;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.jefesimpson.shop.example.configuration.ModelPermissionMapperFactory;
import com.github.jefesimpson.shop.example.controller.*;
import com.github.jefesimpson.shop.example.json.deserializer.*;
import com.github.jefesimpson.shop.example.json.serializer.ClientSerializer;
import com.github.jefesimpson.shop.example.json.serializer.EmployeeSerializer;
import com.github.jefesimpson.shop.example.json.serializer.OrderSerializer;
import com.github.jefesimpson.shop.example.json.serializer.ProductSerializer;
import com.github.jefesimpson.shop.example.model.*;
import com.github.jefesimpson.shop.example.service.*;
import io.javalin.Javalin;

import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.defaultContentType = "application/json";
        });
        BasicClientService basicClientService = new BasicClientService();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Client.class, new ClientPatchDeserializer(basicClientService));
        SimpleModule module = new SimpleModule();
        Map<ModelPermission, Module> permission = new HashMap<>();
        Map<ModelPermission, Module> patchPermission = new HashMap<>();
        patchPermission.put(ModelPermission.UPDATE, simpleModule);
        permission.put(ModelPermission.CREATE, module);
        permission.put(ModelPermission.READ, module);
        permission.put(ModelPermission.UPDATE, module);
        ModelPermissionMapperFactory modelPermissionMapper = new ModelPermissionMapperFactory(permission, patchPermission);
        Service<Order> orderService = new OrderService();
        Service<Product> productService = new ProductService();
        BasicEmployeeService basicEmployeeService = new BasicEmployeeService();
        Controller<Client> clientController = new ClientController(basicClientService, basicEmployeeService, modelPermissionMapper);
        Controller<Employee> employeeController = new  EmployeeController(basicEmployeeService, basicClientService, modelPermissionMapper);
        Controller<Order> orderController = new OrderController(orderService, basicClientService, basicEmployeeService, modelPermissionMapper);
        Controller<Product> productController = new ProductController(productService, basicClientService, basicEmployeeService, modelPermissionMapper);
        module.addDeserializer(Client.class, new ClientDeserializer(basicClientService))
                .addDeserializer(Employee.class, new EmployeeDeserializer(basicEmployeeService))
                .addDeserializer(Order.class, new OrderDeserializer())
                .addDeserializer(Product.class, new ProductDeserializer());
        module.addSerializer(Client.class, new ClientSerializer())
                .addSerializer(Employee.class, new EmployeeSerializer())
                .addSerializer(Order.class, new OrderSerializer())
                .addSerializer(Product.class, new ProductSerializer());






        app.routes(() -> {
            path("clients", () -> {
                get(clientController::getAll);
                post(clientController::create);
                path(id, () -> {
                    get(ctx -> clientController.getOne(ctx,ctx.pathParam(id, Integer.class).get()));
                    patch(ctx -> clientController.update(ctx, ctx.pathParam(id, Integer.class).get()));
                    delete(ctx -> clientController.delete(ctx, ctx.pathParam(id, Integer.class).get()));
                });
            });
            path("employees", () -> {
                get(employeeController::getAll);
                post(employeeController::create);
                path(id, () -> {
                    get(ctx -> employeeController.getOne(ctx, ctx.pathParam(id, Integer.class).get()));
                    patch(ctx -> employeeController.update(ctx, ctx.pathParam(id, Integer.class).get()));
                    delete(ctx -> employeeController.delete(ctx, ctx.pathParam(id, Integer.class).get()));
                });
            });
            path("orders", () -> {
                get(orderController::getAll);
                post(orderController::create);
                path(id, () -> {
                    get(ctx -> orderController.getOne(ctx, ctx.pathParam(id, Integer.class).get()));
                    patch(ctx -> orderController.update(ctx, ctx.pathParam(id, Integer.class).get()));
                    delete(ctx -> orderController.delete(ctx, ctx.pathParam(id, Integer.class).get()));
                });
            });
            path("products", () -> {
                get(productController::getAll);
                post(productController::create);
                path(id, () -> {
                    get(ctx -> productController.getOne(ctx, ctx.pathParam(id, Integer.class).get()));
                    patch(ctx -> productController.update(ctx, ctx.pathParam(id, Integer.class).get()));
                    delete(ctx -> productController.delete(ctx, ctx.pathParam(id, Integer.class).get()));
                });
            });
        });



        app.start(9950);
    }

    static String id = ":id";
}
