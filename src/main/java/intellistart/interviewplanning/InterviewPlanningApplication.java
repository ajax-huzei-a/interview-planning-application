package intellistart.interviewplanning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application.
 */
@SpringBootApplication
@EnableCaching
public class InterviewPlanningApplication {
  public static void main(String[] args) {
    SpringApplication.run(InterviewPlanningApplication.class, args);
  }

}
