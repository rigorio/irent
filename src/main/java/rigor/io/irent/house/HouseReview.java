package rigor.io.irent.house;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
public class HouseReview {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Long userId;
  private Double score;
  private String review;

  public HouseReview() {
  }

  public HouseReview(Long userId, Double score, String review) {
    this.userId = userId;
    this.score = score;
    this.review = review;
  }
}
