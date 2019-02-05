package rigor.io.irent;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHub {
  private static Map<String, String> response = new HashMap<>();
  private static ResponseEntity<?> unAuthorizedResponse;

  static {
    response.put("status", "Unauthorized");
    response.put("message", "You do not have the correct privileges to access this feature");
    unAuthorizedResponse = new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  public static ResponseEntity<?> defaultUnauthorizedResponse() {
    return unAuthorizedResponse;
  }

  public static ResponseEntity<?> defaultDeleted() {
    return new ResponseEntity<>(
        new HashMap<String, String>() {{
          put("status", "Deleted");
          put("message", "Entity successfully deleted");
        }},
        HttpStatus.ACCEPTED);
  }

  public static <T> ResponseEntity<?> defaultCreated(T t) {
    return new ResponseEntity<>(
        new HashMap<String, Object>() {{
          put("status", "Created");
          put("message", t);
        }},
        HttpStatus.CREATED);
  }

  public static <T> ResponseEntity<?> defaultFound(T t) {
    return new ResponseEntity<>(
        new HashMap<String, Object>() {{
          put("status", "Ok");
          put("message", t);
        }},
        HttpStatus.OK);
  }

  public static ResponseEntity<?> defaultWrongPassword() {
    return new ResponseEntity<>(
        new HashMap<String, String>() {{
          put("status", "Bad Request");
          put("message", "The password you entered was wrong");
        }},
        HttpStatus.BAD_REQUEST);
  }

  public static ResponseEntity<?> defaultBadRequest() {
    return new ResponseEntity<>(
        new HashMap<String, String>() {{
          put("status", "Bad Request");
          put("message", "Please fix your request");
        }},
        HttpStatus.OK);
  }

  public static ResponseEntity<?> badLogin() {
    return new ResponseEntity<>(
        new HashMap<String, String>() {{
          put("status", "Failed!");
          put("message", "Login details were incorrect.");
        }},
        HttpStatus.OK);
  }

  public static ResponseEntity<?> defaultNotAllowed(String message) {
    return new ResponseEntity<>(
        new HashMap<String, String>() {{
          put("status", "Not allowed");
          put("message", message);
        }},
        HttpStatus.FORBIDDEN);
  }

}
