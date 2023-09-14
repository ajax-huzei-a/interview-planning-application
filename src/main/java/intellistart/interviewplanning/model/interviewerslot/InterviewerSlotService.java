package intellistart.interviewplanning.model.interviewerslot;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile;
import intellistart.interviewplanning.model.booking.BookingService;
import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.week.Week;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for InterviewSlot entity.
 */
@Service
public class InterviewerSlotService {

  private final InterviewerSlotRepository interviewerSlotRepository;
  private final BookingService bookingService;

  /**
   * Constructor for InterviewerSlotService.
   *
   * @param interviewerSlotRepository - interviewerSlotRepository
   */
  @Autowired
  public InterviewerSlotService(
      InterviewerSlotRepository interviewerSlotRepository, BookingService bookingService) {
    this.interviewerSlotRepository = interviewerSlotRepository;
    this.bookingService = bookingService;
  }

  /**
   * Find instance of InterviewerSlot by id in database.
   *
   * @throws SlotException if no instance with given id
   */
  public InterviewerSlot getById(Long id) throws SlotException {
    return interviewerSlotRepository
        .findById(id)
        .orElseThrow(() -> new SlotException(SlotExceptionProfile.INTERVIEWER_SLOT_NOT_FOUND));
  }

  /**
   * Get InterviewerSlot and save it in the DB.
   *
   * @param interviewerSlot - interviewerSlot
   * @return InterviewerSlot
   */
  public InterviewerSlot create(InterviewerSlot interviewerSlot) {
    return interviewerSlotRepository.save(interviewerSlot);
  }

  /**
   * Get InterviewerSlot and update it in the DB.
   *
   * @param interviewerSlot - interviewerSlot
   * @return InterviewerSlot
   */
  public InterviewerSlot update(InterviewerSlot interviewerSlot) {
    return create(interviewerSlot);
  }

  /**
   * Method deletes all slots of the given user,
   * before deleting a slot it deletes all bookings in the slots being deleted.
   *
   * @param user - user by which slots are deleted.
   */
  public void deleteSlotsByUser(User user) {
    List<InterviewerSlot> interviewerSlots =
        interviewerSlotRepository.findInterviewerSlotsByUser(user);

    for (InterviewerSlot interviewerSlot : interviewerSlots) {
      if (interviewerSlot.getBookings() != null && !interviewerSlot.getBookings().isEmpty()) {
        bookingService.deleteBookings(interviewerSlot.getBookings());
      }
    }

    interviewerSlotRepository.deleteAll(interviewerSlots);
  }

  /**
   * Get slots of user by {@link Week}.
   *
   * @param week object to get slots with
   * @return {@link List} of {@link InterviewerSlot}
   */
  public Set<InterviewerSlot> getSlotsByWeek(Week week) {
    return interviewerSlotRepository.findByWeek(week);
  }

  /**
   * Get slots of user by weekId.
   *
   * @param userEmail - userEmail
   * @param weekId - weekId
   * @return {@link List} of {@link InterviewerSlot}
   */
  public List<InterviewerSlot> getSlotsByWeek(String userEmail, Long weekId) {
    return interviewerSlotRepository.getInterviewerSlotsByUserEmailAndWeekId(userEmail, weekId);
  }

  /**
   * Alias for {@link InterviewerSlotRepository} method.
   */
  public List<InterviewerSlot> getInterviewerSlotsByUserAndWeekAndDayOfWeek(
      User user, Week week, DayOfWeek dayOfWeek) {
    Long userId = user.getId();
    Long weekId = week.getId();

    return interviewerSlotRepository
        .getInterviewerSlotsByUserIdAndWeekIdAndDayOfWeek(userId, weekId, dayOfWeek);
  }

  /**
   * Alias for {@link InterviewerSlotRepository} method.
   */
  public List<InterviewerSlot> getInterviewerSlotsByUserAndWeek(
      User user, Week week) {
    Long userId = user.getId();
    Long weekId = week.getId();

    return interviewerSlotRepository
        .getInterviewerSlotsByUserIdAndWeekId(userId, weekId);
  }
}
