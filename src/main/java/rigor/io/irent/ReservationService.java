package rigor.io.irent;

import org.springframework.stereotype.Service;
import rigor.io.irent.house.House;
import rigor.io.irent.house.HouseRepository;
import rigor.io.irent.joined.HouseUserRepository;
import rigor.io.irent.reservation.Reservation;
import rigor.io.irent.reservation.ReservationRepository;
import rigor.io.irent.reservation.Stay;
import rigor.io.irent.user.User;
import rigor.io.irent.user.UserRepository;

import java.util.List;
import java.util.Map;

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

  public Reservation reserve(User user, House house, Stay stay) {
    Reservation reservation = new Reservation(user, house, stay);
    return reservationRepository.save(reservation);
  }

  public List<Reservation> getInquirers(Long houseId) {
    List<Reservation> reservations = reservationRepository.findByHouseId(houseId);
    return reservations;
  }

  public List<Reservation> getReservations(Long userId) {
    List<Reservation> reservations = reservationRepository.findByUserId(userId);
    return reservations;
//    List<House> allHouses = houseRepository.findAll();
//
//    return reservations.stream()
//        .map(reservation -> allHouses.stream()
//            .filter(house -> house.getId().equals(reservation.getHouseId()))
//            .findAny()
//            .get())
//        .collect(Collectors.toList());
  }


}
