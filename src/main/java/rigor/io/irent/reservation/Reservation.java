package rigor.io.irent.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rigor.io.irent.house.House;
import rigor.io.irent.user.User;

import javax.persistence.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Long userId;
  private Long houseId;

  private String arrival;
  private String departure;


  private String name;
  private String[] contacts;

  private String propertyType;

  private String[] amenities;

  private String street;
  private String city;
  private String state;
  private String country;

  private String coverPic;
  private String title;
  private Long price;
  @Column(columnDefinition = "CLOB")
  private String description;
  private Integer slots;
  private Integer[] reviews;

  public Reservation(User user, House house, Stay stay) {
    userId = user.getId();
    houseId = house.getId();
    arrival = stay.getArrival();
    departure = stay.getDeparture();
    name = user.getName();
    contacts = user.getContacts();
    propertyType = house.getPropertyType();
    amenities = house.getAmenities();
    street = house.getStreet();
    city = house.getCity();
    state = house.getState();
    country = house.getCountry();
    coverPic = house.getCoverPic();
    title = house.getTitle();
    price = house.getPrice();
    description = house.getDescription();
    slots = house.getSlots();
    reviews = house.getReviews();
  }

  public Reservation(Long userId, Long houseId) {
    this.userId = userId;
    this.houseId = houseId;
  }

  public Reservation(String arrival, String departure, Long userId, Long houseId) {
    this.arrival = arrival;
    this.departure = departure;
    this.userId = userId;
    this.houseId = houseId;
  }
}
