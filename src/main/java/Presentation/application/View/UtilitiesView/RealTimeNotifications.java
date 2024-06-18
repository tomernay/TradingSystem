package Presentation.application.View.UtilitiesView;

import Utilities.Messages.Message;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;

import java.net.URISyntaxException;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class RealTimeNotifications {
    static WSClient wsClient;
    public static void sendMessage(UI ui,String me,String user){
        try {
            wsClient=new WSClient(ui,user);


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            wsClient.sendMessage(user+":"+me);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
