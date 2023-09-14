package intellistart.interviewplanning.model.user;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * User entity.
 */
@Entity
@Table(name = "users")
@JsonPropertyOrder({"email", "role", "id"})
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  private Role role;

  /**
   * Constructor.
   *
   * @param id - id
   * @param email - email
   * @param role - role
   */
  public User(Long id, String email, Role role) {
    this.id = id;
    this.email = email;
    this.role = role;
  }

  public User() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public Long getId() {
    return this.id;
  }

  public String getEmail() {
    return this.email;
  }

  public Role getRole() {
    return this.role;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String toString() {
    return "User(id=" + this.getId() + ", email=" + this.getEmail() + ", role="
            + this.getRole() + ")";
  }
}