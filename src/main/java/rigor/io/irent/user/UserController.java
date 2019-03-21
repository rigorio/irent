package rigor.io.irent.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rigor.io.irent.ResponseMessage;
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

  @GetMapping("")
  public ResponseEntity<?> getUserId(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(new ResponseMessage("Failed", "failed"), HttpStatus.OK);
    User user = tokenService.fetchUser(token);

    return new ResponseEntity<>(new ResponseMessage("Success", user), HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    System.out.println("hotdog");
    String email = credentials.get("email");
    String password = credentials.get("password");
    if (email == null && password == null)
      return new ResponseEntity<>(new ResponseMessage("Failed", "Username or password cannot be empty"), HttpStatus.OK);

    if (!emailSender.isValid(email))
      return new ResponseEntity<>(new ResponseMessage("Failed", "Invalid email"), HttpStatus.OK);

    Optional<User> user = userRepository.findByEmailAndPassword(email, password);

    if (!user.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "Wrong email or password"), HttpStatus.OK);

    User u = user.get();

    if (!u.isVerified())
      return new ResponseEntity<>(new ResponseMessage("Failed", "Please check your email to verify your account"), HttpStatus.OK);

    String token = tokenService.createToken(u);

    return new ResponseEntity<>(new ResponseMessage("Success", token), HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String, Object> details) throws MessagingException {
    String email = (String) details.get("email");
    String firstName = (String) details.get("firstName");
    String lastName = (String) details.get("lastName");
    String password = (String) details.get("password");
    List<String> contacts = (List) details.get("contacts");

    if (email == null && password == null)
      return new ResponseEntity<>(new ResponseMessage("Failed", "Username or password cannot be empty"), HttpStatus.OK);

    if (!emailSender.isValid(email))
      return new ResponseEntity<>(new ResponseMessage("Failed", "Invalid email"), HttpStatus.OK);

    User user = User.builder()
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .password(password)
        .contacts(contacts.toArray(new String[0]))
        .verified(false)
        .build();

    Optional<User> u = userRepository.findByEmail(user.getEmail());
    if (u.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "Email is already taken"), HttpStatus.OK);

    emailSender.sendMail(user.getEmail());
    User save = userRepository.save(user);
    return new ResponseEntity<>(new ResponseMessage("Success", save), HttpStatus.OK);
  }

  @PutMapping("")
  public ResponseEntity<?> editUserDetails(@RequestParam(required = false) String token,
                                           @RequestBody Map<String, Object> data) {

    if (!tokenService.isValid(token))
      return new ResponseEntity<>(new ResponseMessage("Failed", "Not Authorized"), HttpStatus.OK);

    String firstName = (String) data.get("firstName");
    String lastName = (String) data.get("lastName");
    String location = (String) data.get("location");
    String email = (String) data.get("email");
    String profPic = (String) data.get("profPic");
    List<String> contacts = (List) data.get("contacts");

    User user = tokenService.fetchUser(token);

    user.setProfPic(profPic);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setLocation(location);
    user.setEmail(email);
    user.setContacts(contacts.toArray(new String[0]));

    User u = userRepository.save(user);

    return new ResponseEntity<>(new ResponseMessage("Saved", u), HttpStatus.OK);
  }


  @PostMapping("/password")
  public ResponseEntity<?> changePassword(@RequestParam(required = false) String token,
                                          @RequestBody Map<String, String> data) {
    if (!tokenService.isValid(token))
      return new ResponseEntity<>(new ResponseMessage("Failed", "Not Authorized"), HttpStatus.OK);


    String oldPassword = data.get("oldPassword");
    String newPassword = data.get("newPassword");

    User user = tokenService.fetchUser(token);

    Optional<User> u = userRepository.findByEmailAndPassword(user.getEmail(), oldPassword);

    if (!u.isPresent())
      return new ResponseEntity<>(new ResponseMessage("Failed", "Wrong password"), HttpStatus.OK);

    user.setPassword(newPassword);
    User savedUser = userRepository.save(user);

    return new ResponseEntity<>(new ResponseMessage("Success", "Password was changed"), HttpStatus.OK);
  }

  @GetMapping("/logout")
  public ResponseEntity<?> logout(@RequestParam(required = false) String token) {
    if (!tokenService.isValid(token))
      return null;

    tokenService.delete(token);

    return new ResponseEntity<>(new ResponseMessage("Success", "Logged out"), HttpStatus.OK);
  }


}
