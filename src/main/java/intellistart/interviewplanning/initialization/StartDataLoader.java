package intellistart.interviewplanning.initialization;

import intellistart.interviewplanning.model.user.Role;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Class for initializing first coordinator in the system.
 */
@Component
public class StartDataLoader implements ApplicationRunner {

  private final UserRepository userRepository;

  @Value("${first-coordinator-email}")
  private String email;

  /**
   * Initial data load.
   */
  @Autowired
  public StartDataLoader(
      UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void run(ApplicationArguments args) {


    User firstCoordinator = new User(1L, email, Role.COORDINATOR);
    firstCoordinator = userRepository.save(firstCoordinator);

    //    User interviewer = new User(null, "guzey2001@gmail.com", Role.INTERVIEWER);
    //    interviewer = userRepository.save(interviewer);

    System.out.println("Added first user: " + firstCoordinator);

    //    System.out.println("Added interviewer user: " + interviewer);
  }
}