package rigor.io.irent.house;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

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
  @Nullable
  private String coverPic;
  private String title;

  private String propertyType;

  private String[] amenities;

  private String street;
  private String city;
  private String state;
  private String country;


  private Long price;
  @Column(columnDefinition = "CLOB")
  private String description;
  private Integer slots;
  private Integer[] reviews;


}
