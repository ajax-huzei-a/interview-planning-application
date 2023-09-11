package intellistart.interviewplanning.controllers.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import intellistart.interviewplanning.model.booking.Booking;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.dayofweek.DayOfWeek;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot;
import intellistart.interviewplanning.model.week.WeekService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

/**
 * Dto object for representation all candidate, interviewer slots
 * and bookings for certain week.
 */
@Getter
@Setter
public class DashboardMapDto {

  // TODO: remove this when refactor weekService to weekUtils
  @JsonIgnore
  private final WeekService weekService;
  private Long weekNum;
  private Map<LocalDate, DashboardDto> dashboard;

  /**
   * Constructor to initialize object with given weekNum.
   * Needs {@link WeekService} instance to access weeks logic
   * (gaining date from week number and day, etc).
   *
   * @param weekNum number of week
   * @param weekService instance of service object to access logic from
   */
  public DashboardMapDto(Long weekNum, WeekService weekService) {
    this.dashboard = new HashMap<>();
    this.weekNum = weekNum;
    this.weekService = weekService;

    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      LocalDate date = weekService.convertToLocalDate(weekNum, dayOfWeek);
      dashboard.put(date, new DashboardDto());
    }
  }

  /**
   * Mapping all given interviewer slots to inner map
   * within all bookings from them.
   *
   * @param interviewerSlots set of InterviewerSlot objects to map information from
   */
  public void addInterviewerSlots(Set<InterviewerSlot> interviewerSlots) {

    for (InterviewerSlot interviewerSlot : interviewerSlots) {

      dashboard.get(weekService.convertToLocalDate(weekNum, interviewerSlot.getDayOfWeek()))
          .getInterviewerSlots()
          .add(new DashboardInterviewerSlotDto(interviewerSlot));

      Map<Long, DashboardBookingDto> bookingDtoMap = interviewerSlot.getBookings().stream()
          .collect(Collectors.toMap(Booking::getId, DashboardBookingDto::new));

      dashboard.get(weekService.convertToLocalDate(weekNum, interviewerSlot.getDayOfWeek()))
          .getBookings()
          .putAll(bookingDtoMap);
    }
  }

  /**
   * Mapping all given candidate slots to inner map
   * within all bookings from them.
   *
   * @param candidateSlots set of CandidateSlot objects to map information from
   */
  public void addCandidateSlots(Set<CandidateSlot> candidateSlots) {

    for (CandidateSlot candidateSlot : candidateSlots) {

      dashboard.get(candidateSlot.getDate())
          .getCandidateSlots()
          .add(new DashboardCandidateSlotDto(candidateSlot));

      Map<Long, DashboardBookingDto> bookingDtoMap = candidateSlot.getBookings().stream()
          .collect(Collectors.toMap(Booking::getId, DashboardBookingDto::new));

      dashboard.get(candidateSlot.getDate())
          .getBookings()
          .putAll(bookingDtoMap);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DashboardMapDto that = (DashboardMapDto) o;
    return Objects.equals(weekNum, that.weekNum) && Objects.equals(dashboard,
        that.dashboard);
  }

  @Override
  public int hashCode() {
    return Objects.hash(weekNum, dashboard);
  }
}
