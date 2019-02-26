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
import rigor.io.irent.reservation.ReservationReview;
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
    User us = this.userRepository.save(User.builder()
                                           .name("rigorei")
                                           .contacts(new String[]{"09123950244", "rigorei.sarmiento@gmail.com"})
                                           .verified(true)
                                           .password("test")
                                           .email("rigorei.sarmiento@gmail.com")
                                           .build());
    this.userRepository.save(User.builder()
                                 .name("rigosarmiento")
                                 .contacts(new String[]{"09123950244", "rigosarmiento4@gmial.com"})
                                 .verified(true)
                                 .password("test")
                                 .email("rigosarmiento4@gmail.com")
                                 .build());
    User test = this.userRepository.save(User.builder()
                                             .name("regosarmiento")
                                             .contacts(new String[]{"091sdfa23950244", "rigosarasfdmiento4@gmial.com"})
                                             .verified(true)
                                             .password("test")
                                             .email("test@test.com")
                                             .build());
    House save = this.houseRepository.save(House.builder()
                                               .coverPic("http://localhost:8080/api/images/MF9Pek5xa2Y3LmpwZWdyaWdvcmVp.jpeg")
                                               .title("Dreamy")
                                               .propertyType("House")
                                               .amenities(new String[]{"Air Conditioning"})
                                               .street("123 Street")
                                               .city("City of stars")
                                               .state("State of Depression")
                                               .average(3.0)
                                               .country("Country road")
                                               .price(4000L)
                                               .description("bishu bashi bishu bashi")
                                               .houseReviews(new ArrayList<>())
                                               .build());
    House save2 = this.houseRepository.save(House.builder()
                                                .coverPic("http://localhost:8080/api/images/1.jpg")
                                                .title("Syke")
                                                .propertyType("Apartment")
                                                .amenities(new String[]{"Air-Conditioning"})
                                                .street("123 Spotify")
                                                .city("City ")
                                                .state("Depression")
                                                .average(2.0)
                                                .country("Country road")
                                                .price(4000L)
                                                .description("bishu bashi bishu bashi")
                                                .houseReviews(new ArrayList<>())
                                                .build());
//    HouseReview review1 = new HouseReview(save.getId(), 3, "");
//    HouseReview review2 = new HouseReview(save.getId(), 3, "");
    this.houseUserRepository.save(new HouseUser(test.getId(), save.getId()));
    this.houseUserRepository.save(new HouseUser(test.getId(), save2.getId()));
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
  @SuppressWarnings("all")
  public ResponseEntity<?> editHouse(@RequestParam(required = false) String token, @RequestBody Map<String, Object> data) throws IOException {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Error occured for " + token), HttpStatus.OK);

    House house = objectMapper.readValue(objectMapper.writeValueAsString(data), House.class);
    Long id = house.getId();
    House realHouse = houseRepository.findById(id).orElse(house);
    realHouse.setCoverPic(house.getCoverPic());
    realHouse.setTitle(house.getTitle());
    realHouse.setPropertyType(house.getPropertyType());
    realHouse.setStreet(house.getStreet());
    realHouse.setCity(house.getCity());
    realHouse.setState(house.getState());
    realHouse.setCountry(house.getCountry());
    realHouse.setPrice(house.getPrice());
    realHouse.setDescription(house.getDescription());
    realHouse.setSlots(house.getSlots());
    realHouse.setAmenities(house.getAmenities());
    houseRepository.save(realHouse);
    List<Reservation> reservations = reservationRepository.findByHouseId(realHouse.getId());
    for (Reservation reservation : reservations) {
      reservation.setCoverPic(house.getCoverPic());
      reservation.setTitle(house.getTitle());
      reservation.setPropertyType(house.getPropertyType());
      reservation.setStreet(house.getStreet());
      reservation.setCity(house.getCity());
      reservation.setState(house.getState());
      reservation.setCountry(house.getCountry());
      reservation.setPrice(house.getPrice());
      reservation.setDescription(house.getDescription());
      reservation.setSlots(house.getSlots());
      reservation.setAmenities(house.getAmenities());
    }
    reservationRepository.saveAll(reservations);

    return new ResponseEntity<>(createMap("Success", "Details were saved"), HttpStatus.OK);
  }

  @GetMapping("/reviews/{houseId}")
  public ResponseEntity<?> viewReviews(@PathVariable Long houseId) {
    Optional<House> byId = houseRepository.findById(houseId);
    if (!byId.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "Please try again later"), HttpStatus.OK);

    House house = byId.get();

    List<HouseReview> reviews = house.getHouseReviews();
    return new ResponseEntity<>(new ResponseMessage("Success", reviews), HttpStatus.OK);
  }

  @PostMapping("/house/review")
  public ResponseEntity<?> addReview(@RequestParam String token,
                                     @RequestBody Map<String, Object> data) {
    Double score = Double.valueOf(data.get("score").toString());
    String review = String.valueOf(data.get("review"));
    User user = tokenService.fetchUser(token);
    Long userId = user.getId();
    HouseReview rev = new HouseReview(userId, score, review);
    Long houseId = Long.parseLong(data.get("houseId").toString());
    Long reservationId = Long.parseLong(data.get("reservationId").toString());

    Optional<House> h = houseRepository.findById(houseId);
    if (!h.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "A problem has occured. Please come back later"), HttpStatus.OK);

    Optional<Reservation> r = reservationRepository.findById(reservationId);
    if (!r.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "A problem has occured. Please come back later"), HttpStatus.OK);

    House house = h.get();

    List<HouseReview> houseReviews = house.getHouseReviews();
    for (HouseReview houseReview1 : houseReviews) {
      if (houseReview1.getUserId().equals(userId))
        return new ResponseEntity<>(new ResponseMessage("Failed", "You've already added a review!"), HttpStatus.OK);
    }

    house.addReview(rev);
    double average = devoid(house);
    System.out.println("Average is " + average);
    house.setAverage(average);


    Reservation reservation = r.get();
    reservation.addReview(ReservationReview.convert(rev));
    reservation.setAverage(average);

    houseRepository.save(house);
    Reservation dang = reservationRepository.save(reservation);
    System.out.println("dang");
    System.out.println(dang);

    return new ResponseEntity<>(new ResponseMessage("Success", "HouseReview was added"), HttpStatus.OK);
  }


  private double devoid(House house) {
    List<Double> scores = house.getHouseReviews().stream()
        .map(HouseReview::getScore)
        .collect(Collectors.toList());
    System.out.println("scores");
    System.out.println(scores);

    Double sum = 0.0;
    for (Double score : scores)
      sum += score;

    System.out.println("sum " + sum);

    Double size = (double) scores.size();
    if (size > sum || size == 0)
      return 0;
    return sum / size;
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
