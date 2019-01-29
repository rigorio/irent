package rigor.io.irent;

import org.springframework.stereotype.Service;
import rigor.io.irent.joined.HouseUser;
import rigor.io.irent.joined.HouseUserRepository;
import rigor.io.irent.house.House;
import rigor.io.irent.house.HouseRepository;
import rigor.io.irent.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AggregatorService {

  private HouseRepository houseRepository;
  private UserRepository userRepository;
  private HouseUserRepository houseUserRepository;

  public AggregatorService(HouseRepository houseRepository, UserRepository userRepository, HouseUserRepository houseUserRepository) {
    this.houseRepository = houseRepository;
    this.userRepository = userRepository;
    this.houseUserRepository = houseUserRepository;
  }

  public Map<String, Object> saveUserHouse(User user, List<House> houses) {
    User savedUser = userRepository.save(user);
    List<House> savedHouses = houseRepository.saveAll(houses);
    List<HouseUser> houseUsers = savedHouses.stream()
        .map(house -> new HouseUser(savedUser.getId(), house.getId()))
        .collect(Collectors.toList());
    List<HouseUser> hU = houseUserRepository.saveAll(houseUsers);
    return new HashMap<String, Object>(){{
      put("user", savedHouses);
      put("houses", savedHouses);
      put("joined", houseUsers);
    }};
  }



  public User getByHouseId(Long id) {
    Optional<HouseUser> byHouseId = houseUserRepository.findByHouseId(id);
    return userRepository.findById(byHouseId.get().getUserId()).orElse(new User());
  }



}
