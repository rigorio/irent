package rigor.io.irent.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.irent.token.TokenService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

  private TokenService tokenService;
  private UserRepository userRepository;

  public UserController(TokenService tokenService, UserRepository userRepository) {
    this.tokenService = tokenService;
    this.userRepository = userRepository;
  }

  @GetMapping("/id")
  public ResponseEntity<?> getUserId(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return null;
    Long id = tokenService.fetchUser(token).getId();

    return new ResponseEntity<>(createMap("Success", id), HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    if (credentials.get("email") == null && credentials.get("password") == null)
      return null;

    String email = credentials.get("email");
    String password = credentials.get("password");
    Optional<User> user = userRepository.findByEmailAndPassword(email, password);
    String token = tokenService.createToken(user.get());

    return user.isPresent()
        ? new ResponseEntity<>(createMap("Success", token), HttpStatus.OK)
        : null;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String, Object> details) {
    String email = (String) details.get("email");
    String name = (String) details.get("name");
    String password = (String) details.get("password");
    List<String> contacts = (List) details.get("contacts");
    User user = new User();
    user.setEmail(email);
    user.setName(name);
    user.setPassword(password);
    user.setContacts(contacts.toArray(new String[0]));
    User save = userRepository.save(user);
    return new ResponseEntity<>(createMap("Success", save), HttpStatus.OK);
  }

  @GetMapping("/logout")
  public ResponseEntity<?> logout(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return null;

    tokenService.delete(token);

    return new ResponseEntity<>(createMap("Success", "Logged out"), HttpStatus.OK);
  }


  private HashMap<String, Object> createMap(String status, Object message) {
    return new HashMap<String, Object>() {{
      put("status", status);
      put("message", message);
    }};
  }


}
