package rigor.io.irent.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.irent.token.TokenService;

import java.util.HashMap;
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

  @GetMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    if (credentials.get("email") == null && credentials.get("password") == null)
      return null;

    String email = credentials.get("email");
    String password = credentials.get("password");
    Optional<User> user = userRepository.findByEmailAndPassword(email, password);

    return user.isPresent()
        ? new ResponseEntity<>(
        new HashMap<String, String>() {{
          put("status", "Logged In");
          put("message", tokenService.createToken(user.get()));
        }}, HttpStatus.OK)
        : null;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String, Object> details) {
    String email = (String) details.get("email");
    String name = (String) details.get("name");
    String password = (String) details.get("password");
    String[] contacts = (String[]) details.get("contacts");
    User user = User.builder()
        .email(email)
        .name(name)
        .password(password)
        .contacts(contacts)
        .build();
    return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
  }

  @GetMapping("/logout")
  public ResponseEntity<?> logout(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return null;

    tokenService.delete(token);

    return new ResponseEntity<>("bye", HttpStatus.OK);
  }


}
