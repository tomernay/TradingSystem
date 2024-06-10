package Presentaion.application.View.UtilitiesView;

import Utilities.Messages.Message;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class RealTimeNotifications {

    private static Queue<Message> messages;

public static void start(UI ui){
    CompletableFuture.runAsync(() -> checkMessageQueue(ui), Executors.newSingleThreadExecutor());

}
    private  static void checkMessageQueue(UI ui) {
        while (true) {
            try {
                // Peek the message from the queue (non-blocking call)
                Message message = messages.poll();

                if (message != null) {
                    // Show a popup notification in the UI thread
                    ui.access(() -> Notification.show("New message: " + message.getMessage()));
                }


            } catch (Exception e) {
                // Handle any exception that occurs during polling or notification
                e.printStackTrace();
            }
        }
    }
}
