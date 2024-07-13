package Domain.Repo;
import Domain.Users.Subscriber.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface IUserRepository extends JpaRepository<Subscriber, Integer> {
    @Query("SELECT u FROM Subscriber u WHERE u.username = ?1")
    Subscriber findByUsername(String username);
}
