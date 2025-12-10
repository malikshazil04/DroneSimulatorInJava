package Control;

public class CommunicationModule {
private String lastMessage;

public CommunicationModule() {
    this.lastMessage = "";
}

public void sendMessage(String msg) {
    if (msg == null) {
        throw new IllegalArgumentException("message cannot be null");
    }
    this.lastMessage = msg;
}

public String readMessage() {
    return this.lastMessage;
}

public void clear() {
    this.lastMessage = "";
}
    
}
