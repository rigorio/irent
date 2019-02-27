package rigor.io.irent.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Nullable
  private String profPic;
  private String email;
  private String firstName;
  private String lastName;
  private String password;
  private String[] contacts;
  private boolean verified;

  @Nullable
  public String getProfPic() {
    return profPic;
  }

  public void setProfPic(@Nullable String profPic) {
    this.profPic = profPic;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public boolean isVerified() {
    return verified;
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

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
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
        ", profPic='" + profPic + '\'' +
        ", email='" + email + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", contacts=" + Arrays.toString(contacts) +
        ", verified=" + verified +
        '}';
  }
}
