package rigor.io.irent;

import org.springframework.stereotype.Service;
import rigor.io.irent.joined.HouseUserRepository;
import rigor.io.irent.house.HouseRepository;
import rigor.io.irent.reservation.ReservationRepository;
import rigor.io.irent.user.UserRepository;

@Service
public class ReservationService {

  private HouseRepository houseRepository;
  private UserRepository userRepository;
  private HouseUserRepository houseUserRepository;
  private ReservationRepository reservationRepository;

  public ReservationService(HouseRepository houseRepository, UserRepository userRepository, HouseUserRepository houseUserRepository, ReservationRepository reservationRepository) {
    this.houseRepository = houseRepository;
    this.userRepository = userRepository;
    this.houseUserRepository = houseUserRepository;
    this.reservationRepository = reservationRepository;
  }





}
