package rigor.io.irent.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.irent.token.TokenService;

import javax.mail.MessagingException;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/users")
@SuppressWarnings("all")
public class UserController {

  private TokenService tokenService;
  private UserRepository userRepository;
  private EmailSender emailSender;

  public UserController(TokenService tokenService, UserRepository userRepository, EmailSender emailSender) {
    this.tokenService = tokenService;
    this.userRepository = userRepository;
    this.emailSender = emailSender;
  }

  @GetMapping("/id")
  public ResponseEntity<?> getUserId(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "failed"), HttpStatus.OK);
    Long id = tokenService.fetchUser(token).getId();

    return new ResponseEntity<>(createMap("Success", id), HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    String email = credentials.get("email");
    String password = credentials.get("password");
    if (email == null && password == null)
      return new ResponseEntity<>(createMap("Failed", "Username or password cannot be empty"), HttpStatus.OK);

    if (!emailSender.isValid(email))
      return new ResponseEntity<>(createMap("Failed", "Invalid email"), HttpStatus.OK);

    Optional<User> user = userRepository.findByEmailAndPassword(email, password);

    if (!user.isPresent())
      return new ResponseEntity<>(createMap("Failed", "Wrong email or password"), HttpStatus.OK);

    User u = user.get();

    if (!u.isVerified())
      return new ResponseEntity<>(createMap("Failed", "Please check your email to verify your account"), HttpStatus.OK);

    String token = tokenService.createToken(u);

    return new ResponseEntity<>(createMap("Success", token), HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String, Object> details) throws MessagingException {
    String email = (String) details.get("email");
    String name = (String) details.get("name");
    String password = (String) details.get("password");
    List<String> contacts = (List) details.get("contacts");

    if (email == null && password == null)
      return new ResponseEntity<>(createMap("Failed", "Username or password cannot be empty"), HttpStatus.OK);

    if (!emailSender.isValid(email))
      return new ResponseEntity<>(createMap("Failed", "Invalid email"), HttpStatus.OK);

    User user = new User();
    user.setEmail(email);
    user.setName(name);
    user.setPassword(password);
    user.setContacts(contacts.toArray(new String[0]));
    user.setVerified(false);

    Optional<User> u = userRepository.findByEmail(user.getEmail());
    if (u.isPresent())
      return new ResponseEntity<>(createMap("Failed", "Email is already taken"), HttpStatus.OK);

    emailSender.sendMail(user.getEmail());
    User save = userRepository.save(user);
    return new ResponseEntity<>(createMap("Success", save), HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<?> editUserDetails(@RequestParam(required = false) String token,
                                    @RequestBody Map<String, Object> data) {

    if (!tokenService.isValid(token))
      return new ResponseEntity<>(createMap("Failed", "Not Authorized"), HttpStatus.OK);

    String name = (String) data.get("name");
    List<String> contacts = (List) data.get("contacts");

    User user = tokenService.fetchUser(token);

    user.setName(name);
    user.setContacts(contacts.toArray(new String[0]));

    User u = userRepository.save(user);

    return new ResponseEntity<>(createMap("Saved", u), HttpStatus.OK);
  }

  @GetMapping("/confirmation")
  public ResponseEntity<?> confirmation(@RequestParam String code) {

    String email = new String(Base64.getDecoder().decode(code));
    Optional<User> userCon = userRepository.findByEmail(email);

    if (!userCon.isPresent())
      return new ResponseEntity<>(createMap("Failed", "There was a problem with your verification"), HttpStatus.OK);

    User user = userCon.get();
    if (user.isVerified())
      return new ResponseEntity<>(createMap("Already Verified", "Email has already been verified"), HttpStatus.OK);

    user.setVerified(true);
    User u = userRepository.save(user);
    return new ResponseEntity<>(createMap("Success", "User successfully verified. Please login to the app to use"), HttpStatus.OK);
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
