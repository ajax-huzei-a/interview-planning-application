package intellistart.interviewplanning.controllers;

import intellistart.interviewplanning.controllers.dto.CandidateSlotDto;
import intellistart.interviewplanning.controllers.dto.CandidateSlotsDto;
import intellistart.interviewplanning.exceptions.SlotException;
import intellistart.interviewplanning.model.candidateslot.CandidateSlot;
import intellistart.interviewplanning.model.candidateslot.CandidateSlotService;
import intellistart.interviewplanning.model.candidateslot.validation.CandidateSlotValidator;
import intellistart.interviewplanning.security.JwtUserDetails;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for processing requests from Candidate.
 */
@RestController
@CrossOrigin
public class CandidateController {

  private final CandidateSlotService candidateSlotService;
  private final CandidateSlotValidator candidateSlotValidator;

  @Autowired
  public CandidateController(CandidateSlotService candidateSlotService,
      CandidateSlotValidator candidateSlotValidator) {
    this.candidateSlotService = candidateSlotService;
    this.candidateSlotValidator = candidateSlotValidator;
  }

  /**
   * POST request for adding a new CandidateSlot.
   * First we do the conversion, then we pass it to the validation,
   * and then we send it to the service for saving.
   *
   * @param request - Request body of POST mapping.
   *
   * @return ResponseEntity - Response of the saved object converted to a DTO.
   *
   * @throws SlotException - when parameters are incorrect or slot is overlapping.
   */
  @PostMapping("/candidates/current/slots")
  public ResponseEntity<CandidateSlotDto> createCandidateSlot(@RequestBody CandidateSlotDto request,
      Authentication authentication)
      throws SlotException {
    CandidateSlot candidateSlot = getCandidateSlotFromDto(request, authentication);
    candidateSlotValidator.validateCreating(candidateSlot);

    candidateSlot = candidateSlotService.create(candidateSlot);

    return ResponseEntity.ok(new CandidateSlotDto(candidateSlot));
  }

  /**
   * POST request for editing the CandidateSlot.
   * First we do the conversion, then we pass it to the validation,
   * and then we send it to the service for updating.
   *
   * @param request - Request body of POST mapping.
   * @param id - Parameter from the request mapping. This is the slot id for update.
   *
   * @return ResponseEntity - Response of the updated object converted to a DTO.
   *
   * @throws SlotException - when parameters are incorrect or updated slot is booked
   *     or slot is overlapping.
   */
  @PostMapping("/candidates/current/slots/{slotId}")
  public ResponseEntity<CandidateSlotDto> updateCandidateSlot(@RequestBody CandidateSlotDto request,
      @PathVariable("slotId") Long id, Authentication authentication)
      throws SlotException {
    CandidateSlot candidateSlot = getCandidateSlotFromDto(request, authentication);
    candidateSlot.setId(id);

    candidateSlotValidator.validateUpdating(candidateSlot);

    candidateSlot = candidateSlotService.update(candidateSlot);

    return ResponseEntity.ok(new CandidateSlotDto(candidateSlot));
  }

  /**
   * GET request for getting all slots of current Candidate.
   *
   * @return ResponseEntity - Response of the list of slots converted to a DTO.
   */
  @GetMapping("/candidates/current/slots")
  public ResponseEntity<CandidateSlotsDto> getAllSlotsOfCandidate(Authentication authentication) {

    JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
    
    List<CandidateSlot> candidateSlots = candidateSlotService
        .getAllSlotsByEmail(jwtUserDetails.getEmail());

    return ResponseEntity.ok(getCandidateSlotsDtoFromListOf(candidateSlots));
  }

  /**
   * Converts the candidate slot from the DTO.
   *
   * @param candidateSlotDto - DTO of Candidate slot.
   *
   * @return CandidateSlot object by given DTO.
   */
  private CandidateSlot getCandidateSlotFromDto(CandidateSlotDto candidateSlotDto,
      Authentication authentication) throws SlotException {

    JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();

    return candidateSlotService.createCandidateSlot(candidateSlotDto.getDate(),
        candidateSlotDto.getFrom(), candidateSlotDto.getTo(), jwtUserDetails.getEmail(),
        jwtUserDetails.getName());
  }

  /**
   * Created the list of CandidateSlotDto object by given list of CandidateSlot.
   *
   * @param candidateSlotList - List of candidates to convert.
   *
   * @return candidateSlotsDto - List of DTO by given list of CandidateSlot.
   */
  private CandidateSlotsDto getCandidateSlotsDtoFromListOf(List<CandidateSlot> candidateSlotList) {
    List<CandidateSlotDto> candidateSlotDtoList = candidateSlotList
        .stream()
        .map(CandidateSlotDto::new)
        .collect(Collectors.toList());

    CandidateSlotsDto candidateSlotsDto = new CandidateSlotsDto();
    candidateSlotsDto.setCandidateSlotDtoList(candidateSlotDtoList);

    return candidateSlotsDto;
  }
}
