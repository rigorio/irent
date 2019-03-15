package rigor.io.irent.house;

import com.fasterxml.jackson.core.type.TypeReference;
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

  public HouseController(HouseRepository houseRepository, ReservationRepository reservationRepository, UserRepository userRepository, HouseUserRepository houseUserRepository, TokenService tokenService, ReservationService reservationService) throws IOException {
    this.houseRepository = houseRepository;
    this.reservationRepository = reservationRepository;
    this.userRepository = userRepository;
    this.houseUserRepository = houseUserRepository;
    this.tokenService = tokenService;
    this.reservationService = reservationService;
    Optional<User> byEmail = this.userRepository.findByEmail("test@test.com");
    User user;
    if (!byEmail.isPresent())
      user = this.userRepository.save(User.builder()
                                          .firstName("Chika")
                                          .lastName("Fujiwara")
                                          .profPic("http://localhost:8080/api/images/bmFuZGVtb25haS5QTkdDaGlrYUZ1aml3YXJh.PNG")
                                          .contacts(new String[]{"09123950244", "test@test.com"})
                                          .verified(true)
                                          .password("test")
                                          .email("test@test.com")
                                          .build());
    else
      user = byEmail.get();
//    List<House> houses = getHouses();
//    houses = this.houseRepository.saveAll(houses);
//    this.houseUserRepository.save(new HouseUser(user.getId(), houses.get(0).getId()));
//    this.houseUserRepository.save(new HouseUser(user.getId(), houses.get(1).getId()));
//
//    Optional<User> byEmail1 = this.userRepository.findByEmail("test");
//    User user1;
//    if (!byEmail1.isPresent())
//      user1 = this.userRepository.save(User.builder()
//                                           .firstName("Rigo")
//                                           .lastName("Sarmiento")
//                                           .profPic("http://localhost:8080/api/images/1.jpg")
//                                           .contacts(new String[]{"09123950244", "wala daw dapat email?"})
//                                           .verified(true)
//                                           .password("test")
//                                           .email("test")
//                                           .build());
//    else
//      user1 = byEmail.get();
//    this.houseUserRepository.save(new HouseUser(user1.getId(), houses.get(2).getId()));


  }

  @GetMapping("/valid")
  public ResponseEntity<?> validate(@RequestParam String token) {
    return new ResponseEntity<>(new ResponseMessage("Success", tokenService.isValid(token)), HttpStatus.OK);
  }

  @GetMapping("/houses")
  public ResponseEntity<?> getAllHouses() {
    return new ResponseEntity<>(createMap("Success", houseRepository.findAll()), HttpStatus.OK);
  }

  @GetMapping("/houses/highest")
  public List<House> getLowest() {
    List<House> houses = houseRepository.findAll().stream()
        .filter(house -> house.getAverage() != null)
        .collect(Collectors.toList());

    List<House> highest = houses.stream()
        .sorted((o2, o1) -> o1.getAverage().compareTo(o2.getAverage()))
        .collect(Collectors.toList());
    return highest;
  }

  @GetMapping("/houses/lowest")
  public List<House> getHighest() {
    List<House> houses = houseRepository.findAll().stream()
        .filter(house -> house.getAverage() != null)
        .collect(Collectors.toList());

    List<House> lowest = houses.stream()
        .sorted(Comparator.comparing(House::getAverage))
        .collect(Collectors.toList());
    return lowest;
  }


  @PostMapping("/houses/filter")
  public ResponseEntity<?> filterHouses(@RequestParam(required = false) String order,
                                        @RequestBody Map<String, Object> filters) throws IOException {
    List<House> houses = houseRepository.findAll();
    System.out.println(houses);
    System.out.println("----------------------------");
    System.out.println(filters);

    if (filters.size() < 1)
      return getAllHouses();

    Object kw = filters.get("keyword");
    String keyword = kw == null ? null : kw.toString();

    Object pt = filters.get("propertyType");
    String propertyType = pt == null ? null : pt.toString();

    Object st = filters.get("sortedRating");
    String sortedRating = st == null ? null : st.toString();

    Object mnp = filters.get("minPrice");
    Integer minPrice = mnp == null || mnp.toString().length() < 1 ? null : Integer.valueOf(mnp.toString());

    Object mxp = filters.get("maxPrice");
    Integer maxPrice = mxp == null || mxp.toString().length() < 1 ? null : Integer.valueOf(mxp.toString());

    Object s = filters.get("slots");
    Integer slots = s == null ? null : Integer.valueOf(s.toString());

    Object r = filters.get("rating");
    Double rating = r == null ? null : Double.valueOf(Integer.valueOf(r.toString()));

    Object a = filters.get("amenities");
    List<String> amenities = a == null ? null : (List<String>) a;

//    List<String> amenities = (List) filters.get("amenities");

    houses = sortedRating != null && sortedRating.equalsIgnoreCase("lowest")
        ? getHighest()
        : sortedRating != null && sortedRating.equalsIgnoreCase("highest")
        ? getLowest()
        : houses;


    if (keyword != null) {
      houses = houses.stream()
          .filter(house ->
                      cs(house.getTitle(), keyword) ||
                          cs(house.getCity(), keyword) ||
                          cs(house.getCountry(), keyword) ||
                          cs(house.getState(), keyword) ||
                          cs(house.getStreet(), keyword)

                 )
          .collect(Collectors.toList());
    }

    System.out.println("keyword");
    System.out.println(houses);

    if (propertyType != null)
      houses = houses.stream()
          .filter(house -> house.getPropertyType().equals(propertyType))
          .collect(Collectors.toList());

    System.out.println("propertyType");
    System.out.println(houses);


    if (slots != null)
      houses = houses.stream()
          .filter(house -> house.getSlots() >= slots)
          .collect(Collectors.toList());

    System.out.println("slots");
    System.out.println(houses);

    if (rating != null)
      houses = houses.stream()
          .filter(house -> {
                    Double average = house.getAverage();
                    return average != null && average >= rating;
                  }
                 )
          .collect(Collectors.toList());


    houses = houses.stream()
        .filter(house -> {
          Integer minP = minPrice;
          if (minP == null)
            minP = 0;
          Integer maxP = maxPrice;
          if (maxP == null)
            maxP = Integer.MAX_VALUE;

          return house.getPrice() >= minP && house.getPrice() <= maxP;
        })
        .collect(Collectors.toList());

    if (amenities != null) {
      for (String amenity : amenities) {
        houses = houses.stream()
            .filter(house -> Arrays.asList(house.getAmenities()).contains(amenity))
            .collect(Collectors.toList());
      }
    }


    return new ResponseEntity<>(new ResponseMessage("Success", houses), HttpStatus.OK);
  }

  private boolean cs(String x, String y) {
    return x.toLowerCase().contains(y.toLowerCase());
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

  public List<House> getHouses() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    List<House> houses = mapper.readValue(bale, new TypeReference<List<House>>() {});
    return houses;
  }

  private String bale = "[\n" +
      "\t{\n" +
      "\t    \"id\": 66,\n" +
      "\t    \"coverPic\": \"http://localhost:8080/api/images/M192YTVDc09DLmpwZWdDaGlrYUZ1aml3YXJh.jpeg\",\n" +
      "\t    \"title\": \"dreamy\",\n" +
      "\t    \"propertyType\": \"Rooms\",\n" +
      "\t    \"amenities\": [],\n" +
      "\t    \"street\": \"street\",\n" +
      "\t    \"city\": \"city\",\n" +
      "\t    \"state\": \"state\",\n" +
      "\t    \"country\": \"country\",\n" +
      "\t    \"price\": 4500,\n" +
      "\t    \"description\": \"desc\",\n" +
      "\t    \"slots\": 4,\n" +
      "\t    \"average\": 3,\n" +
      "\t    \"houseReviews\": []\n" +
      "\t},\n" +
      "\t{\n" +
      "\t    \"id\": 68,\n" +
      "\t    \"coverPic\": \"http://localhost:8080/api/images/bmFuZGVtb25haS5QTkdDaGlrYUZ1aml3YXJh.PNG\",\n" +
      "\t    \"title\": \"moonbae\",\n" +
      "\t    \"propertyType\": \"Rooms\",\n" +
      "\t    \"amenities\": [],\n" +
      "\t    \"street\": \"country\",\n" +
      "\t    \"city\": \"siti\",\n" +
      "\t    \"state\": \"stayt\",\n" +
      "\t    \"country\": \"street\",\n" +
      "\t    \"price\": 3000,\n" +
      "\t    \"description\": \"desc\",\n" +
      "\t    \"slots\": 5,\n" +
      "\t    \"average\": 3.5,\n" +
      "\t    \"houseReviews\": []\n" +
      "\t},\n" +
      "\t{\n" +
      "\t    \"id\": 70,\n" +
      "\t    \"coverPic\": \"http://localhost:8080/api/images/Y2hvaS5QTkdDaGlrYUZ1aml3YXJh.PNG\",\n" +
      "\t    \"title\": \"choi dal dal\",\n" +
      "\t    \"propertyType\": \"Apartment\",\n" +
      "\t    \"amenities\": [],\n" +
      "\t    \"street\": \"stret\",\n" +
      "\t    \"city\": \"kete\",\n" +
      "\t    \"state\": \"stet\",\n" +
      "\t    \"country\": \"kantry\",\n" +
      "\t    \"price\": 500,\n" +
      "\t    \"description\": \"dsec\",\n" +
      "\t    \"slots\": 6,\n" +
      "\t    \"average\": 1,\n" +
      "\t    \"houseReviews\": []\n" +
      "\t}\n" +
      "]";


}
