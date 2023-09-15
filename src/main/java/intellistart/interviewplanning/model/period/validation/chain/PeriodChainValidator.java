package intellistart.interviewplanning.model.period.validation.chain;

import java.time.LocalTime;

/**
 * Single business logic validator.
 */
public interface PeriodChainValidator {

  boolean isCorrect(LocalTime lowerBoundary, LocalTime upperBoundary);
}
