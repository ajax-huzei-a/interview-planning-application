package intellistart.interviewplanning.model.booking;

import org.springframework.data.repository.CrudRepository;

/**
 * DAO for Booking entity.
 */
public interface BookingRepository extends CrudRepository<Booking, Long> {
}
