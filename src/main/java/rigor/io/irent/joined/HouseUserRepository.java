package rigor.io.irent.joined;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface HouseUserRepository extends JpaRepository<HouseUser, Long> {
  Optional<HouseUser> findByHouseId(Long houseId);

  List<HouseUser> findByUserId(Long userId);
  @Transactional
  void deleteByHouseId(Long houseId);
}
