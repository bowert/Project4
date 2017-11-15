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


    private ChatServer(int port) {
        this.port = port;
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
            }catch(IOException e){
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
        if(args.length == 1) {
            server = new ChatServer(Integer.parseInt(args[0]));
        }
        else{
            server = new ChatServer(1500);
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

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            while (true) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                broadcast(username + " : " + cm.getMsg());
                try {
                    if (this.socket.getInputStream().read() != -1){
                        System.out.println("connected NOW");
                    }
                    else
                        System.out.println("not conned");
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // Send message back to the client
            }
        }
        private boolean writeMessage(String msg){
            try {
                if(this.socket.getInputStream().read() != -1){
                    sOutput.writeObject(msg);
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return false;
        }
        private void broadcast(String message){
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date time = new Date();
            System.out.println(message + " " + dateFormat.format(time));
            for(int i = 0; i < clients.size(); i++){
                clients.get(i).writeMessage((message + " " + dateFormat.format(time)));
            }
        }
    }




    private void remove(int id){
        for(int i = 0; i < clients.size(); i++){
                clients.remove(id);
            }
        }



    private void close(){

    }
}
