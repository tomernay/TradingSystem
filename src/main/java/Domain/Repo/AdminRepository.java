package Domain.Repo;


import Domain.Users.Admin;
import org.springframework.stereotype.Repository;

@Repository
public class AdminRepository {
    private final Admin admin;

    public AdminRepository(){
        admin = new Admin();
    }

    public Admin getAdmin() {
        return admin;
    }

}
