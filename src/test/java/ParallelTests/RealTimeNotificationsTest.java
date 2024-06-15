package ParallelTests;
import Presentation.application.View.UtilitiesView.RealTimeNotifications;
import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RealTimeNotificationsTest {

    private UI mockUI;
    private Queue<Message> messageQueue;

    @BeforeEach
    void setUp() {
        mockUI = mock(UI.class);
        messageQueue = new LinkedBlockingQueue<>();
        Notification notificationMock = mock(Notification.class);
        Mockito.mockStatic(Notification.class);
        when(Notification.show(anyString())).thenReturn(notificationMock);
    }

    @Test
    void testRealTimeNotificationsWithTwoThreads() throws InterruptedException {
        // Add messages to the queue in two separate threads
        Thread producer1 = new Thread(() -> messageQueue.add(new NormalMessage("Message from thread 1")));
        Thread producer2 = new Thread(() -> messageQueue.add(new NormalMessage("Message from thread 2")));

        producer1.start();
        producer2.start();

        // Ensure both producers have finished
        producer1.join();
        producer2.join();

        // Start the RealTimeNotifications
        RealTimeNotifications.start(mockUI, messageQueue);

        // Allow some time for the messages to be processed
        Thread.sleep(1000);

        // Verify that the UI.access method was called with the correct type
        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(mockUI, atLeastOnce()).access(commandCaptor.capture());

        // Execute the captured commands and verify that Notification.show was called with the correct messages
        commandCaptor.getAllValues().forEach(Command::execute);

        for (Command command : commandCaptor.getAllValues()) {
            command.execute();
        }

        // Verify that Notification.show was called with the correct messages
        verify(Notification.class, times(2));
        Notification.show("New message: Message from thread 1");

        verify(Notification.class, times(2));
        Notification.show("New message: Message from thread 2");
    }
}
