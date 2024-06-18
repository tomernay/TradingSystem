package Presentation.application.View.UtilitiesView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

 public class WSClient extends WebSocketClient {

    private CompletableFuture<String> futureMessage;
    String user;
     private UI ui;
    public WSClient(UI ui,String user) throws URISyntaxException {
        super(new URI("ws://localhost:8080/websocket"));
        this.futureMessage = new CompletableFuture<>();
        connect();
        this.user=user;
        this.ui=ui;
    }



     @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Opened connection");
    }

    @Override
    public void onMessage(String message) {
        futureMessage.complete(message);
        System.out.println("get message");
        String[] m=message.split(":");
        System.out.println(m[1]);
        if(m[1].trim().equals(user.trim())) {

          try {


              ui.access(() -> Notification.show(m[2]));
          }catch (Exception e){}
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed connection");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void sendMessage(String message) throws InterruptedException, ExecutionException {
        futureMessage = new CompletableFuture<>();
        send(message);

        futureMessage.get(); // Wait for the response
    }

    public void setOnMessageListener(OnMessageListener listener) {
        futureMessage.thenAccept(listener::onMessage);
    }

    public interface OnMessageListener {
        void onMessage(String message);
    }
}
