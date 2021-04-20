package client;

public class ClientMain {

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int MAX_CONNECTION_ATTEMPTS = 3;

    private static boolean chechAbility(String[] params) {
        try {
            if (params.length != 2) throw new IllegalArgumentException();
            int port = Integer.parseInt(params[1]);
            if (port < 1) throw new IllegalArgumentException();
            return true;
        } catch (IllegalArgumentException exception) {
            System.out.print("Illegal arguments. Usage - <host> <port>");
        }
        return false;
    }

    public static void main(String[] args) {
//        if (!chechAbility(args)) return;
//        ClientHelper clientHelper = new ClientHelper(args[0], Integer.parseInt(args[1]), CONNECTION_TIMEOUT, MAX_CONNECTION_ATTEMPTS);
        ClientHelper clientHelper = new ClientHelper("localhost", 38912, CONNECTION_TIMEOUT, MAX_CONNECTION_ATTEMPTS);
        clientHelper.start();
    }

}

