package intellistart.interviewplanning.model.user;

import intellistart.interviewplanning.exceptions.UserException;
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService;
import java.util.List;
import java.util.Optional;

import intellistart.interviewplanning.security.JwtUserDetailsService;
import intellistart.interviewplanning.utils.FacebookUtil;
import intellistart.interviewplanning.utils.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import redis.clients.jedis.JedisPooled;

public class UserServiceTest {

  private static UserRepository userRepository;
  private static InterviewerSlotService interviewerSlotService;
  private static JedisPooled jedis;
  private static FacebookUtil facebookUtil;
  private static JwtUserDetailsService userDetailsService;
  private static JwtUtil jwtUtil;
  private static AuthenticationManager authenticationManager;
  private static UserService cut;
  private static User user1;
  private static User user2;
  private static User user3;
  private static User user4;
  private static User user5;

  @BeforeAll
  static void initialize() {
    userRepository = Mockito.mock(UserRepository.class);
    interviewerSlotService = Mockito.mock(InterviewerSlotService.class);
    jedis = Mockito.mock(JedisPooled.class);
    facebookUtil = Mockito.mock(FacebookUtil.class);
    userDetailsService = Mockito.mock(JwtUserDetailsService.class);
    authenticationManager = Mockito.mock(AuthenticationManager.class);
    jwtUtil = Mockito.mock(JwtUtil.class);
    facebookUtil = Mockito.mock(FacebookUtil.class);
    cut = new UserService(
            userRepository,
            interviewerSlotService,
            jedis,
            facebookUtil,
            userDetailsService,
            jwtUtil,
            authenticationManager
    );

    user1 = new User();
    user1.setId(1L);
    user1.setEmail("test1@mail.com");
    user1.setRole(Role.COORDINATOR);

    user2 = new User();
    user2.setId(2L);
    user2.setEmail("test2@mail.com");
    user2.setRole(Role.INTERVIEWER);

    user3 = new User();
    user3.setEmail("test3@mail.com");
    user3.setRole(Role.COORDINATOR);

    user4 = new User();
    user4.setEmail("test4@mail.com");
    user4.setRole(Role.INTERVIEWER);

    user5 = new User();
    user5.setId(5L);
    user5.setEmail("test5@mail.com");
    user5.setRole(Role.COORDINATOR);
  }

  static Arguments[] getUserByEmailTestArgs(){
    return new Arguments[]{
        Arguments.arguments("test1@mail.com", user1),
        Arguments.arguments("test2@mail.com", user2)
    };
  }
  @ParameterizedTest
  @MethodSource("getUserByEmailTestArgs")
  void getUserByEmailTest(String email, User expected) {
    Mockito.when(userRepository.findByEmail(email)).thenReturn(expected);

    User actual = cut.getUserByEmail(email);
    Assertions.assertEquals(expected, actual);
  }

  static Arguments[] grantRoleByEmailTestArgs(){
    return new Arguments[]{
        Arguments.arguments("test3@mail.com", Role.COORDINATOR, user3),
        Arguments.arguments("test4@mail.com", Role.INTERVIEWER, user4)
    };
  }
  @ParameterizedTest
  @MethodSource("grantRoleByEmailTestArgs")
  void grantRoleByEmailTest(String email, Role role, User expected)
      throws UserException {
    Mockito.when(userRepository.findByEmail(email)).thenReturn(null);
    Mockito.when(userRepository.save(expected)).thenReturn(expected);

    User actual = cut.grantRoleByEmail(email, role);
    Assertions.assertEquals(expected, actual);
  }

  static Arguments[] grantRoleByEmailTestExcArgs(){
    return new Arguments[]{
        Arguments.arguments("test3@mail.com", Role.COORDINATOR, user3),
        Arguments.arguments("test4@mail.com", Role.INTERVIEWER, user4)
    };
  }
  @ParameterizedTest
  @MethodSource("grantRoleByEmailTestExcArgs")
  void grantRoleByEmailTestException(String email, Role role, User user) {
    Mockito.when(userRepository.findByEmail(email)).thenReturn(user);

    Class<UserException> actual = UserException.class;
    Assertions.assertThrows(actual, () -> cut.grantRoleByEmail(email, role));
  }

  static Arguments[] obtainUsersByRoleTestArgs(){
    return new Arguments[]{
        Arguments.arguments(Role.COORDINATOR, List.of(user1)),
        Arguments.arguments(Role.INTERVIEWER, List.of(user2)),
        Arguments.arguments(Role.INTERVIEWER, null)
    };
  }
  @ParameterizedTest
  @MethodSource("obtainUsersByRoleTestArgs")
  void obtainUsersByRoleTest(Role role, List<User> expected) {
    Mockito.when(userRepository.findByRole(role)).thenReturn(expected);

    List<User> actual = cut.obtainUsersByRole(role);
    Assertions.assertEquals(expected, actual);
  }

  static Arguments[] deleteInterviewerTestTestArgs(){
    return new Arguments[]{
        Arguments.arguments(4L, user4),
        Arguments.arguments(2L, user2),
    };
  }
  @ParameterizedTest
  @MethodSource("deleteInterviewerTestTestArgs")
  void deleteInterviewerTest(Long id, User expected)
      throws UserException {
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expected));
    Mockito.doNothing().when(interviewerSlotService).deleteSlotsByUser(expected);

    User actual = cut.deleteInterviewer(id);
    Assertions.assertEquals(expected, actual);
  }

  static Arguments[] deleteInterviewerTestUserNotFoundTestArgs(){
    return new Arguments[]{
        Arguments.arguments(4L),
        Arguments.arguments(2L),
    };
  }
  @ParameterizedTest
  @MethodSource("deleteInterviewerTestUserNotFoundTestArgs")
  void deleteInterviewerUserNotFoundTest(Long id) {
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

    Class<UserException> actual = UserException.class;
    Assertions.assertThrows(actual, () -> cut.deleteInterviewer(id));
  }

  static Arguments[] deleteInterviewerUserAlreadyHasRoleTestTestArgs(){
    return new Arguments[]{
        Arguments.arguments(1L, user1),
        Arguments.arguments(3L, user3),
    };
  }
  @ParameterizedTest
  @MethodSource("deleteInterviewerUserAlreadyHasRoleTestTestArgs")
  void deleteInterviewerUserAlreadyHasRoleTest(Long id, User user) {
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

    Class<UserException> actual = UserException.class;
    Assertions.assertThrows(actual, () -> cut.deleteInterviewer(id));
  }

  static Arguments[] deleteCoordinatorTestArgs(){
    return new Arguments[]{
        Arguments.arguments(4L, "test5@mail.com", user1),
        Arguments.arguments(2L,"test5@mail.com", user3),
    };
  }
  @ParameterizedTest
  @MethodSource("deleteCoordinatorTestArgs")
  void deleteCoordinatorTest(Long id, String email, User expected)
      throws UserException {
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(expected));
    Mockito.when(userRepository.findByEmail(email)).thenReturn(user5);
    Mockito.doNothing().when(interviewerSlotService).deleteSlotsByUser(expected);

    User actual = cut.deleteCoordinator(id, email);
    Assertions.assertEquals(expected, actual);
  }

  static Arguments[] deleteCoordinatorUserNotFoundTestArgs(){
    return new Arguments[]{
        Arguments.arguments(44L, "test5@mail.com"),
        Arguments.arguments(24L, "test5@mail.com"),
    };
  }
  @ParameterizedTest
  @MethodSource("deleteCoordinatorUserNotFoundTestArgs")
  void deleteCoordinatorUserNotFoundTest(Long id, String email) {
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

    Class<UserException> actual = UserException.class;
    Assertions.assertThrows(actual, () -> cut.deleteCoordinator(id, email));
  }

  static Arguments[] deleteCoordinatorSelfRevokingTestArgs(){
    return new Arguments[]{
        Arguments.arguments(1L, "test1@mail.com", user1),
        Arguments.arguments(5L, "test5@mail.com", user5),
    };
  }
  @ParameterizedTest
  @MethodSource("deleteCoordinatorSelfRevokingTestArgs")
  void deleteCoordinatorSelfRevokingTest(Long id, String email, User user) {
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
    Mockito.when(userRepository.findByEmail(email)).thenReturn(user);

    Class<UserException> actual = UserException.class;
    Assertions.assertThrows(actual, () -> cut.deleteCoordinator(id, email));
  }

  static Arguments[] deleteCoordinatorUserHasAnotherRoleTestArgs(){
    return new Arguments[]{
        Arguments.arguments(2L, "test5@mail.com", user2),
        Arguments.arguments(4L, "test5@mail.com", user4),
    };
  }
  @ParameterizedTest
  @MethodSource("deleteCoordinatorUserHasAnotherRoleTestArgs")
  void deleteCoordinatorUserHasAnotherRoleTest(Long id, String email, User user) {
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
    Mockito.when(userRepository.findByEmail(email)).thenReturn(user5);

    Class<UserException> actual = UserException.class;
    Assertions.assertThrows(actual, () -> cut.deleteCoordinator(id, email));
  }
}
