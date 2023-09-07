package intellistart.interviewplanning.model.user;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 * DAO for User entity.
 */
public interface UserRepository extends CrudRepository<User, Long> {

  User findByEmail(String email);

  List<User> findByRole(Role role);

}
