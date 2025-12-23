package crime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import edu.rutgers.cs112.Comparable112; 

/**
 * Represents an incident with details about a reported event.
 * Extends Comparable112 to enable comparison between incidents.
 * @author Anna Lu
 * @author Krish Lenka
 */
public class Incident extends Comparable112<Incident>{

    private String incidentNumber;
    private String nature;
    private LocalDateTime reportDate;
    private String occurrenceDate;
    private String location;
    private String disposition;
    private String generalLocation;
    private Category category;

     /**
     * Constructs a new Incident with all required details.
     * @param incidentNumber Unique identifier for the incident
     * @param nature Type/category of the incident
     * @param reportDate Date when incident was reported 
     * @param occurrenceDate Date when incident (likely have) occurred 
     * @param location Where the incident occurred
     * @param disposition Current status of the incident
     * @param generalLocation Category or general area of the incident location
     */
    public Incident(String incidentNumber, String nature, String reportDate, String occurrenceDate, String location, String disposition, String generalLocation, Category category) {
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
            .appendPattern("MM/dd/")
            .appendValueReduced(ChronoField.YEAR, 2, 2, 2000)    
            .appendPattern(" HHmm'Hrs'")
            .toFormatter();
        this.incidentNumber = incidentNumber;
        this.nature = nature;
        this.reportDate = LocalDateTime.parse(reportDate, fmt);
        this.occurrenceDate = occurrenceDate;
        this.location = location;
        this.disposition = disposition;
        this.generalLocation = generalLocation;
        this.category = category;
    }

    /**
     * Checks if two incidents are equal based on incident number.
     * @param o The object to compare with
     * @return true if incidents have same incident number, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Incident)) return false;
        Incident incident = (Incident) o;
        return incidentNumber.equals(incident.incidentNumber) && nature.equals(incident.nature) 
            && reportDate.equals(incident.reportDate) && occurrenceDate.equals(incident.occurrenceDate)
            && location.equals(incident.location) && disposition.equals(incident.disposition)
            && generalLocation.equals(incident.generalLocation);
    }

    /**
     * Compares this incident with another based on incident number.
     * @param o The other incident to compare with
     * @return Negative if this comes before, positive if after, 0 if equal
     */
    @Override
    public int compareTo(Incident o) {
        Incident other = (Incident) o;
        return this.incidentNumber.compareTo(other.incidentNumber);
    }

    /**
     * Returns a string representation of the incident.
     * @return Formatted string with incident number and nature
     */
    @Override
    public String toString() {
        return "Incident #" + incidentNumber + " (" + nature + ")";
    }
    
    //Getter methods
    public String getIncidentNumber() {
        return incidentNumber;
    }
    public String getNature() {
        return nature;
    }
    public LocalDateTime getReportDate() {
        return reportDate;
    }
    public String getOccurrenceDate() {
        return occurrenceDate;
    }
    public String getLocation() {
        return location;
    }
    public String getDisposition() {
        return disposition;
    }
    public String getGeneralLocation() {
        return generalLocation;
    }
    public Category getCategory() {
        return category;
    }

}
