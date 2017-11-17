package chatapplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;
    private final String filterName;


    private ChatServer(int port, String filterName) {
        this.port = port;
        this.filterName = filterName;
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            Runnable r = new ClientThread(socket, uniqueId++);
            Thread t = new Thread(r);
            clients.add((ClientThread) r);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        ChatServer server;
        if (args.length == 1) {
            server = new ChatServer(Integer.parseInt(args[0]), "badwords.txt");
        } else {
            server = new ChatServer(1500, "badwords.txt");
        }
        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;
        int userNum = 0;

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                String tempName = (String) sInput.readObject();
                for(int i = 0; i < clients.size(); i++){
                    if(tempName.equals(clients.get(i).username)){
                        return;
                    }
                }
                username = tempName;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            System.out.println("using run method");
            // Read the username sent to you by client
            while (true) {
                System.out.println("Past while(true)");

                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(cm.getType() == 0) {
                    broadcast(username + " : " + cm.getMessage(),cm.getRecipient(), cm.getSender());
                }
                else if(cm.getType() == 1){
                    broadcast(cm.getMessage(),cm.getRecipient(), cm.getSender());
                }
                else{
                    broadcast(cm.getMessage(), cm.getRecipient(), cm.getSender());
                }

                if(cm.getType() == 1) {
                    for(int i =0; i < clients.size(); i++) {
                        if (username.equals(clients.get(i).username)) {
                            try {
                                remove(i);
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                // Send message back to the client
            }
        }

        private boolean writeMessage(String msg, String recipient, String sender) {
            try {
                if (socket.isConnected()) {
                    sOutput.writeObject(new ChatMessage(0, msg, recipient, sender ));
                }
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }


            return false;
        }

        private synchronized void broadcast(String message, String recipient, String sender) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date time = new Date();
            if(recipient == null) {
                System.out.println(message + " " + dateFormat.format(time));
                //System.out.print(clients.size());
                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).writeMessage((message + " " + dateFormat.format(time)) + "\n", recipient, sender);
                }
            }
            else{
                System.out.println("/msg");
                for(int i = 0; i<clients.size();i++){
                    if(recipient.equals(clients.get(i).username)){
                        writeMessage(message, recipient, sender);
                    }
                }
            }
        }


        private void remove(int id) throws IOException {

       /*     clients.get(id).sInput.close();
            clients.get(id).sOutput.close();
            clients.get(id).socket.close();
       */     clients.remove(id);
        }


        private void close() {

        }

    }
}
