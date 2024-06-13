package Domain.Repo;


import Domain.Users.Admin;
import Utilities.Response;
import Utilities.SystemLogger;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

public class AdminRepository {
    private final Admin admin;

    public AdminRepository(){
        admin = new Admin();
    }

    public Response<String> suspendUser(String subscriberID, Date endOfSuspensionDate){
        try {
            if (!(admin.getSuspensionList().containsKey(subscriberID))) {
                admin.getSuspensionList().put(subscriberID, endOfSuspensionDate);
                SystemLogger.error("[SUCCESS] The User " +subscriberID+ " Has Been Suspended");
                return new Response<>(true,"The user has been successfully suspended");
            }
            else{
                SystemLogger.error("[ERROR] The User is Already Suspended");
                return new Response<>(false,"The user is already suspended");
            }

        }
        catch (Exception exception){
            return new Response<>(false, "Other Exception");
        }
    }

    public Response<String> reactivateUser(String subscriberID){
        try {
            if (admin.getSuspensionList().containsKey(subscriberID)) {
                admin.getSuspensionList().remove(subscriberID);
            }

            return new Response<>(true,"The user has been successfully reactivated");
        }
        catch (Exception exception){
            return new Response<>(false, "Other Exception");
        }
    }

    public Map<String,Date> getSuspensionList(){
        return admin.getSuspensionList();
    }

    public boolean isSuspended(String user){
        if(!(admin.getSuspensionList().containsKey(user))){
            return false;
        } else if (isDateBeforeOrEqualNow(admin.getSuspensionList().get(user))) {
            return false;
        }
        return true;
    }

    public static boolean isDateBeforeOrEqualNow(Date sqlDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate dateToCheck = sqlDate.toLocalDate();
        return !dateToCheck.isAfter(currentDate);
    }

}
