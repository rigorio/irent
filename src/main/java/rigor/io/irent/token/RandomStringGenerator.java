package rigor.io.irent.token;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomStringGenerator {

  public List<String> generateCodes(int number) {
    Set<String> codes = new HashSet<>(number);
    String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
    RandomString gen = new RandomString(10, new SecureRandom(), easy);
    for (int i = 0; i < number; i++) {
      codes.add(gen.nextString());
    }
    return new ArrayList<>(codes);
  }


}
