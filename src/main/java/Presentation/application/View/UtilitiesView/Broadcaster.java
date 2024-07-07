package Presentation.application.View.UtilitiesView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.function.Consumer;

public class Broadcaster {
    static final Map<String,Consumer<String>> userToLISTENER=Collections.synchronizedMap(new HashMap<>());
    static final Set<Consumer<String>> listeners = Collections.synchronizedSet(new LinkedHashSet<>());

    public static synchronized void broadcast(String message,String user) {
      Consumer<String> listener=userToLISTENER.get(user);
      if(listener!=null) {
          listener.accept(message);
      }

    }

    public static synchronized Registration register(Consumer<String> listener,String user) {
        listeners.add(listener);
        userToLISTENER.put(user, listener);
        return () -> {
            listeners.remove(listener);
            userToLISTENER.remove(user);
        };
    }

    public static Map<String, Consumer<String>> getUserToLISTENER() {
        return userToLISTENER;
    }

    public static Set<Consumer<String>> getListeners() {
        return listeners;
    }
}
