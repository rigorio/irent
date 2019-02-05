package rigor.io.irent.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

  private String location;
  private String coverPic;
  private String title;
  private Long price;
  private String description;
  private Integer slots;
  private Integer[] reviews;

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
