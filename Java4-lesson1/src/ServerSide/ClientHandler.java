package ServerSide;

import DB.DbAuthService;
import DB.DbConnection;
import DB.User;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private User record;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;


    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())
        ) {
            while (true) {
                String command = in.readUTF();
                if (command.equals("upload")) {
                    try {
                        File file = new File("Storage/server/" + in.readUTF());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        long size = in.readLong();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[256];
                        for (int i = 0; i < (size + 255) / 256; i++) {
                            int read = in.read(buffer);
                            fos.write(buffer, 0, read);
                        }
                        fos.close();
                        sendMessage("OK");
                    } catch (Exception e) {
                        sendMessage("WRONG");
                    }
                }
                if (command.equals("download")) {
                    String filename = in.readUTF();
                    sendMessage("sending");
                    try {
                        File file = new File("Storage/server/" + filename);
                        long length = file.length();
                        out.writeLong(length);
                        FileInputStream fileBytes = new FileInputStream(file);
                        int read = 0;
                        byte[] buffer = new byte[256];
                        while ((read = fileBytes.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        out.flush();
                        sendMessage("sent");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (command.equals("exit")) {
                    System.out.println("Client disconnected correctly");
                    sendMessage("OK");
                    break;
                }
                if (command.equals("files")) {
                    //get list of files from users folder, find on server using users id.
                }
                if (command.equals("createfolder")){
                    //create folder
                }
                if (command.equals("delete")){
                    //delete folder or file
                }
                System.out.println(command);
            }

        } catch (SocketException socketException) {
            System.out.println("Client disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doAuth() throws IOException {
        while (true) {
            System.out.println("Waiting for auth...");
            String message = in.readUTF();
            if (message.startsWith("/auth")) {
                String[] credentials = message.split("\\s");
                User possibleUser = new DbAuthService().findUser(credentials[1], credentials[2]);
                if (possibleUser != null) {
                    if (!server.isOccupied(possibleUser)) {
                        record = possibleUser;
                        sendMessage("/authok " + record.getNickname());
                        server.subscribe(this);
                        break;
                    }

                }
            } else {
                sendMessage(String.format("User no found"));
            }
        }
    }

    public User getRecord() {
        return record;
    }


    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
