package Facades;

import Domain.Repo.AdminRepository;
import Domain.Users.Admin;
import Presentation.application.View.UtilitiesView.Broadcaster;
import Utilities.Response;
import Utilities.SystemLogger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

public class AdminFacade {

    private final AdminRepository adminRepository;

    public AdminFacade(){
        adminRepository = new AdminRepository();
    }

    public Response<String> suspendUser(String subscriberUsername, Date endOfSuspensionDate) {
        try {
            Admin admin = adminRepository.getAdmin();
            if (!(admin.getSuspensionList().containsKey(subscriberUsername))) {
                admin.suspendUser(subscriberUsername, endOfSuspensionDate);
                SystemLogger.error("[SUCCESS] The User " +subscriberUsername+ " Has Been Suspended");
                return Response.success("The user has been successfully suspended", null);
            }
            else{
                SystemLogger.error("[ERROR] The User is Already Suspended");
                return Response.error("The user is already suspended", null);
            }

        }
        catch (Exception exception){
            return Response.error("Other Exception", null);
        }
    }

    public Response<String> reactivateUser(String subscriberUsername) {
        try {
            Admin admin = adminRepository.getAdmin();
            admin.getSuspensionList().remove(subscriberUsername);
            Broadcaster.broadcast("your suspension has been over",subscriberUsername);
            SystemLogger.error("[SUCCESS] The User " +subscriberUsername+ " Has Been Reactivated");
            return Response.success("The user has been successfully reactivated", null);
        }
        catch (Exception exception){
            return Response.error("Other Exception", null);
        }
    }

    public Map<String, Date> getSuspensionList() {
        Admin admin = adminRepository.getAdmin();
        return admin.getSuspensionList();
    }

    public boolean isSuspended(String subscriberUsername) {
        Admin admin = adminRepository.getAdmin();
        if(!(admin.getSuspensionList().containsKey(subscriberUsername))){
            return false;
        } else return !isDateBeforeOrEqualNow(admin.getSuspensionList().get(subscriberUsername));
    }

    public static boolean isDateBeforeOrEqualNow(Date date) {
        LocalDate currentDate = LocalDate.now();
        LocalDate dateToCheck = convertDateToLocalDate(date);
        return !dateToCheck.isAfter(currentDate);
    }

    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
