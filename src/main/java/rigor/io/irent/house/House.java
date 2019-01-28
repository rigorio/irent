package rigor.io.irent.house;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class House {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String[] pics;
  private String title;
  private Long price;
  private String description;
  private Integer slots;
  private Integer vacant;
  private Integer[] reviews;


}
