package client;

import common.exceptions.ConnectionErrorException;
import common.objects.Request;
import common.objects.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ClientHelper {
    private String serverHost;
    private int serverPort;
    private int maxConnectionAttempts;
    private int connectionTimeout;
    private int connectionAttempt;

    private SocketChannel socketChannel;
    private ByteArrayOutputStream byteOutputStream;
    private ByteArrayInputStream byteInputStream;
    private ObjectOutputStream streamWriter;
    private ObjectInputStream streamReader;

    public ClientHelper(String serverHost, int serverPort, int connectionTimeout, int maxConnectionAttempts ) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.maxConnectionAttempts = maxConnectionAttempts;
        this.connectionTimeout = connectionTimeout;
    }

    public void  start() {
        boolean processingStatus = true;
        try {
            while (processingStatus) {
                try {
                    setConnection();
                    processingStatus = processRequest();
                } catch (ConnectionErrorException exception) {
                    if (connectionAttempt > maxConnectionAttempts) break;
                    try {
                        Thread.sleep(connectionTimeout);
                    } catch (Exception timeoutException) {
                        System.out.print("Connection error");
                    }
                }
                connectionAttempt++;
            }
            if (socketChannel != null) socketChannel.close();
        } catch (IOException exception) {
            System.out.print("Connection error");
        }
    }

    private void setConnection() throws ConnectionErrorException{
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress(serverHost, serverPort));
            streamWriter = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            streamReader = new ObjectInputStream(socketChannel.socket().getInputStream());
        } catch (IOException exception) {
            System.out.print("Connection error");
            throw new ConnectionErrorException();
        }
    }

    private boolean processRequest() {
        Request request = null;;
        Response response; // = null;;
        Scanner scanner = new Scanner(System.in);
        do {
            try {
                request = new Request(scanner.nextLine());
                streamWriter.writeObject(request);
                response = (Response) streamReader.readObject();
                System.out.print(response);
            } catch (InvalidClassException | NotSerializableException exception) {
                System.out.print("InvalidClassException");
            } catch (ClassNotFoundException exception) {
                System.out.print("ClassNotFoundException");
            } catch (IOException exception) {
                System.out.print("Send/receive error");
            }

        } while (!request.getCommandString().equals("bye"));
        return false;
    }
}
