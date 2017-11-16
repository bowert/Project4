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

    public ChatMessage(int type, String msg, String recipient){
        this.type = type;
        this.msg = msg;
        this.recipient = recipient;
    }


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
