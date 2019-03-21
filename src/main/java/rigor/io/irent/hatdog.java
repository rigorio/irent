package rigor.io.irent;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rigor.io.irent.user.User;
import rigor.io.irent.user.UserRepository;

import java.util.Base64;
import java.util.Optional;

@Controller
public class hatdog {

  private UserRepository userRepository;

  public hatdog(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/users/confirmation")
  public String confirmation(@RequestParam String code) {
    System.out.println("linub ya bap");
    String email = new String(Base64.getDecoder().decode(code));
    Optional<User> userCon = userRepository.findByEmail(email);

    if (!userCon.isPresent())
      return "/already.html";

    User user = userCon.get();
    if (user.isVerified())
      return "/already.html";

    user.setVerified(true);
    User u = userRepository.save(user);
    return "/success.html";
  }

}
