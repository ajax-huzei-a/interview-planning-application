package intellistart.interviewplanning.model.candidateslot

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import java.time.LocalDate
import org.springframework.stereotype.Service

/**
 * Service for CandidateSlot entity.
 */
@Service
class CandidateSlotService(private val candidateSlotRepository: CandidateSlotRepository) {

    /**
     * Create a CandidateSlot object in the database.
     *
     * @param candidateSlot - The object to be saved in the database.
     *
     * @return CandidateSlot - An object that was successfully saved in the database.
     */
    fun create(candidateSlot: CandidateSlot): CandidateSlot = candidateSlotRepository.save(candidateSlot)

    /**
     * Update a CandidateSlot object in the database.
     *
     * @param candidateSlot - Updated slot data.
     *
     * @return CandidateSlot - An object that was successfully updated in the database.
     */
    fun update(candidateSlot: CandidateSlot): CandidateSlot = create(candidateSlot)

    /**
     * Return slots of the current Candidate by email.
     *
     * @return List of CandidateSlot - the list of slots of the current candidate.
     */
    fun getAllSlotsByEmail(email: String): List<CandidateSlot> = candidateSlotRepository.findByEmail(email)

    /**
     * Return slots of the current Candidate by email and date.
     *
     * @param date - date on which the database will be searched.
     *
     * @return List of CandidateSlot - Slots that were found in the database by given parameters.
     */
    fun getCandidateSlotsByEmailAndDate(email: String, date: LocalDate): List<CandidateSlot> =
        candidateSlotRepository.findByEmailAndDate(email, date)

    /**
     * Find a CandidateSlot of the current Candidate in the database by id.
     *
     * @param id - The slot number to search for in the database.
     *
     * @throws SlotException if a slot with the given id is not present
     */
    fun getById(id: Long): CandidateSlot = candidateSlotRepository.findById(id)
        .orElseThrow { SlotException(SlotExceptionProfile.CANDIDATE_SLOT_NOT_FOUND) }

    fun getCandidateSlotsByDate(date: LocalDate): Set<CandidateSlot> = candidateSlotRepository.findByDate(date)
}
