package intellistart.interviewplanning.model.candidateslot.validation;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.candidateslot.CandidateSlotService;
import intellistart.interviewplanning.model.period.Period;
import intellistart.interviewplanning.model.period.PeriodService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Validator for CandidateSlot.
 */
@Service
public class CandidateSlotValidator {
  private final CandidateSlotService candidateSlotService;
  private final PeriodService periodService;

  @Autowired
  public CandidateSlotValidator(CandidateSlotService candidateSlotService,
      PeriodService periodService) {
    this.candidateSlotService = candidateSlotService;
    this.periodService = periodService;
  }

  /**
   * Validate CandidateSlot object for what the slot should be in the future,
   * whether the slot is not overlapping.
   *
   * @param candidateSlot - the slot that we will validate.
   *
   * @throws SlotException - when parameters are incorrect or slot is overlapping.
   */
  public void validateCreating(CandidateSlot candidateSlot) throws SlotException {
    validateSlotInFuture(candidateSlot);
    validateOverlapping(candidateSlot);
  }

  /**
   * Validate CandidateSlot object for all slot creation checks,
   * whether the slot exists, whether the slot is not booking.
   *
   * @param candidateSlot - the updated slot that we will validate.
   *
   * @throws SlotException - when parameters are incorrect or updated slot is booked
   *     slot is overlapping.
   */

  public void validateUpdating(CandidateSlot candidateSlot) throws SlotException {
    validateSlotIsBookingAndTheSlotExists(candidateSlot.getId());
    validateCreating(candidateSlot);
  }

  /**
   * Validate that date in CandidateSlot in the future.
   *
   * @param candidateSlot - the slot that we will validate.
   *
   * @throws SlotException - when parameters are incorrect.
   */
  private void validateSlotInFuture(CandidateSlot candidateSlot) throws SlotException {
    if (LocalDate.now().isAfter(candidateSlot.getDate())) {
      throw new SlotException(SlotExceptionProfile.INVALID_BOUNDARIES);
    }
  }

  /**
   * Validate that the slot does not overlap with other slots.
   *
   * @param candidateSlot - the slot that we validate.
   *
   * @throws SlotException - when the slot is overlapping.
   */
  private void validateOverlapping(CandidateSlot candidateSlot) throws SlotException {
    Period period = candidateSlot.getPeriod();

    List<CandidateSlot> candidateSlotList =
        candidateSlotService.getCandidateSlotsByEmailAndDate(candidateSlot.getEmail(),
            candidateSlot.getDate());

    if (!candidateSlotList.isEmpty()) {
      for (CandidateSlot item : candidateSlotList) {
        if (candidateSlot.getId() != item.getId()
                && periodService.areOverlapping(period, item.getPeriod())) {
          throw new SlotException(SlotExceptionProfile.SLOT_IS_OVERLAPPING);
        }
      }
    }
  }

  /**
   * Validate for the given number of slot is exist in DB,
   * whether the slot is not booking.
   *
   * @param id - - the number of slot that we must check.
   *
   * @throws SlotException - when id not found in DB or slot is booked.
   */
  private void validateSlotIsBookingAndTheSlotExists(Long id)
      throws SlotException {
    CandidateSlot candidateSlot = candidateSlotService.getById(id);

    if (!candidateSlot.getBookings().isEmpty()) {
      throw new SlotException(SlotExceptionProfile.SLOT_IS_BOOKED);
    }
  }
}
