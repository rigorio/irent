package rigor.io.irent.token;

public interface TokenService {

  String createToken(User admin);

  void delete(String token);

  boolean isValid(String token);

  User fetchUser(String token);

}

