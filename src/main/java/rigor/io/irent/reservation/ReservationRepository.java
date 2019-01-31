package rigor.io.irent.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  List<Reservation> findByHouseId(Long houseId);
  List<Reservation> findByUserId(Long userId);
  void deleteByUserIdAndHouseId(Long userId, Long houseId);

}
