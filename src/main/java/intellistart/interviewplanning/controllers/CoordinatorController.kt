package intellistart.interviewplanning.controllers;

import intellistart.interviewplanning.controllers.dto.BookingDto;
import intellistart.interviewplanning.controllers.dto.BookingDtoKt;
import intellistart.interviewplanning.controllers.dto.DashboardMapDto;
import intellistart.interviewplanning.controllers.dto.EmailDto;
import intellistart.interviewplanning.controllers.dto.UsersDto;
import intellistart.interviewplanning.controllers.dto.UsersDtoKt;
import intellistart.interviewplanning.exceptions.BookingException;
import intellistart.interviewplanning.exceptions.BookingLimitException;
import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.UserException;
import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.booking.BookingService;
import intellistart.interviewplanning.model.booking.validation.BookingValidator;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.candidateslot.CandidateSlotService;
import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService;
import intellistart.interviewplanning.model.period.PeriodService;
import intellistart.interviewplanning.model.user.Role;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.user.UserService;
import intellistart.interviewplanning.model.week.Week;
import intellistart.interviewplanning.model.week.WeekService;
import intellistart.interviewplanning.security.JwtUserDetails;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for processing requests from users with Coordinator role.
 */
@RestController
@CrossOrigin
public class CoordinatorController {

  private final BookingService bookingService;
  private final BookingValidator bookingValidator;
  private final InterviewerSlotService interviewerSlotService;
  private final CandidateSlotService candidateSlotService;
  private final PeriodService periodService;
  private final UserService userService;
  private final WeekService weekService;

  /**
   * Constructor.
   */
  @Autowired
  public CoordinatorController(BookingService bookingService, BookingValidator bookingValidator,
      InterviewerSlotService interviewerSlotService, CandidateSlotService candidateSlotService,
      PeriodService periodService, UserService userService,
      WeekService weekService) {

    this.bookingService = bookingService;
    this.bookingValidator = bookingValidator;
    this.interviewerSlotService = interviewerSlotService;
    this.candidateSlotService = candidateSlotService;
    this.periodService = periodService;
    this.userService = userService;
    this.weekService = weekService;
  }

  /**
   * POST request to grant a INTERVIEWER role by email.
   *
   * @param request - Request body of POST mapping.
   * @return ResponseEntity - Response of the granted User.
   * @throws UserException - when user already has role.
   */
  @PostMapping("/users/interviewers")
  public ResponseEntity<User> grantInterviewerByEmail(@RequestBody EmailDto request)
      throws UserException {
    return ResponseEntity.ok(userService.grantRoleByEmail(request.getEmail(), Role.INTERVIEWER));
  }

  /**
   * POST request to grant a COORDINATOR role by email.
   *
   * @param request - Request body of POST mapping.
   * @return ResponseEntity - Response of the granted User.
   * @throws UserException - - when user already has role.
   */
  @PostMapping("/users/coordinators")
  public ResponseEntity<User> grantCoordinatorByEmail(@RequestBody EmailDto request)
      throws UserException {
    return ResponseEntity.ok(userService.grantRoleByEmail(request.getEmail(), Role.COORDINATOR));
  }

  /**
   * GET request to get a list of users with the interviewer role.
   *
   * @return ResponseEntity - Response of the list of users with the interviewer role.
   */
  @GetMapping("/users/interviewers")
  public ResponseEntity<UsersDto> getAllInterviewers() {
    return ResponseEntity
            .ok(UsersDtoKt.toUsersDto(userService.obtainUsersByRole(Role.INTERVIEWER)));
  }

  /**
   * GET request to get a list of users with the coordinator role.
   *
   * @return ResponseEntity - Response of the list of users with the coordinator role.
   */
  @GetMapping("/users/coordinators")
  public ResponseEntity<UsersDto> getAllCoordinators() {
    return ResponseEntity
            .ok(UsersDtoKt.toUsersDto(userService.obtainUsersByRole(Role.COORDINATOR)));
  }

  /**
   * DELETE request for deleting interviewer.
   *
   * @param id - the interviewer's id to delete.
   * @return ResponseEntity - the deleted user.
   * @throws UserException - when the user not found by given id or has not interviewer role.
   */
  @DeleteMapping("/users/interviewers/{id}")
  public ResponseEntity<User> deleteInterviewerById(@PathVariable("id") Long id)
      throws UserException {
    return ResponseEntity.ok(userService.deleteInterviewer(id));
  }

  /**
   * DELETE request for deleting interviewer.
   *
   * @param id - the interviewer's id to delete.
   * @return ResponseEntity - the deleted user.
   * @throws UserException - when the user not found by given id or the coordinator removes himself
   */
  @DeleteMapping("/users/coordinators/{id}")
  public ResponseEntity<User> deleteCoordinatorById(@PathVariable("id") Long id,
      Authentication authentication)
      throws UserException {

    JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
    String currentEmailCoordinator = jwtUserDetails.getEmail();
    return ResponseEntity.ok(userService.deleteCoordinator(id, currentEmailCoordinator));
  }

  /**
   * Returns {@link DashboardMapDto} object with week num and map of
   * LocalDate with DashboardDto which contains all candidate, interviewer
   * slots and booking for the certain date.
   *
   * @param weekId number of week to get all slots from
   * @return all candidate, interviewer slots and bookings for certain week
   */
  @GetMapping("/weeks/{weekNum}/dashboard")
  public ResponseEntity<DashboardMapDto> getDashboard(@PathVariable("weekNum") Long weekId) {

    Week week = weekService.getWeekByWeekNum(weekId);
    DashboardMapDto dashboard = new DashboardMapDto(weekId, weekService);

    Set<InterviewerSlot> interviewerSlots = interviewerSlotService.getSlotsByWeek(week);
    dashboard.addInterviewerSlots(interviewerSlots);

    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      LocalDate date = weekService.convertToLocalDate(weekId, dayOfWeek);
      Set<CandidateSlot> candidateSlots = candidateSlotService.getCandidateSlotsByDate(date);
      dashboard.addCandidateSlots(candidateSlots);
    }

    return ResponseEntity.ok(dashboard);
  }

  /**
   * POST request method for updating booking by id.
   *
   * @param bookingDto request DTO
   *
   * @return ResponseEntity - Response of the saved updated object converted to a DTO.
   *
   * @throws SlotException          if Period boundaries are Invalid or
   *                                Candidate/Interviewer Slot is not found
   * @throws BookingLimitException  if interviewer's booking limit is exceeded
   * @throws BookingException       if CandidateSlot, InterviewerSlot
   *                                do not intersect with Period
   */
  @PostMapping("bookings/{id}")
  public ResponseEntity<BookingDto> updateBooking(
      @RequestBody BookingDto bookingDto,
      @PathVariable Long id)
      throws SlotException, BookingException, BookingLimitException, UserException {

    Booking updatingBooking = bookingService.findById(id);
    Booking newDataBooking = getFromDto(bookingDto);

    bookingValidator.validateUpdating(updatingBooking, newDataBooking);
    populateFields(updatingBooking, newDataBooking);

    Booking savedBooking = bookingService.save(updatingBooking);
    return ResponseEntity.ok(BookingDtoKt.toDto(savedBooking));
  }

  /**
   * POST request method for adding booking.
   *
   * @param bookingDto request DTO
   *
   * @return ResponseEntity - Response of the saved created object converted to a DTO.
   *
   * @throws SlotException          if Period boundaries are Invalid
   *                                or Candidate/Interviewer Slot is not found
   * @throws BookingLimitException  if interviewer's booking limit is exceeded
   * @throws BookingException       if CandidateSlot, InterviewerSlot
   *                                do not intersect with Period
   */
  @PostMapping("bookings")
  public ResponseEntity<BookingDto> createBooking(
      @RequestBody BookingDto bookingDto)
      throws SlotException, BookingException, BookingLimitException, UserException {

    Booking newBooking = getFromDto(bookingDto);

    bookingValidator.validateCreating(newBooking);
    Booking savedBooking = bookingService.save(newBooking);

    return ResponseEntity.ok(BookingDtoKt.toDto(savedBooking));
  }

  Booking getFromDto(BookingDto bookingDto) throws SlotException {
    Booking booking = new Booking();

    booking.setSubject(bookingDto.getSubject());
    booking.setDescription(bookingDto.getDescription());

    booking.setInterviewerSlot(interviewerSlotService
        .findById(bookingDto.getInterviewerSlotId()));

    booking.setCandidateSlot(candidateSlotService
        .findById(bookingDto.getCandidateSlotId()));

    booking.setPeriod(periodService.obtainPeriod(
        bookingDto.getFrom(),
        bookingDto.getTo()));

    return booking;
  }

  private void populateFields(Booking booking, Booking newDataBooking) {
    booking.setSubject(newDataBooking.getSubject());
    booking.setDescription(newDataBooking.getDescription());
    booking.setInterviewerSlot(newDataBooking.getInterviewerSlot());
    booking.setCandidateSlot(newDataBooking.getCandidateSlot());
    booking.setPeriod(newDataBooking.getPeriod());
  }

  /**
   * DELETE request for deleting booking by id.
   *
   * @param bookingId - id of booking to delete
   * @return DTO of deleted booking
   * @throws BookingException - throw if booking by given id wasn't found
   */
  @DeleteMapping("/bookings/{id}")
  public ResponseEntity<BookingDto> deleteBooking(@PathVariable("id") Long bookingId)
      throws BookingException {

    Booking bookingToDelete = bookingService.findById(bookingId);
    bookingService.deleteBooking(bookingToDelete);

    return ResponseEntity.ok(BookingDtoKt.toDto(bookingToDelete));
  }
}
