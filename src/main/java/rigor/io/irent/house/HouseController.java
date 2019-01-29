package rigor.io.irent.house;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.irent.AggregatorService;
import rigor.io.irent.joined.HouseUser;
import rigor.io.irent.joined.HouseUserRepository;
import rigor.io.irent.token.TokenService;
import rigor.io.irent.user.User;
import rigor.io.irent.user.UserRepository;

import java.io.IOException;
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
  private AggregatorService aggregatorService;
  private ObjectMapper objectMapper = new ObjectMapper();

  public HouseController(HouseRepository houseRepository, UserRepository userRepository, HouseUserRepository houseUserRepository, TokenService tokenService, AggregatorService aggregatorService) {
    this.houseRepository = houseRepository;
    this.userRepository = userRepository;
    this.houseUserRepository = houseUserRepository;
    this.tokenService = tokenService;
    this.aggregatorService = aggregatorService;
  }

  @GetMapping("/houses")
  public ResponseEntity<?> getAllHouses() {
    return new ResponseEntity<>(houseRepository.findAll(), HttpStatus.OK);
  }

  @GetMapping("/houses/{id}")
  public ResponseEntity<?> getHouseById(@PathVariable Long id) {
    return new ResponseEntity<>(houseRepository.findById(id).orElse(new House()), HttpStatus.OK);
  }

//  @GetMapping("/users")

  @GetMapping("/houses/{id}/user")
  public ResponseEntity<?> findUserByHouse(@PathVariable Long id) {
    Optional<HouseUser> byHouseId = houseUserRepository.findByHouseId(id);
    return new ResponseEntity<>(userRepository.findById(byHouseId.get().getUserId()), HttpStatus.OK);
  }

  @GetMapping("/account/houses")
  public ResponseEntity<?> findHousesByUser(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return null;

    User user = tokenService.fetchUser(token);
    List<HouseUser> houseUsers = houseUserRepository.findByUserId(user.getId());

    List<House> allHouses = houseRepository.findAll(); // to avoid multiple queries

    List<House> houses = houseUsers.stream()
        .map(houseUser -> allHouses.stream().filter(house -> house.getId().equals(houseUser.getHouseId())).findAny().orElse(new House()))
        .collect(Collectors.toList());
    return new ResponseEntity<>(houses, HttpStatus.OK);
  }

  @PostMapping("/houses")
  public ResponseEntity<?> addHouse(@RequestParam(required = false) String token, @RequestBody House house) {
    if (!tokenService.isValid(token))
      return null;

    User user = tokenService.fetchUser(token);
    House h = houseRepository.save(house);
    HouseUser savedHouseUser = houseUserRepository.save(new HouseUser(user.getId(), h.getId()));
    return new ResponseEntity<>(h, HttpStatus.OK);
  }

  @DeleteMapping("/houses/{id}")
  public ResponseEntity<?> deleteHouse(@PathVariable Long id,
                                       @RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return null;

    houseRepository.deleteById(id);
    houseUserRepository.deleteByHouseId(id);
    return new ResponseEntity<>("del", HttpStatus.OK);
  }

  @PutMapping("/houses")
  public ResponseEntity<?> editHouse(@RequestBody Map<String, Object> data) throws IOException {
    House house = objectMapper.readValue(objectMapper.writeValueAsString(data), House.class);
    return new ResponseEntity<>(houseRepository.save(house), HttpStatus.OK);
  }


}
