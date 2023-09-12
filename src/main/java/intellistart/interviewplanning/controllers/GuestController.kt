package intellistart.interviewplanning.controllers;

import intellistart.interviewplanning.model.week.Week;
import intellistart.interviewplanning.model.week.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for processing requests from unauthorized users.
 */
@RestController
@CrossOrigin
public class GuestController {

  private final WeekService weekService;

  @Autowired
  public GuestController(WeekService weekService) {
    this.weekService = weekService;
  }

  @GetMapping("weeks/current")
  public Week currentWeek() {
    return weekService.getCurrentWeek();
  }

  @GetMapping("weeks/next")
  public Week nextWeek() {
    return weekService.getNextWeek();
  }
}
