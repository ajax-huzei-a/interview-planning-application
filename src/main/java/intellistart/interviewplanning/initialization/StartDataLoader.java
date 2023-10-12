package intellistart.interviewplanning.initialization;

import intellistart.interviewplanning.model.user.Coordinator;
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

//
//    User firstCoordinator = new Coordinator();
//    firstCoordinator.setEmail(email);
//    firstCoordinator = userRepository.save(firstCoordinator);
//
//
//    System.out.println("Added first user: " + firstCoordinator);

  }
}