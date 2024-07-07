package BrodacastTest;

import com.vaadin.flow.shared.Registration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class BroadcasterTest {

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testRegister() {
        // Arrange
        Consumer<String> listener = Mockito.mock(Consumer.class);
        String user = "user1";

        // Act
        Registration registration = Broadcaster.register(listener, user);

        // Assert
        assertEquals(1, Broadcaster.getUserToLISTENER().size());

        assertTrue(Broadcaster.getUserToLISTENER().containsKey(user));
        assertTrue(Broadcaster.getListeners().contains(listener));

        // Unregister
        registration.remove();
        assertEquals(0, Broadcaster.getUserToLISTENER().size());

    }

    @Test
    public void testBroadcastToRegisteredUser() {
        // Arrange
        Consumer<String> listener = Mockito.mock(Consumer.class);
        String user = "user1";
        String message = "Hello, user1!";
        Broadcaster.register(listener, user);

        // Act
        Broadcaster.broadcast(message, user);

        // Assert
        verify(listener, times(1)).accept(message);
    }

    @Test
    public void testBroadcastToUnregisteredUser() {
        // Arrange
        Consumer<String> listener = Mockito.mock(Consumer.class);
        String user = "user1";
        String message = "Hello, user1!";
        
        // Act
        Broadcaster.broadcast(message, user);

        // Assert
        verify(listener, never()).accept(message);
    }
}
