package ua.epam.spring.hometask.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Yuriy_Tkach
 */
@Getter
@Setter
public class Event extends DomainObject {

    public static final double VIP_PRICE_COEFFICIENT = 2;
    private static final Map<EventRating, Double> ratingCoefficient;

    static {
        ratingCoefficient = new HashMap<>();
        ratingCoefficient.put(EventRating.HIGH, 1.2);
        ratingCoefficient.put(EventRating.MID, 1.0);
        ratingCoefficient.put(EventRating.LOW, 0.8);
    }

    private String name;

    private NavigableSet<LocalDateTime> airDates = new TreeSet<>();

    private double basePrice;

    private EventRating rating;

    private NavigableMap<LocalDateTime, Auditorium> auditoriums = new TreeMap<>();

    /**
     * Checks if event is aired on particular <code>dateTime</code> and assigns
     * auditorium to it.
     * 
     * @param dateTime
     *            Date and time of aired event for which to assign
     * @param auditorium
     *            Auditorium that should be assigned
     * @return <code>true</code> if successful, <code>false</code> if event is
     *         not aired on that date
     */
    public boolean assignAuditorium(LocalDateTime dateTime, Auditorium auditorium) {
        if (airDates.contains(dateTime)) {
            auditoriums.put(dateTime, auditorium);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes auditorium assignment from event
     * 
     * @param dateTime
     *            Date and time to remove auditorium for
     * @return <code>true</code> if successful, <code>false</code> if not
     *         removed
     */
    public boolean removeAuditoriumAssignment(LocalDateTime dateTime) {
        return auditoriums.remove(dateTime) != null;
    }

    /**
     * Add date and time of event air
     * 
     * @param dateTime
     *            Date and time to add
     * @return <code>true</code> if successful, <code>false</code> if already
     *         there
     */
    public boolean addAirDateTime(LocalDateTime dateTime) {
        return airDates.add(dateTime);
    }

    /**
     * Adding date and time of event air and assigning auditorium to that
     * 
     * @param dateTime
     *            Date and time to add
     * @param auditorium
     *            Auditorium to add if success in date time add
     * @return <code>true</code> if successful, <code>false</code> if already
     *         there
     */
    public boolean addAirDateTime(LocalDateTime dateTime, Auditorium auditorium) {
        boolean result = airDates.add(dateTime);
        if (result) {
            auditoriums.put(dateTime, auditorium);
        }
        return result;
    }

    /**
     * Removes the date and time of event air. If auditorium was assigned to
     * that date and time - the assignment is also removed
     * 
     * @param dateTime
     *            Date and time to remove
     * @return <code>true</code> if successful, <code>false</code> if not there
     */
    public boolean removeAirDateTime(LocalDateTime dateTime) {
        boolean result = airDates.remove(dateTime);
        if (result) {
            auditoriums.remove(dateTime);
        }
        return result;
    }

    /**
     * Checks if event airs on particular date and time
     * 
     * @param dateTime
     *            Date and time to check
     * @return <code>true</code> event airs on that date and time
     */
    public boolean airsOnDateTime(LocalDateTime dateTime) {
        return airDates.stream().anyMatch(dt -> dt.equals(dateTime));
    }

    /**
     * Checks if event airs on particular date
     * 
     * @param date
     *            Date to ckeck
     * @return <code>true</code> event airs on that date
     */
    public boolean airsOnDate(LocalDate date) {
        return airDates.stream().anyMatch(dt -> dt.toLocalDate().equals(date));
    }

    /**
     * Checking if event airs on dates between <code>from</code> and
     * <code>to</code> inclusive
     * 
     * @param from
     *            Start date to check
     * @param to
     *            End date to check
     * @return <code>true</code> event airs on dates
     */
    public boolean airsOnDates(LocalDate from, LocalDate to) {
        return airDates.stream()
                .anyMatch(dt -> dt.toLocalDate().compareTo(from) >= 0 && dt.toLocalDate().compareTo(to) <= 0);
    }

    public boolean airsAfterDate(LocalDateTime from) {
        return airDates.stream()
                .anyMatch(dt -> dt.compareTo(from) >= 0);
    }

    public boolean airsOnDateTimes(LocalDateTime from, LocalDateTime to) {
        return airDates.stream()
                .anyMatch(dt -> dt.compareTo(from) >= 0 && dt.compareTo(to) <= 0);
    }

    public Optional<Double> getSeatPrice(Long seatNumber, LocalDateTime dateTime){
        Auditorium auditorium = getAuditoriums().get(dateTime);
        Double seatPrice = basePrice;
        if(auditorium.getVipSeats().contains(seatNumber)){
            seatPrice = basePrice*VIP_PRICE_COEFFICIENT;
        } else if (auditorium.getAllSeats().contains(seatNumber)) {
            seatPrice = basePrice;
        } else {return Optional.empty();}

        return Optional.of(seatPrice*ratingCoefficient.get(getRating()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Event other = (Event) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
