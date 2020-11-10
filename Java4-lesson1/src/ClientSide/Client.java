package ClientSide;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public Client() throws HeadlessException, IOException {
        socket = new Socket("localhost", 8189);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        setSize(300, 300);
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JPanel panel2 = new JPanel(new GridLayout(1, 2));
        JButton send = new JButton("Send");
        JButton download = new JButton("Download");
        JTextField text = new JTextField();
        send.addActionListener(a -> {
            String cmd = text.getText();
            sendFile(cmd);
        });
        download.addActionListener(a -> {
            String cmd = text.getText();
            getFile(cmd);
        });
        panel.add(text);
        panel2.add(send);
        panel2.add(download);
        panel.add(panel2);
        add(panel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                sendMessage("exit");
            }
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

    }

    private void getFile(String fileName) {
        try {
            out.writeUTF("download");
            out.writeUTF(fileName);
            String status = in.readUTF();
            while (status.equalsIgnoreCase("sending")){
                File file = new File("Storage/client/" + fileName);
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
                status = in.readUTF();
                System.out.println("File downloaded");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(String filename) {
        try {
            out.writeUTF("upload");
            out.writeUTF(filename);
            File file = new File("Storage/client/" + filename);
            long length = file.length();
            out.writeLong(length);
            FileInputStream fileBytes = new FileInputStream(file);
            int read = 0;
            byte[] buffer = new byte[256];
            while ((read = fileBytes.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
            String status = in.readUTF();
            System.out.println(status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String text) {
        try {
            out.writeUTF(text);
            System.out.println(in.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//after JavaFX create window to choose Login or Registration
    private void Authorization (String login, String password) {
        try {
            out.writeUTF(String.format("/auth %s %s", login, password));
            while (true) {
                String message = in.readUTF();
                if (message.startsWith("/authok")) {
                    System.out.println("Authorized");
                    break;
                }
                else if (message.equalsIgnoreCase("wrongpassword")){
                    System.out.println("Wrong password");
                }
                else if (message.equalsIgnoreCase("wronguser")){
                    System.out.println("Incorrect login");
                    // after JavaFX create selection between login and registration
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Registration(String login, String password){
        try{
            out.writeUTF(String.format("/reg %s %s", login, password));
            while (true){

            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }
}
