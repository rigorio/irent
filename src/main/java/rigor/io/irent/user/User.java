package rigor.io.irent.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;

@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String email;
  private String name;
  private String password;
  private String[] contacts;
  private boolean verified;

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public boolean isVerified() {
    return verified;
  }

  public User() {
  }

  public User(String email, String name, String password, String[] contacts) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.contacts = contacts;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String[] getContacts() {
    return contacts;
  }

  public void setContacts(String[] contacts) {
    this.contacts = contacts;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", email='" + email + '\'' +
        ", name='" + name + '\'' +
        ", password='" + password + '\'' +
        ", contacts=" + Arrays.toString(contacts) +
        '}';
  }
}
