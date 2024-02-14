package server;

import client.Client;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private final CopyOnWriteArrayList<Client> clients = new CopyOnWriteArrayList<>();
    private final String logPath = "log.txt";
    private volatile boolean work = false;

    public synchronized boolean connectUser(Client client) {
        if (!work) {
            return false;
        }
        clients.add(client);
        client.answerFromServer("Подключен к серверу.");
        return true;
    }

    public synchronized void disconnectUser(Client client) {
        clients.remove(client);
        client.answerFromServer("Отключен от сервера.");
    }

    public synchronized void sendMessage(String message) {
        if (!work) {
            return;
        }
        for (Client client : clients) {
            client.answerFromServer(message);
        }
        writeToLog(message);
    }

    public synchronized String getHistory() {
        try {
            return new String(Files.readAllBytes(Paths.get(logPath)));
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка при чтении истории.";
        }
    }

    public void startServer() {
        work = true;
        writeToLog("Сервер запущен.");
    }

    public void stopServer() {
        work = false;
        for (Client client : clients) {
            disconnectUser(client);
        }
        writeToLog("Сервер остановлен.");
    }

    private void writeToLog(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logPath, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isWorking() {
        return work;
    }
}