
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    private ChatClient(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
    }

    /*
     * This starts the Chat Client
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create your input and output streams
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {

        // Get proper arguments and override defaults

        // Create your client and start it
        ChatClient client;
        if(args.length == 3) {
            client = new ChatClient(args[2], Integer.parseInt(args[1]), args[0]);
        }
        else if(args.length == 2)
         {
            client = new ChatClient("localhost", Integer.parseInt(args[1]), args[0]);
         }
        else if(args.length == 1) {
            client = new ChatClient("localhost", 1500, args[0]);
        }
        else{
            client = new ChatClient("localhost", 1500, "Anonymous");
        }


            client.start();

        // Send an empty message to the server


        nameofloop:
        while(true) {
            Scanner s = new Scanner(System.in);
            String msg = s.nextLine();

            String[] direct = msg.split(" ");
            String dmes = "";
            ChatMessage cMsg;
            if((msg.toLowerCase()).equals("/logout")){
                cMsg = new ChatMessage(1, (args[0] + " has logged out."), null);
                client.sendMessage(cMsg);
                break nameofloop;
            }
            else if(direct[0].equals("/msg")){
                for(int i = 2; i<direct.length; i++){
                    dmes += direct[i];
                }
                cMsg = new ChatMessage(2, dmes, direct[1]);
            }
            else
            {
                cMsg = new ChatMessage(0, msg, null);
            }
            client.sendMessage(cMsg);
        }
    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            while (true) {
                try {
                    ChatMessage msg = (ChatMessage) sInput.readObject();
                    System.out.print(msg.getMessage());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
