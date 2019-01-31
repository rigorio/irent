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

  public Reservation reserve(Map<String, Object> request) {

    String arrival = String.valueOf(request.get("arrival"));
    String departure = String.valueOf(request.get("departure"));
    Long houseId = Long.parseLong(String.valueOf(request.get("houseId")));
    Long userId = Long.parseLong(String.valueOf(request.get("userId")));
    User user = userRepository.findById(userId).get();
    House house = houseRepository.findById(houseId).get();
    String coverPic = house.getCoverPic();
    String[] pics = house.getPics();
    String title = house.getTitle();
    Long price = house.getPrice();
    String description = house.getDescription();
    Integer slots = house.getSlots();
    Integer[] reviews = house.getReviews();

    Reservation reservation = Reservation.builder()
        .userId(userId)
        .houseId(houseId)

        .arrival(arrival)
        .departure(departure)

        .name(user.getName())
        .contacts(user.getContacts())

        .coverPic(coverPic)
        .pics(pics)
        .title(title)
        .price(price)
        .description(description)
        .slots(slots)
        .reviews(reviews)
        .build();
    return reservationRepository.save(reservation);
  }

  public void reserve(Long userId, Long houseId) {

    reservationRepository.save(new Reservation(userId, houseId));
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
