package rigor.io.irent.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.irent.ReservationService;
import rigor.io.irent.house.House;
import rigor.io.irent.token.TokenService;
import rigor.io.irent.user.User;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ReservationController {

  private ReservationService reservationService;
  private ReservationRepository reservationRepository;
  private TokenService tokenService;

  public ReservationController(ReservationService reservationService, ReservationRepository reservationRepository, TokenService tokenService) {
    this.reservationService = reservationService;
    this.reservationRepository = reservationRepository;
    this.tokenService = tokenService;
  }

  @PostMapping("/reservations")
  public ResponseEntity<?> createReservation(@RequestParam(required = false) String token,
                                             @RequestBody Long houseId) {
    if (!tokenService.isValid(token))
      return null;

    User user = tokenService.fetchUser(token);
    reservationService.reserve(user.getId(), houseId);

    return new ResponseEntity<>(createMap("Created", "Reservation was saved"), HttpStatus.OK);
  }

  @GetMapping("/reservations/users/{id}")
  public ResponseEntity<?> getUsersByHouse(@RequestParam(required = false) String token,
                                           @PathVariable Long id) {
    if (!tokenService.isValid(token))
      return null;

    List<User> inquirers = reservationService.getInquirers(id);

    return new ResponseEntity<>(createMap("Success", inquirers), HttpStatus.OK);
  }

  @GetMapping("/reservations/houses/{id}")
  public ResponseEntity<?> getHousesByUser(@RequestParam(required = false) String token,
                                           @PathVariable Long id) {
    if (!tokenService.isValid(token))
      return null;

    List<House> reservations = reservationService.getReservations(id);

    return new ResponseEntity<>(createMap("Success", reservations), HttpStatus.OK);
  }

  @DeleteMapping("/reservations/{id}")
  public ResponseEntity<?> deleteReservation(@RequestParam(required = false) String token,
                                             @PathVariable Long id) {
    if (!tokenService.isValid(token))
      return null;

    reservationRepository.deleteById(id);

    return new ResponseEntity<>(createMap("Deleted", "Reservation Cancelled!"), HttpStatus.OK);
  }


  private HashMap<String, Object> createMap(String status, Object message) {
    return new HashMap<String, Object>() {{
      put("status", status);
      put("message", message);
    }};
  }

}
