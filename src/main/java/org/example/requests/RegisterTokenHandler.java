package org.example.requests;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterTokenHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
        String token = bufferedReader.readLine();
        String response = "";

        if (token != null) {
            try {
                PreparedStatement checkIfExistsStatement = Main.connection.prepareStatement("SELECT COUNT(*) FROM user WHERE token = ?;");
                checkIfExistsStatement.setString(1, token);
                ResultSet resultSet = checkIfExistsStatement.executeQuery();

                resultSet.next();
                int rowCount = resultSet.getInt("COUNT(*)");

                if (rowCount == 0) {
                    PreparedStatement preparedStatement = Main.connection.prepareStatement("INSERT INTO user (token) VALUES (?)");
                    preparedStatement.setString(1, token);
                    preparedStatement.executeUpdate();
                }

                response = "Token registered successfully";
            } catch (SQLException e) {
                response = "Registering token failed";
            }
        } else {
            response = "Token is empty";
        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}