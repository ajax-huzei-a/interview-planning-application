package intellistart.interviewplanning.initialization;

import intellistart.interviewplanning.model.user.Coordinator;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StartDataLoader implements ApplicationRunner {

  private final UserRepository userRepository;

  private static final Logger logger = LoggerFactory.getLogger(StartDataLoader.class);

  @Value("${first-coordinator-email}")
  private String email;

  @Autowired
  public StartDataLoader(
      UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void run(ApplicationArguments args) {
    User firstCoordinator = new Coordinator();
    firstCoordinator.setEmail(email);
    userRepository.save(firstCoordinator).subscribe(
        user -> logger.info("Added first user: {}", user.getEmail()),
        error -> logger.error("Error while adding first user: {}", error.getMessage()),
        () -> logger.info("Complete adding first user")
    );
  }
}