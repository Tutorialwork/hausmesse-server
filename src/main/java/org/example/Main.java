package org.example;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.sun.net.httpserver.HttpServer;
import org.example.requests.ChangeStatusHandler;
import org.example.requests.RegisterTokenHandler;
import org.example.requests.StatusHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static Connection connection;

    public static void main(String[] args) throws IOException {
        SendNotification sendNotification = new SendNotification();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/hausmesse?user=hausmesse&password=root");
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        System.out.println("Starting...........");

        File file = new File("status");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String status = bufferedReader.readLine();

        GPIOManager gpioManager = new GPIOManager();

        if (status.equals("1")) {
            gpioManager.getYellowLed().high();
        } else {
            gpioManager.getYellowLed().low();
        }

        gpioManager.getDoorSwitch().setShutdownOptions(false);
        gpioManager.getDoorSwitch().addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());

                gpioManager.getRedLed().high();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                gpioManager.getRedLed().low();

                try {
                    PreparedStatement checkIfExistsStatement = connection.prepareStatement("SELECT * FROM user;");
                    ResultSet resultSet = checkIfExistsStatement.executeQuery();

                    while (resultSet.next()) {
                        sendNotification.sendMessage(resultSet.getString("token"));
                    }
                } catch (FirebaseMessagingException | SQLException e) {
                    System.out.println("Failed to send push notification");
                    System.out.println(e.getMessage());
                }
            }
        });

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/status", new StatusHandler());
        server.createContext("/changeStatus", new ChangeStatusHandler());
        server.createContext("/registerToken", new RegisterTokenHandler());
        server.setExecutor(null);
        server.start();
    }
}