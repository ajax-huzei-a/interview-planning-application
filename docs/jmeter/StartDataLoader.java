import com.intellias.intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import com.intellias.intellistart.interviewplanning.model.candidateslot.CandidateSlotRepository;
import com.intellias.intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import com.intellias.intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import com.intellias.intellistart.interviewplanning.model.interviewerslot.InterviewerSlotRepository;
import com.intellias.intellistart.interviewplanning.model.period.Period;
import com.intellias.intellistart.interviewplanning.model.period.PeriodService;
import com.intellias.intellistart.interviewplanning.model.user.Role;
import com.intellias.intellistart.interviewplanning.model.user.User;
import com.intellias.intellistart.interviewplanning.model.user.UserRepository;
import com.intellias.intellistart.interviewplanning.model.week.Week;
import com.intellias.intellistart.interviewplanning.model.week.WeekRepository;
import java.time.LocalDate;
import java.util.HashSet;
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
  private final WeekRepository weekRepository;
  private final PeriodService periodService;
  private final InterviewerSlotRepository interviewerSlotRepository;
  private final CandidateSlotRepository candidateSlotRepository;

  @Value("${first-coordinator-email}")
  private String email;

  /**
   * Start Data Loader.
   *
   * @param userRepository - user repository
   * @param weekRepository - week repository
   * @param periodService - period repository
   * @param interviewerSlotRepository - interviewer slot repository
   * @param candidateSlotRepository - candidate slot repository
   */
  @Autowired
  public StartDataLoader(UserRepository userRepository, WeekRepository weekRepository,
      PeriodService periodService, InterviewerSlotRepository interviewerSlotRepository,
      CandidateSlotRepository candidateSlotRepository) {
    this.userRepository = userRepository;
    this.weekRepository = weekRepository;
    this.periodService = periodService;
    this.interviewerSlotRepository = interviewerSlotRepository;
    this.candidateSlotRepository = candidateSlotRepository;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {

    User firstCoordinator = new User(null, email, Role.COORDINATOR);
    firstCoordinator = userRepository.save(firstCoordinator);
    System.out.println("Added first user: " + firstCoordinator);

    User secondCoordinator = new User(null, "sonyakazanceva1331@gmail.com", Role.COORDINATOR);
    secondCoordinator = userRepository.save(secondCoordinator);

    User firstInterviewer = new User(null, "email@gmail.com", Role.INTERVIEWER);
    firstInterviewer = userRepository.save(firstInterviewer);

    Week week2 = new Week(48L, new HashSet<>());
    week2 = weekRepository.save(week2);

    DayOfWeek dayOfWeek = DayOfWeek.TUE;
    LocalDate date = LocalDate.of(2022, 11, 29);

    Period periodInterviewer1 = periodService.obtainPeriod("09:00", "21:00");
    Period periodCandidate1 = periodService.obtainPeriod("09:00", "21:00");

    InterviewerSlot interviewerSlot1 = new InterviewerSlot(null, week2, dayOfWeek,
        periodInterviewer1, new HashSet<>(), firstInterviewer);
    InterviewerSlot interviewerSlot1Saved = interviewerSlotRepository.save(interviewerSlot1);

    CandidateSlot candidateSlot1 = new CandidateSlot(null, date, periodCandidate1, new HashSet<>(),
        "candidate1@gmail.com", "Maks");
    CandidateSlot candidateSlot1Saved = candidateSlotRepository.save(candidateSlot1);
  }
}

