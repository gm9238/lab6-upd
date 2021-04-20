package server;

import common.exceptions.MakeSocketException;
import common.exceptions.ConnectionErrorException;
import common.exceptions.CloseSocketException;
import common.objects.Request;
import common.objects.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;


public class ServerHelper {
    public static final int CONNECTION_TIMEOUT = 60000;

    private int port;
    private ServerSocket serverSocket;

    public ServerHelper(int port) {
        this.port = port;
    }

    public void start() {
        try {
            makeSocket();
            boolean processingStatus = true;
            while (processingStatus) {
                try (Socket connection = acceptConnection()) {
                    processingStatus = processRequest(connection);
                } catch (ConnectionErrorException | SocketTimeoutException exception) {
                    break;
                } catch (IOException exception) {
                    ServerMain.logger.log(Level.SEVERE,"Произошла ошибка при попытке завершить соединение с клиентом!", exception);
                }
            }
            stop();
        } catch (MakeSocketException exception) {
            ServerMain.logger.log(Level.SEVERE,"Сервер не может быть запущен!", exception);
        }
    }
    private void stop() {
        try {
            ServerMain.logger.info("Завершение работы сервера...");
            if (serverSocket == null) throw new CloseSocketException();
            serverSocket.close();
            ServerMain.logger.fine("Работа сервера успешно завершена.");
        } catch (CloseSocketException exception) {
            ServerMain.logger.log(Level.SEVERE,"Попытка завершения работы незапущенного сервера", exception);
        } catch (IOException exception) {
            ServerMain.logger.log(Level.SEVERE,"Ошибка при завершении работы сервера!", exception);
        }
    }
    private void makeSocket() throws MakeSocketException {
        try {
            ServerMain.logger.info("Запуск сервера...");
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(CONNECTION_TIMEOUT);
            ServerMain.logger.fine("Сервер успешно запущен.");
        } catch (IllegalArgumentException exception) {
            ServerMain.logger.severe("Неверный номер порта " + port + " .");
            throw new MakeSocketException();
        } catch (IOException exception) {
            ServerMain.logger.severe("Порт " + port + " занят.");
            throw new MakeSocketException();
        }
    }
    private Socket acceptConnection() throws ConnectionErrorException, SocketTimeoutException {
        try {
            ServerMain.logger.info("Прослушивание порта '" + port + "'...");
            Socket connection = serverSocket.accept();
            ServerMain.logger.info("Соединение с клиентом успешно установлено.");
            return connection;
        } catch (SocketTimeoutException exception) {
            ServerMain.logger.warning("Превышено время ожидания подключения!");
            throw new SocketTimeoutException();
        } catch (IOException exception) {
            ServerMain.logger.severe("Произошла ошибка при соединении с клиентом!");
            throw new ConnectionErrorException();
        }
    }

    private boolean processRequest(Socket connection) {
        Request request = null;
        Response response = null;

        try (ObjectInputStream streamReader = new ObjectInputStream(connection.getInputStream());
             ObjectOutputStream streamWriter = new ObjectOutputStream(connection.getOutputStream())) {
            do {
                request = (Request) streamReader.readObject();
                response = new Response(request.getCommandString());//requestHandler.handle(request);
                System.out.println("Поступил запрос " + request.getCommandString());
                ServerMain.logger.info("Запрос '" + request.getCommandString() + "' успешно обработан.");
                streamWriter.writeObject(response);
                streamWriter.flush();
            } while (!request.getCommandString().equals("bye"));//(response.getResponseCode() != ResponseCode.SERVER_EXIT);
            return false;
        } catch (ClassNotFoundException exception) {
            ServerMain.logger.severe("ClassNotFoundException");
        } catch (InvalidClassException | NotSerializableException exception) {
            ServerMain.logger.severe("InvalidClassException");
        } catch (IOException exception) {
            if (request == null) {
                ServerMain.logger.warning("Соединение разорвано");
            } else {
                ServerMain.logger.fine("Соединение успешно закрыто");
            }
        }
        return true;
    }

}
