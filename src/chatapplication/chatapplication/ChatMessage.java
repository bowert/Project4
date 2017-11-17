package chatapplication;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;
    private int type;
    private String msg;
    private String recipient;
    private String sender;
    public ChatMessage(int type, String msg, String recipient, String sender){
        this.type = type;
        this.msg = msg;
        this.recipient = recipient;
        this.sender = sender;
    }
    public String getSender(){return sender;}

    public int getType(){
        return type;
    }

    public String getMessage(){
        return msg;
    }
    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.

    public String getRecipient(){
        return recipient;
    }
}
