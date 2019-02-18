package rigor.io.irent;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseMessage {
  private String status;
  private Object message;
}
