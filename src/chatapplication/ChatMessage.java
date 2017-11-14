package chatapplication;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;
    private int type;
    private String msg;

    public ChatMessage(int type, String msg){
        this.type = type;
        this.msg = msg;
    }


    public int getType(){
        return type;
    }

    public String getMsg(){
        return msg;
    }
    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.
}
