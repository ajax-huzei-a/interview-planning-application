package intellistart.interviewplanning.model.week

import intellistart.interviewplanning.model.dayofweek.DayOfWeek
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.WeekFields

/**
 * Service for Week entity.
 */
@Service
class WeekService(private val weekRepository: WeekRepository) {

    /**
     * Get date and convert it to number of week.
     *
     * @param date any date
     * @return number of week
     */
    fun getNumberOfWeek(date: LocalDate): Long {
        val sumOfWeeks: Long = if (date.year != START_YEAR) {
            (START_YEAR until date.year).sumOf {
                date.withYear(it).range(WeekFields.ISO.weekOfYear()).maximum.toInt()
            }.toLong()
        } else { 0 }

        val weeksOfCurrentYear = date.get(WeekFields.ISO.weekOfYear())
        if (checkBeginOfYear(date.year)) {
            return sumOfWeeks + weeksOfCurrentYear - 1
        }
        return sumOfWeeks + weeksOfCurrentYear
    }

    /**
     * Method checks if the first day of year is Tuesday, Wednesday, or Thursday
     * for right calculating of the number of the week.
     *
     * @param year current year
     * @return true if the year begins from Tuesday, Wednesday, or Thursday
     */
    private fun checkBeginOfYear(year: Int): Boolean {
        val date = LocalDate.of(year, 1, 1)
        return date.dayOfWeek in setOf(
            java.time.DayOfWeek.TUESDAY,
            java.time.DayOfWeek.WEDNESDAY,
            java.time.DayOfWeek.THURSDAY
        )
    }

    /**
     * Get date and convert it to day of the week.
     *
     * @param date any date
     * @return day of the week
     */
    fun getDayOfWeek(date: LocalDate): DayOfWeek {
        val dayOfWeek = date.dayOfWeek.toString().substring(0, NUM_OF_LETTERS__OF_DAY_CODE)
        return DayOfWeek.valueOf(dayOfWeek)
    }

    /**
     * Get number of the week and day of the week
     * and convert them to date (LocalDate).
     *
     * @param weekNum number of the week
     * @param dayOfWeek day of the week
     * @return date
     */
    fun convertToLocalDate(weekNum: Long, dayOfWeek: DayOfWeek): LocalDate {
        return LocalDate.now()
            .with(WeekFields.ISO.weekBasedYear(), getYear(weekNum).toLong())
            .with(WeekFields.ISO.weekOfYear(), getWeek(weekNum))
            .with(WeekFields.ISO.dayOfWeek(), dayOfWeek.ordinal.toLong() + 1)
    }

    /**
     * Get number of the week and return the current year.
     *
     * @param weekNum number of the week from 2022
     * @return current year
     */
    private fun getYear(weekNum: Long): Int {
        val date = LocalDate.parse("2022-01-01")
        val currentDate = date.plusDays(weekNum * NUM_OF_DAYS_OF_WEEK)
        return currentDate.year
    }

    /**
     * Get number of the week and calculate the number of the week from the current year.
     *
     * @param weekNum number of the week from 2022
     * @return number of the week from the current year
     */
    private fun getWeek(weekNum: Long): Long {
        val date = LocalDate.parse("2022-01-01")
        val year = getYear(weekNum)
        var remainingWeeks = weekNum
        for (i in START_YEAR until year) {
            remainingWeeks -= date.withYear(i).range(WeekFields.ISO.weekOfYear()).maximum
        }
        return remainingWeeks
    }

    /**
     * Return the Week object for the request for getting the number of the current week.
     *
     * @return Week object
     */
    fun getCurrentWeek(): Week {
        val date = LocalDate.now()
        return getWeekByWeekNum(getNumberOfWeek(date))
    }

    /**
     * Return the Week object for the request for getting the number of the next week.
     *
     * @return Week object
     */
    fun getNextWeek(): Week {
        val date = LocalDate.now()
        return getWeekByWeekNum(getNumberOfWeek(date) + 1)
    }

    /**
     * Get the number of the week and check if the Week object with id weekNum exists.
     * If it exists, return this object; if not, create an object with such id and return it.
     *
     * @param weekNum number of the week
     * @return Week object
     */
    fun getWeekByWeekNum(weekNum: Long): Week {
        val week = weekRepository.findById(weekNum)
        return week.orElseGet { createWeek(weekNum) }
    }

    /**
     * Create a Week object with id weekNum and return it.
     *
     * @param weekNum number of the week
     * @return Week object
     */
    private fun createWeek(weekNum: Long): Week = weekRepository.save(Week(weekNum, HashSet()))

    companion object {
        private const val START_YEAR = 2022
        private const val NUM_OF_DAYS_OF_WEEK = 7
        private const val NUM_OF_LETTERS__OF_DAY_CODE = 3
    }
}
