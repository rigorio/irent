package rigor.io.irent.token;

import rigor.io.irent.user.User;

public interface TokenService {

  String createToken(User admin);

  void delete(String token);

  boolean isValid(String token);

  User fetchUser(String token);

}

