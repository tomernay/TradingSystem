package ParallelTests;

import Presentation.application.View.UtilitiesView.MyWebSocketHandler;
import Presentation.application.View.UtilitiesView.WSClient;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.Command;
import org.java_websocket.handshake.HandshakeImpl1Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.net.URISyntaxException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class RealTimeNotificationsTest {



    private WSClient wsClient;
    private UI mockUI;

    @Autowired
    private MyWebSocketHandler myWebSocketHandler;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() throws URISyntaxException {
        mockUI = Mockito.mock(UI.class);
        UI.setCurrent(mockUI);

        // Initialize and connect the WSClient
        wsClient = new WSClient(UI.getCurrent(), "user");
        wsClient.onOpen(new HandshakeImpl1Server());
        wsClient.setOnMessageListener(message -> {
            Notification.show(message); // Simulate UI update
        });
    }

    @Test
    public void testRealTimeNotificationsWithTwoThreads() throws InterruptedException {
        // Mock UI.access to execute the command immediately
        doAnswer(invocation -> {
            Command command = invocation.getArgument(0);
            command.execute();
            return null;
        }).when(mockUI).access(any(Command.class));

        // Spy on the Notification class to verify calls to show method
        Notification notificationSpy = Mockito.spy(Notification.class);
        doAnswer(invocation -> {
            notificationSpy.open(); // Simulate open call
            return null;
        }).when(notificationSpy).open();

        // Simulate WebSocket message receiving in two separate threads
        Thread producer1 = new Thread(() -> wsClient.onMessage("message:user:Message from thread 1"));
        Thread producer2 = new Thread(() -> wsClient.onMessage("message:user1:Message from thread 2"));

        producer1.start();
        producer2.start();

        // Ensure both producers have finished
        producer1.join();
        producer2.join();

        // Allow some time for the messages to be processed
        Thread.sleep(1000);

        // Verify that the UI.access method was called with the correct commands
        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        verify(mockUI, times(1)).access(commandCaptor.capture());

        // Execute the captured commands to simulate UI updates
        for (Command command : commandCaptor.getAllValues()) {
            command.execute();
        }

        // Verify that Notification.show was called with the correct messages
        verify(notificationSpy, times(1)).show("Message from thread 1");

    }

    @EnableWebSocket
    @Configuration
    @Import(RealTimeNotificationsTest.TestWebSocketConfig.class)
    static class TestWebSocketConfig implements WebSocketConfigurer {

        @Autowired
        private MyWebSocketHandler myWebSocketHandler;

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry.addHandler(myWebSocketHandler, "/websocket").setAllowedOrigins("*");
        }

        @Bean
        public MyWebSocketHandler myWebSocketHandler() {
            return new MyWebSocketHandler();
        }

        @Bean
        public ServletServerContainerFactoryBean createWebSocketContainer() {
            return new ServletServerContainerFactoryBean();
        }
    }
}
