package rigor.io.irent.user;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String email;
  private String name;
  @Getter(AccessLevel.NONE)
  private String password;
  private String[] contacts;

}
