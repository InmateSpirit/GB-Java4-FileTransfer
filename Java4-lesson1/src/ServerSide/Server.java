package ServerSide;
import DB.DbConnection;
import DB.User;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private Set<ClientHandler> clientHandlers;
    private ExecutorService executorService;

    public Server() {
        ExecutorService runner = Executors.newFixedThreadPool(4);
        try(ServerSocket server = new ServerSocket(8189)) {

            clientHandlers = new HashSet<>();

            while (true) {
                Socket socket = server.accept();
                System.out.println("Client accepted");
                runner.execute(new ClientHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public synchronized boolean isOccupied(User record) {
        for (ClientHandler ch : clientHandlers) {
            if (ch.getRecord().equals(record)) {
                return true;
            }
        }
        return false;
    }
    public synchronized void subscribe(ClientHandler ch) {
        clientHandlers.add(ch);
    }

    public static void main(String[] args) {
        new Server();
    }
}
