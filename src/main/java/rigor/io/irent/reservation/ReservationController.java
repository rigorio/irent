package rigor.io.irent.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.irent.ReservationService;
import rigor.io.irent.house.House;
import rigor.io.irent.house.HouseRepository;
import rigor.io.irent.joined.HouseUser;
import rigor.io.irent.joined.HouseUserRepository;
import rigor.io.irent.token.TokenService;
import rigor.io.irent.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ReservationController {

  private ReservationService reservationService;
  private ReservationRepository reservationRepository;
  private HouseUserRepository houseUserRepository;
  private HouseRepository houseRepository;
  private TokenService tokenService;

  public ReservationController(ReservationService reservationService, ReservationRepository reservationRepository, HouseUserRepository houseUserRepository, HouseRepository houseRepository, TokenService tokenService) {
    this.reservationService = reservationService;
    this.reservationRepository = reservationRepository;
    this.houseUserRepository = houseUserRepository;
    this.houseRepository = houseRepository;
    this.tokenService = tokenService;
  }

  @PostMapping("/reservations")
  public ResponseEntity<?> createReservation(@RequestParam(required = false) String token,
                                             @RequestBody Map<String, Object> request) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

    User user = tokenService.fetchUser(token);
    Long houseId = Long.parseLong(String.valueOf(request.get("houseId")));
    Optional<HouseUser> byHouseId = houseUserRepository.findByHouseId(houseId);

    if (!byHouseId.isPresent())
      throw new RuntimeException("House was not found in join column");

    if (byHouseId.get().getUserId().equals(user.getId()))
      return new ResponseEntity<>(createMap("Not allowed", "You are not allowed to " +
          "make a reservation on your own property"), HttpStatus.OK);

    Optional<House> h = houseRepository.findById(houseId);

    if (!h.isPresent())
      throw new RuntimeException("House was not found in house repo");

    House house = h.get();
    Object o = request.get("stay");
    Stay stay = new ObjectMapper().convertValue(o, Stay.class);
    Reservation reservation = reservationService.reserve(user, house, stay);
    System.out.println(reservation);
//    reservationService.reserve(user.getId(), houseId);

    return new ResponseEntity<>(createMap("Created", "Reservation was saved"), HttpStatus.OK);
  }

  @GetMapping("/reservations/users/{houseId}")
  public ResponseEntity<?> getReservationsByHouse(@RequestParam(required = false) String token,
                                                  @PathVariable Long houseId) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

//    List<Reservation> reservations = reservationRepository.findByHouseId(id);
    List<Reservation> inquirers = reservationService.getInquirers(houseId);

    return new ResponseEntity<>(createMap("Success", inquirers), HttpStatus.OK);
  }

  @GetMapping("/reservations/houses")
  public ResponseEntity<?> getReservationsByUser(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

    User user = tokenService.fetchUser(token);

    List<Reservation> reservations = reservationService.getReservations(user.getId());

    return new ResponseEntity<>(createMap("Success", reservations), HttpStatus.OK);
  }

  @DeleteMapping("/reservations/{id}")
  public ResponseEntity<?> deleteReservation(@RequestParam(required = false) String token,
                                             @PathVariable Long id) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

    User user = tokenService.fetchUser(token);

    reservationRepository.deleteById(id); // TODO EHHH ?? nanu?

    return new ResponseEntity<>(createMap("Deleted", "Reservation Cancelled!"), HttpStatus.OK);
  }


  private HashMap<String, Object> createMap(String status, Object message) {
    return new HashMap<String, Object>() {{
      put("status", status);
      put("message", message);
    }};
  }

}
