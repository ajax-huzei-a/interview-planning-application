package intellistart.interviewplanning.model.candidateslot;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile;
import intellistart.interviewplanning.model.period.PeriodService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for CandidateSlot entity.
 */
@Service
public class CandidateSlotService {

  private final CandidateSlotRepository candidateSlotRepository;
  private final PeriodService periodService;

  /**
   * Constructor.
   */
  @Autowired
  public CandidateSlotService(CandidateSlotRepository candidateSlotRepository,
      PeriodService periodService) {
    this.candidateSlotRepository = candidateSlotRepository;
    this.periodService = periodService;
  }

  /**
   * Created in DB the CandidateSlot object.
   *
   * @param candidateSlot - The object to be saved in the database.
   *
   * @return CandidateSlot - An object that was successfully saved in the database.
   */
  public CandidateSlot create(CandidateSlot candidateSlot) {
    return candidateSlotRepository.save(candidateSlot);
  }

  /**
   * Updated in DB the CandidateSlot object.
   *
   * @param candidateSlot - Updated slot data.
   *
   * @return CandidateSlot - An object that was successfully updated in the database.
   */
  public CandidateSlot update(CandidateSlot candidateSlot) {
    return create(candidateSlot);
  }


  /**
   * Returned slots of current Candidate.
   *
   * @return List of CandidateSlot - the list of slots of current candidate.
   */
  public List<CandidateSlot> getAllSlotsByEmail(String email) {
    return candidateSlotRepository.findByEmail(email);
  }

  /**
   * Returned slots of current Candidate by date.
   *
   * @param date - date on which the database will be searched.
   *
   * @return List of CandidateSlot - Slots that were found in the database by given parameters.
   */
  public List<CandidateSlot> getCandidateSlotsByEmailAndDate(String email, LocalDate date) {
    return candidateSlotRepository.findByEmailAndDate(email, date);
  }

  /**
   * Find CandidateSlot of current Candidate in database by id.
   *
   * @param id - The slot number to search for in the database.
   *
   * @throws SlotException if slot with given id is not present
   */
  public CandidateSlot findById(Long id) throws SlotException {
    return candidateSlotRepository
        .findById(id)
        .orElseThrow(() -> new SlotException(SlotExceptionProfile.CANDIDATE_SLOT_NOT_FOUND));
  }

  /**
   * Created CandidateSlot object by given parameters.
   *
   * @param date - slot date.
   * @param from - the time from which the slot will start.
   * @param to - the time by which the slot will end.
   *
   * @return CandidateSlot - created object by parameters.
   */
  public CandidateSlot createCandidateSlot(LocalDate date, String from, String to, String email,
      String name) throws SlotException {
    CandidateSlot candidateSlot = new CandidateSlot();

    candidateSlot.setDate(date);
    candidateSlot.setPeriod(periodService.obtainPeriod(from, to));
    candidateSlot.setEmail(email);
    candidateSlot.setName(name);
    
    return candidateSlot;
  }

  public Set<CandidateSlot> getCandidateSlotsByDate(LocalDate date) {
    return candidateSlotRepository.findByDate(date);
  }
}
