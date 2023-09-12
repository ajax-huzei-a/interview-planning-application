package intellistart.interviewplanning.model.candidateslot;

import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile;
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

  /**
   * Constructor.
   */
  @Autowired
  public CandidateSlotService(CandidateSlotRepository candidateSlotRepository) {
    this.candidateSlotRepository = candidateSlotRepository;
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
  public CandidateSlot getById(Long id) throws SlotException {
    return candidateSlotRepository
        .findById(id)
        .orElseThrow(() -> new SlotException(SlotExceptionProfile.CANDIDATE_SLOT_NOT_FOUND));
  }

  public Set<CandidateSlot> getCandidateSlotsByDate(LocalDate date) {
    return candidateSlotRepository.findByDate(date);
  }
}
