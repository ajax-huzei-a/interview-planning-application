package intellistart.interviewplanning.controllers.dto;

import java.util.HashMap;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DashboardDtoTest {

  private static final DashboardDto expected = new DashboardDto(new HashSet<>(), new HashSet<>(), new HashMap<>());

  @Test
  void initializeDashboard() {

    DashboardDto actual = new DashboardDto(new HashSet<>(), new HashSet<>(), new HashMap<>());
    Assertions.assertEquals(expected, actual);
  }
}
