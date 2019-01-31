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
import java.util.Map;

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
                                             @RequestBody Map<String, Object> request) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

    User user = tokenService.fetchUser(token);
    request.put("userId", user.getId());
    Reservation reserve = reservationService.reserve(request);
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

    reservationRepository.deleteById(id); // TODO EHHH ??

    return new ResponseEntity<>(createMap("Deleted", "Reservation Cancelled!"), HttpStatus.OK);
  }


  private HashMap<String, Object> createMap(String status, Object message) {
    return new HashMap<String, Object>() {{
      put("status", status);
      put("message", message);
    }};
  }

}
