package rigor.io.irent.house;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
public class Review {

  private Long userId;
  private Integer score;
  private String review;

}
