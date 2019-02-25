package rigor.io.irent.house;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.irent.ReservationService;
import rigor.io.irent.ResponseMessage;
import rigor.io.irent.joined.HouseUser;
import rigor.io.irent.joined.HouseUserRepository;
import rigor.io.irent.reservation.Reservation;
import rigor.io.irent.reservation.ReservationRepository;
import rigor.io.irent.token.TokenService;
import rigor.io.irent.user.User;
import rigor.io.irent.user.UserRepository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class HouseController {

  private HouseRepository houseRepository;
  private ReservationRepository reservationRepository;
  private UserRepository userRepository;
  private HouseUserRepository houseUserRepository;
  private TokenService tokenService;
  private ReservationService reservationService;
  private ObjectMapper objectMapper = new ObjectMapper();

  public HouseController(HouseRepository houseRepository, ReservationRepository reservationRepository, UserRepository userRepository, HouseUserRepository houseUserRepository, TokenService tokenService, ReservationService reservationService) {
    this.houseRepository = houseRepository;
    this.reservationRepository = reservationRepository;
    this.userRepository = userRepository;
    this.houseUserRepository = houseUserRepository;
    this.tokenService = tokenService;
    this.reservationService = reservationService;
  }

  @GetMapping("/houses")
  public ResponseEntity<?> getAllHouses() {
    return new ResponseEntity<>(createMap("Success", houseRepository.findAll()), HttpStatus.OK);
  }

  @GetMapping("/houses/{id}")
  public ResponseEntity<?> getHouseById(@PathVariable Long id) {
    House house = houseRepository.findById(id).orElse(new House());
    return new ResponseEntity<>(createMap("Success", house), HttpStatus.OK);
  }

//  @GetMapping("/users")

  @GetMapping("/houses/{id}/user")
  public ResponseEntity<?> findUserByHouse(@PathVariable Long id) {
    Optional<HouseUser> byHouseId = houseUserRepository.findByHouseId(id);
    Optional<User> user = userRepository.findById(byHouseId.get().getUserId());
    return new ResponseEntity<>(createMap("Success", user.get()), HttpStatus.OK);
  }

  @GetMapping("/account/houses")
  public ResponseEntity<?> findHousesByUser(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

    User user = tokenService.fetchUser(token);
    List<HouseUser> houseUsers = houseUserRepository.findByUserId(user.getId());

    List<House> allHouses = houseRepository.findAll(); // to avoid multiple queries

    List<House> houses = houseUsers.stream()
        .map(houseUser -> allHouses.stream().filter(house -> house.getId().equals(houseUser.getHouseId())).findAny().orElse(new House()))
        .collect(Collectors.toList());
    return new ResponseEntity<>(createMap("Success", houses), HttpStatus.OK);
  }

  @PostMapping("/houses")
  public ResponseEntity<?> addHouse(@RequestParam(required = false) String token, @RequestBody House house) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

    User user = tokenService.fetchUser(token);
    House h = houseRepository.save(house);
    System.out.println(h);
    HouseUser savedHouseUser = houseUserRepository.save(new HouseUser(user.getId(), h.getId()));
    return new ResponseEntity<>(createMap("Success", "Successfully added rental"), HttpStatus.OK);
  }

  /**
   * WILL DELETE THE HOUSE ITSELF, DOES NOT JUST CANCEL RESERVATION LOL
   *
   * @param id
   * @param token
   * @return
   */
  @DeleteMapping("/houses/{id}")
  public ResponseEntity<?> deleteHouse(@PathVariable Long id,
                                       @RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Cannot be deleted"), HttpStatus.OK);

    houseRepository.deleteById(id);
    houseUserRepository.deleteByHouseId(id);
    return new ResponseEntity<>(createMap("Deleted", "Item Deleted"), HttpStatus.OK);
  }

  @PutMapping("/houses")
  public ResponseEntity<?> editHouse(@RequestParam(required = false) String token, @RequestBody Map<String, Object> data) throws IOException {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

    House house = objectMapper.readValue(objectMapper.writeValueAsString(data), House.class);
    House h = houseRepository.save(house);
    return new ResponseEntity<>(createMap("Success", "Details were saved"), HttpStatus.OK);
  }

  @PostMapping("/review")
  public ResponseEntity<?> addReview(@RequestBody Map<String, Object> data) {
    Integer score = Integer.valueOf(data.get("score").toString());
    String review = String.valueOf(data.get("review"));
    Review rev = new Review(score, review);
    Long houseId = Long.parseLong(data.get("houseId").toString());
    Long reservationId = Long.parseLong(data.get("reservationId").toString());

    Optional<House> h = houseRepository.findById(houseId);
    if (!h.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "A problem has occured. Please come back later"), HttpStatus.OK);

    Optional<Reservation> r = reservationRepository.findById(reservationId);
    if (!r.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "A problem has occured. Please come back later"), HttpStatus.OK);

    House house = h.get();
    house.addReview(rev);
    Reservation reservation = r.get();
    reservation.addReview(rev);

    houseRepository.save(house);
    reservationRepository.save(reservation);

    return new ResponseEntity<>(new ResponseMessage("Success", "Review was added"), HttpStatus.OK);
  }

  @GetMapping("/amenities")
  public ResponseEntity<?> amenities() {

    return new ResponseEntity<>(new ResponseMessage("Success",
                                                    Arrays.asList("Computer and Internet access",
                                                                  "Swimming pools",
                                                                  "Parking",
                                                                  "Laundry service",
                                                                  "Air-conditioning",
                                                                  "Storage in unit",
                                                                  "Fitness center",
                                                                  "Playground",
                                                                  "Security guard",
                                                                  "Security cameras",
                                                                  "Gated access")
    ), HttpStatus.OK);
  }


  private HashMap<String, Object> createMap(String status, Object message) {
    return new HashMap<String, Object>() {{
      put("status", status);
      put("message", message);
    }};
  }


}
