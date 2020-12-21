package com.github.jefesimpson.shop.example.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.github.jefesimpson.shop.example.model.Client;
import com.github.jefesimpson.shop.example.service.ClientService;
import com.github.jefesimpson.shop.example.service.EmployeeService;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.time.LocalDate;

public class ClientPatchDeserializer extends StdDeserializer<Client> implements ValidationChecker {
    private final ClientService clientService;

    public ClientPatchDeserializer(ClientService clientService) {
        super(Client.class);
        this.clientService = clientService;
    }

    @Override
    public Client deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode root = jsonParser.getCodec().readTree(jsonParser);
        String name = root.get(Client.COLUMN_NAME).asText();
        String surname = root.get(Client.COLUMN_SURNAME).asText();
        String email = root.get(Client.COLUMN_EMAIL).asText();
        String phone = root.get(Client.COLUMN_PHONE).asText();
        String login = root.get(Client.COLUMN_LOGIN).asText();
        String password = root.get(Client.COLUMN_PASSWORD).asText();

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        if(!(root.get(Client.COLUMN_DATE_EXTERMINATION) instanceof NullNode)) {
            LocalDate date = LocalDate.parse(root.get(Client.COLUMN_DATE_EXTERMINATION).asText());
            if (!(root.get(Client.COLUMN_TOKEN) instanceof NullNode)) {
                String token = root.get(Client.COLUMN_TOKEN).asText();
                String tokenHash = BCrypt.hashpw(token, BCrypt.gensalt());
                System.out.println("on ne null");
                return new Client(0, name, surname, email, phone, login, passwordHash, tokenHash, date);
            } else {
                return new Client(0, name, surname, email, phone, login, passwordHash, null, date);
            }
        } else {
            return new Client(0, name, surname, email, phone, login, passwordHash, null, null);
        }


    }

    @Override
    public boolean phoneChecker(String phone) {
        return false;
    }

    @Override
    public boolean emailChecker(String email) {
        return false;
    }

    @Override
    public ClientService clientService() {
        return clientService;
    }

    @Override
    public EmployeeService employeeService() {
        return null;
    }
}
