package intellistart.interviewplanning.controllers;

import intellistart.interviewplanning.controllers.dto.BookingLimitDto;
import intellistart.interviewplanning.controllers.dto.BookingLimitDtoKt;
import intellistart.interviewplanning.controllers.dto.InterviewerSlotDtoKt;
import intellistart.interviewplanning.controllers.dto.InterviewerSlotDtoRequest;
import intellistart.interviewplanning.controllers.dto.InterviewerSlotDtoResponse;
import intellistart.interviewplanning.controllers.dto.InterviewerSlotsDto;
import intellistart.interviewplanning.controllers.dto.InterviewerSlotsDtoKt;
import intellistart.interviewplanning.exceptions.BookingLimitException;
import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.UserException;
import intellistart.interviewplanning.model.bookinglimit.BookingLimit;
import intellistart.interviewplanning.model.bookinglimit.BookingLimitService;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotDtoValidator;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService;
import intellistart.interviewplanning.model.user.User;
import intellistart.interviewplanning.model.user.UserService;
import intellistart.interviewplanning.model.week.WeekService;
import intellistart.interviewplanning.security.JwtUserDetails;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for processing requests from users with Interview role.
 */
@RestController
@CrossOrigin
public class InterviewerController {

  private final InterviewerSlotService interviewerSlotService;
  private final InterviewerSlotDtoValidator interviewerSlotDtoValidator;
  private final BookingLimitService bookingLimitService;
  private final WeekService weekService;
  private final UserService userService;

  /**
   * Constructor.
   *
   * @param interviewerSlotService      - interviewerSlotService
   * @param interviewerSlotDtoValidator - interviewerSlotDtoValidator
   * @param bookingLimitService         - bookingLimitService
   * @param weekService                 - weekService
   * @param userService                 - userService
   */
  @Autowired
  public InterviewerController(
      InterviewerSlotService interviewerSlotService,
      InterviewerSlotDtoValidator interviewerSlotDtoValidator,
      BookingLimitService bookingLimitService,
      WeekService weekService,
      UserService userService) {
    this.interviewerSlotService = interviewerSlotService;
    this.interviewerSlotDtoValidator = interviewerSlotDtoValidator;
    this.bookingLimitService = bookingLimitService;
    this.weekService = weekService;
    this.userService = userService;
  }

  /**
   * Post Request for creating slot.
   *
   * @param interviewerSlotDto - DTO from request
   * @param interviewerId      - user Id from request
   * @return interviewerSlotDto - and/or HTTP status
   *
   * @throws SlotException when:
   *     <ul>
   *     <li>cannot edit this week
   *     <li>invalid boundaries of time period
   *     <li>when slot is not found by slotId
   *     <li>slot is overlapping
   *     </ul>
   *
   * @throws UserException invalid user (interviewer) exception
   */
  @PostMapping("/interviewers/{interviewerId}/slots")
  public ResponseEntity<InterviewerSlotDtoResponse> createInterviewerSlot(
      @RequestBody InterviewerSlotDtoRequest interviewerSlotDto,
      @PathVariable("interviewerId") Long interviewerId,
      Authentication authentication
  ) throws SlotException, UserException {

    InterviewerSlot interviewerSlot = interviewerSlotDtoValidator
        .validateAndCreate(interviewerSlotDto, authentication, interviewerId);

    return new ResponseEntity<>(InterviewerSlotDtoKt.toDTOResponse(interviewerSlot), HttpStatus.OK);
  }

  /**
   * Post Request for updating slot.
   *
   * @param interviewerSlotDtoRequest - DTO from request
   * @param interviewerId      - user Id from request
   * @param slotId             - slot Id from request
   *
   * @return interviewerSlotDto - and/or HTTP status
   *
   * @throws UserException when:
   *     <ul>
   *     <li>cannot edit this week
   *     <li>invalid boundaries of time period
   *     <li>when slot is not found by slotId
   *     </ul>
   *
   * @throws SlotException - when slot has at least one booking or slot overlaps
   */
  @PostMapping("/interviewers/{interviewerId}/slots/{slotId}")
  public ResponseEntity<InterviewerSlotDtoResponse> updateInterviewerSlot(
      @RequestBody InterviewerSlotDtoRequest interviewerSlotDtoRequest,
      @PathVariable("interviewerId") Long interviewerId,
      @PathVariable("slotId") Long slotId,
      Authentication authentication
  ) throws SlotException, UserException {

    InterviewerSlot interviewerSlot = interviewerSlotDtoValidator
            .validateAndUpdate(interviewerSlotDtoRequest,
        authentication, interviewerId, slotId);

    return new ResponseEntity<>(InterviewerSlotDtoKt.toDTOResponse(interviewerSlot), HttpStatus.OK);
  }

  /**
   * Post Request for creating booking limit.
   *
   * @param bookingLimitDto - DTO for BookingLimit
   * @param interviewerId   - user id from request
   * @return BookingLimitDto and HTTP status
   * @throws UserException - invalid user (interviewer) exception or not interviewer id
   * @throws BookingLimitException - invalid bookingLimit exception
   */
  @PostMapping("/interviewers/{interviewerId}/booking-limits")
  public ResponseEntity<BookingLimitDto> createBookingLimit(
      @RequestBody BookingLimitDto bookingLimitDto,
      @PathVariable("interviewerId") Long interviewerId)
      throws UserException, BookingLimitException {

    User user = userService.getUserById(interviewerId);

    BookingLimit bookingLimit = bookingLimitService.createBookingLimit(user,
        bookingLimitDto.getBookingLimit());

    return ResponseEntity.ok(BookingLimitDtoKt.toDTO(bookingLimit));
  }

  /**
   * Request for getting booking limit for current week.
   *
   * @param interviewerId - user Id from request
   * @return BookingLimitDto and HTTP status
   * @throws UserException - invalid user (interviewer) exception or ot interviewer id
   */
  @GetMapping("/interviewers/{interviewerId}/booking-limits/current-week")
  public ResponseEntity<BookingLimitDto> getBookingLimitForCurrentWeek(
      @PathVariable("interviewerId") Long interviewerId)
      throws UserException {

    User user = userService.getUserById(interviewerId);

    BookingLimit bookingLimit = bookingLimitService.getBookingLimitForCurrentWeek(user);

    return ResponseEntity.ok(BookingLimitDtoKt.toDTO(bookingLimit));
  }

  /**
   * Request for getting booking limit for next week.
   *
   * @param interviewerId user Id from request
   * @return BookingLimitDto and HTTP status
   * @throws UserException - invalid user (interviewer) exception or ot interviewer id
   */
  @GetMapping("/interviewers/{interviewerId}/booking-limits/next-week")
  public ResponseEntity<BookingLimitDto> getBookingLimitForNextWeek(
      @PathVariable("interviewerId") Long interviewerId)
      throws UserException {

    User user = userService.getUserById(interviewerId);

    BookingLimit bookingLimit = bookingLimitService.getBookingLimitForNextWeek(user);

    return ResponseEntity.ok(BookingLimitDtoKt.toDTO(bookingLimit));
  }

  /**
   * Request for getting Interviewer Slots of current user for current week.
   *
   * @param authentication - user
   * @return {@link List} of {@link InterviewerSlot}
   */
  @GetMapping("/interviewers/current/slots")
  public ResponseEntity<InterviewerSlotsDto> getInterviewerSlotsForCurrentWeek(
      Authentication authentication) {
    JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();

    String email = jwtUserDetails.getEmail();
    Long currentWeekId = weekService.getCurrentWeek().getId();

    List<InterviewerSlot> slots = interviewerSlotService.getSlotsByWeek(email, currentWeekId);

    return ResponseEntity.ok(InterviewerSlotsDtoKt.toDTOList(slots));
  }

  /**
   * Request for getting Interviewer Slots of current user for next week.
   *
   * @param authentication - user
   * @return {@link List} of {@link InterviewerSlot}
   */
  @GetMapping("/interviewers/next/slots")
  public ResponseEntity<InterviewerSlotsDto> getInterviewerSlotsForNextWeek(
      Authentication authentication) {
    JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();

    String email = jwtUserDetails.getEmail();
    Long nextWeekId = weekService.getNextWeek().getId();

    List<InterviewerSlot> slots = interviewerSlotService.getSlotsByWeek(email, nextWeekId);

    return ResponseEntity.ok(InterviewerSlotsDtoKt.toDTOList(slots));
  }
}