package rigor.io.irent;

import org.springframework.stereotype.Service;
import rigor.io.irent.house.House;
import rigor.io.irent.joined.HouseUserRepository;
import rigor.io.irent.house.HouseRepository;
import rigor.io.irent.reservation.Reservation;
import rigor.io.irent.reservation.ReservationRepository;
import rigor.io.irent.user.User;
import rigor.io.irent.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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

  public void reserve(Long userId, Long houseId) {

    reservationRepository.save(new Reservation(userId, houseId));
  }

  public List<User> getInquirers(Long houseId) {
    List<Reservation> reservations = reservationRepository.findByHouseId(houseId);
    List<User> allUsers = userRepository.findAll();

    return reservations.stream()
        .map(reservation -> allUsers.stream()
            .filter(user -> user.getId().equals(reservation.getUserId()))
            .findAny()
            .get())
        .collect(Collectors.toList());
  }

  public List<House> getReservations(Long userId) {
    List<Reservation> reservations = reservationRepository.findByUserId(userId);
    List<House> allHouses = houseRepository.findAll();

    return reservations.stream()
        .map(reservation -> allHouses.stream()
            .filter(house -> house.getId().equals(reservation.getHouseId()))
            .findAny()
            .get())
        .collect(Collectors.toList());
  }


}
