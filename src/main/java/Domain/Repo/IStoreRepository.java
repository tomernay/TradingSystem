package Domain.Repo;
import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface IStoreRepository extends JpaRepository<Store, Integer> {
    @Query("SELECT s FROM Store s WHERE s.storeID = ?1")
    Store findByStoreID(Integer storeID);
}
