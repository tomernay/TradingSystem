package Domain.Repo;
import Domain.Users.Subscriber.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<Subscriber, Integer> {
}
