package rigor.io.irent.joined;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class HouseUser {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Long houseId;
  private Long userId;

  public HouseUser(Long userId, Long houseId) {
    this.houseId = houseId;
    this.userId = userId;
  }
}
