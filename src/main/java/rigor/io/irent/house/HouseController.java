package rigor.io.irent.house;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.irent.ReservationService;
import rigor.io.irent.joined.HouseUser;
import rigor.io.irent.joined.HouseUserRepository;
import rigor.io.irent.token.TokenService;
import rigor.io.irent.user.User;
import rigor.io.irent.user.UserRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class HouseController {

  private HouseRepository houseRepository;
  private UserRepository userRepository;
  private HouseUserRepository houseUserRepository;
  private TokenService tokenService;
  private ReservationService reservationService;
  private ObjectMapper objectMapper = new ObjectMapper();

  public HouseController(HouseRepository houseRepository, UserRepository userRepository, HouseUserRepository houseUserRepository, TokenService tokenService, ReservationService reservationService) {
    this.houseRepository = houseRepository;
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
    HouseUser savedHouseUser = houseUserRepository.save(new HouseUser(user.getId(), h.getId()));
    return new ResponseEntity<>(createMap("Success", "Successfully added rental"), HttpStatus.OK);
  }

  @DeleteMapping("/houses/{id}")
  public ResponseEntity<?> deleteHouse(@PathVariable Long id,
                                       @RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

    houseRepository.deleteById(id);
    houseUserRepository.deleteByHouseId(id);
    return new ResponseEntity<>("del", HttpStatus.OK);
  }

  @PutMapping("/houses")
  public ResponseEntity<?> editHouse(@RequestParam(required = false) String token, @RequestBody Map<String, Object> data) throws IOException {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

    House house = objectMapper.readValue(objectMapper.writeValueAsString(data), House.class);
    House h = houseRepository.save(house);
    return new ResponseEntity<>(createMap("Success", h), HttpStatus.OK);
  }




  private HashMap<String, Object> createMap(String status, Object message) {
    return new HashMap<String ,Object>(){{
      put("status", status);
      put("message", message);
    }};
  }


}
