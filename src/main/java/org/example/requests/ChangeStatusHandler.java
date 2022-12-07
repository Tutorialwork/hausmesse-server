package org.example.requests;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.GPIOManager;
import org.example.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ChangeStatusHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
            String newStatus = bufferedReader.readLine();

            GPIOManager gpioManager = Main.gpioManagerInstance;
            if (newStatus.equals("1")) {
                gpioManager.getYellowLed().high();
            } else {
                gpioManager.getYellowLed().low();
            }

            if (newStatus == null) {
                String response = "Body is empty";
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            FileOutputStream fileOutputStream = new FileOutputStream(new File("status"));
            fileOutputStream.write(newStatus.getBytes());
            fileOutputStream.close();

            String response = "Successfully changed status";
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (Exception exception) {
            System.out.println("Failed to update alarm status");
            exception.printStackTrace();
        }
    }
}