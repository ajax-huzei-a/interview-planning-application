package intellistart.interviewplanning.model.user;

import intellistart.interviewplanning.controllers.dto.JwtRequest;
import intellistart.interviewplanning.exceptions.SecurityException;
import intellistart.interviewplanning.exceptions.UserException;
import intellistart.interviewplanning.exceptions.UserException.UserExceptionProfile;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService;
import intellistart.interviewplanning.security.JwtUserDetails;
import intellistart.interviewplanning.security.JwtUserDetailsService;
import intellistart.interviewplanning.utils.FacebookUtil;
import intellistart.interviewplanning.utils.FacebookUtil.FacebookScopes;
import intellistart.interviewplanning.utils.JwtUtil;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import redis.clients.jedis.JedisPooled;

/**
 * Service for User entity.
 */
@Service
public class UserService {

  private final UserRepository userRepository;
  private final InterviewerSlotService interviewerSlotService;
  private final JedisPooled jedis;
  private final FacebookUtil facebookUtil;
  private final JwtUserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;
  @Value("${jwt.caching}")
  private Long jwtValidity;

  /**
  * User Service.
  *
  *
  * @param userRepository userRepository
  * @param interviewerSlotService interviewerSlotService
  * @param jedis jedis
  * @param facebookUtil facebookUtil
  * @param userDetailsService userDetailsService
  * @param jwtUtil userDetailsService
  * @param authenticationManager userDetailsService
  */
  @Autowired
  public UserService(
          UserRepository userRepository,
          InterviewerSlotService interviewerSlotService,
          JedisPooled jedis,
          FacebookUtil facebookUtil,
          JwtUserDetailsService userDetailsService,
          JwtUtil jwtUtil,
          AuthenticationManager authenticationManager
  ) {
    this.userRepository = userRepository;
    this.interviewerSlotService = interviewerSlotService;
    this.jedis = jedis;
    this.facebookUtil = facebookUtil;
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
    this.authenticationManager = authenticationManager;
  }

  /**
   * Method for gaining Optional User by id.
   *
   * @return Optional User by id.
   */
  public User getUserById(Long id) throws UserException {
    return userRepository.findById(id).orElseThrow(() ->
            new UserException(UserException.UserExceptionProfile.INVALID_INTERVIEWER));
  }

  /**
   * Returned the current user by given email.
   *
   * @param email - email on which the database will be searched.
   * @return User - user object with current info.
   */
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * Method for grant the user a role by email.
   *
   * @param email      - email address of the user to whom we will give the role.
   * @param roleOfUser - the role to grant the user.
   * @return User - user to whom we granted the role.
   * @throws UserException - when user already has role.
   */
  public User grantRoleByEmail(String email, Role roleOfUser) throws UserException {
    User user = getUserByEmail(email);
    if (user != null) {
      throw new UserException(UserExceptionProfile.USER_ALREADY_HAS_ROLE);
    }

    user = new User();
    user.setEmail(email);
    user.setRole(roleOfUser);

    return userRepository.save(user);
  }

  /**
   * Method returned the list of users by given role from DB.
   *
   * @param role - role on which the database will be searched.
   * @return List of users by given role.
   */
  public List<User> obtainUsersByRole(Role role) {
    return userRepository.findByRole(role);
  }

  /**
   * Method will return the interviewer whom we will delete.
   * Before deleting, the method checks if the submitted id is really the interviewer.
   * The method also deletes all the interviewer's bookings and slots before deleting.
   *
   * @param id - the interviewer's id to delete.
   * @return User - the deleted user.
   * @throws UserException -
   *                       when the user has not interviewer role or not found by given id.
   */
  public User deleteInterviewer(Long id) throws UserException {
    User user = userRepository.findById(id).orElseThrow(() ->
          new UserException(UserExceptionProfile.USER_NOT_FOUND));

    if (user.getRole() != Role.INTERVIEWER) {
      throw new UserException(UserExceptionProfile.NOT_INTERVIEWER);
    }

    interviewerSlotService.deleteSlotsByUser(user);

    userRepository.delete(user);
    return user;
  }

  /**
   * Method will return the coordinator whom we will delete.
   * Before deleting, the method checks that the coordinator to be deleted is not himself.
   *
   * @param id                      - the coordinator's id to delete.
   * @param currentEmailCoordinator - email of current user.
   * @return User - the deleted user.
   * @throws UserException - when the coordinator removes himself,
   *     not found by given id or the user has not interviewer role.
   */
  public User deleteCoordinator(Long id, String currentEmailCoordinator)
          throws UserException {

    User user = userRepository.findById(id).orElseThrow(() ->
            new UserException(UserExceptionProfile.USER_NOT_FOUND));
    User currentUser = userRepository.findByEmail(currentEmailCoordinator);

    if (user.getRole() != Role.COORDINATOR) {
      throw new UserException(UserExceptionProfile.NOT_COORDINATOR);
    }

    if (user.getId() == currentUser.getId()) {
      throw new UserException(UserExceptionProfile.SELF_REVOKING);
    }

    userRepository.delete(user);
    return user;
  }

  /**
   * Returns JWT by facebook token and caches it if it has not been cached before.
   *
   * @param jwtRequest - contains facebook token
   * @return jwt
   */
  public String getJwtToken(JwtRequest jwtRequest) {
    String fbCached = jedis.get(jwtRequest.getFacebookToken());

    if (fbCached != null) {
      return fbCached;
    }

    Map<FacebookScopes, String> userScopes;
    try {
      userScopes = facebookUtil.getScope(jwtRequest.getFacebookToken());
    } catch (RestClientException e) {
      throw new SecurityException(SecurityException.SecurityExceptionProfile.BAD_FACEBOOK_TOKEN);
    }

    String email = userScopes.get(FacebookScopes.EMAIL);
    String name = userScopes.get(FacebookScopes.NAME);

    authenticate(email);

    final JwtUserDetails userDetails = (JwtUserDetails) userDetailsService
            .loadUserByEmailAndName(email, name);

    String jwt = jwtUtil.generateToken(userDetails);

    jedis.setex(jwtRequest.getFacebookToken(), jwtValidity, jwt);

    return jwt;
  }

  private void authenticate(String username) {
    try {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(username, username));
    } catch (BadCredentialsException e) {
      throw new SecurityException(SecurityException.SecurityExceptionProfile.BAD_CREDENTIALS);
    }
  }
}

