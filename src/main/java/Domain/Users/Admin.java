package Domain.Users;

import Presentation.application.View.UtilitiesView.Broadcaster;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Admin {

    private Map<String, Date> suspensionList;

    public Admin() {
        this.suspensionList = new HashMap<>();
    }

    public Map<String, Date> getSuspensionList() {
        return suspensionList;
    }

    public void suspendUser(String subscriberUsername, Date endOfSuspensionDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        // Format the date using the formatter
        String formattedDate = formatter.format(endOfSuspensionDate);
        Broadcaster.broadcast("your subscription has been suspended until:"+formattedDate,subscriberUsername);
        suspensionList.put(subscriberUsername, endOfSuspensionDate);
    }
}