package intellistart.interviewplanning.model.booking;

import intellistart.interviewplanning.exceptions.BookingException;
import intellistart.interviewplanning.exceptions.BookingException.BookingExceptionProfile;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for Booking entity.
 */
@Service
public class BookingService {

  private final BookingRepository bookingRepository;

  /**
   * Constructor.
   */
  @Autowired
  public BookingService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  /**
   * Finds all Booking objects in DB and returns Set of them.
   *
   * @return Set&lt;Booking&gt; set with all Booking objects from the DB.
   */
  public Set<Booking> findAll() {
    return (Set<Booking>) bookingRepository.findAll();
  }

  /**
   * Find Booking by id from repository.
   *
   * @throws BookingException if no booking with given id
   */
  public Booking findById(Long id) throws BookingException {
    return bookingRepository.findById(id).orElseThrow(() -> new BookingException(
        BookingExceptionProfile.BOOKING_NOT_FOUND));
  }

  /**
   * Alias for method in {@link BookingRepository}.
   */
  public Booking save(Booking booking) {
    Booking bookingSaved = bookingRepository.save(booking);
    return bookingSaved;
  }

  /**
   * Delete the given bookings from DB.
   *
   * @param bookings - bookings that need to be removed from the database.
   */
  public void deleteBookings(Set<Booking> bookings) {
    bookingRepository.deleteAll(bookings);
  }

  /**
   * Delete the given booking from DB.
   *
   * @param booking - booking that needed to be removed from the database.
   */
  public void deleteBooking(Booking booking) {
    bookingRepository.delete(booking);
  }
}
