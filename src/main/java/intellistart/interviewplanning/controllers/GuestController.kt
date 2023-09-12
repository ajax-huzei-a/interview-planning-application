package intellistart.interviewplanning.controllers

import intellistart.interviewplanning.model.week.Week
import intellistart.interviewplanning.model.week.WeekService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for processing requests from unauthorized users.
 */
@RestController
@CrossOrigin
class GuestController(private val weekService: WeekService) {
    @GetMapping("weeks/current")
    fun currentWeek(): Week = weekService.getCurrentWeek()

    @GetMapping("weeks/next")
    fun nextWeek(): Week = weekService.getNextWeek()
}
