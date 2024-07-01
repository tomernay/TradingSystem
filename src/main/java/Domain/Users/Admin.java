package Domain.Users;

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
        suspensionList.put(subscriberUsername, endOfSuspensionDate);
    }
}