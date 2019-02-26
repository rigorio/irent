package rigor.io.irent.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import rigor.io.irent.house.HouseReview;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
public class ReservationReview {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Long userId;
  private Double score;
  private String review;

  public ReservationReview() {
  }

  public ReservationReview(Long userId, Double score, String review) {
    this.userId = userId;
    this.score = score;
    this.review = review;
  }

  public ReservationReview(HouseReview houseReview) {
    this.userId = houseReview.getUserId();
    this.score = houseReview.getScore();
    this.review = houseReview.getReview();
  }

  public static ReservationReview convert(HouseReview houseReview) {
    return new ReservationReview(houseReview.getUserId(), houseReview.getScore(), houseReview.getReview());
  }

}
