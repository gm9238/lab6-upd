package server;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {
    public static final int PORT = 38912;
    public static final Logger logger = Logger.getLogger(ServerMain.class.getName());

    public static void main(String[] args) {
        ServerHelper server = new ServerHelper(PORT); //requestHandler);
        server.start();
    }
}
