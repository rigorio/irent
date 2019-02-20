package rigor.io.irent.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stay {

  private String checkIn;
  private String checkOut;

}
