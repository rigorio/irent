package rigor.io.irent.user;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String email;
  private String name;
  @Getter(AccessLevel.NONE)
  private String password;
  private String[] contacts;

  public User(String email, String name, String password, String[] contacts) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.contacts = contacts;
  }

  public User() {
  }
}
