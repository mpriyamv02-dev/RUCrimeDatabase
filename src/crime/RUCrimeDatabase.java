package crime;
import edu.rutgers.cs112.LL.LLNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
/**
 * Students will be analyzing cybercrime incident data using hash tables (with separate chaining) 
 * and linked lists to organize and query crime records efficiently. The theme revolves around 
 * parsing, storing, and analyzing real-world crime logs from Rutgers University’s public 
 * safety database: Daily Crime & Fire Safety Log.
 * 
 * @author Anna Lu
 * @author Krish Lenka
 */
public class RUCrimeDatabase {
    private LLNode<Incident>[] incidentTable; // Array of LLNodes
    private int totalIncidents;
    private static final double LOAD_FACTOR_THRESHOLD = 4.0;

    /**
     * Default constructor initializes the hash table with a size of 10.
     * The total number of incidents is set to zero.
     */
    public RUCrimeDatabase() {
        incidentTable = new LLNode[10];
        totalIncidents = 0;
    }

    /**
     * Adds a new incident to the hash table
     * @param incident An incident object which we will use to add to the hash table:
     */
    public void addIncident(Incident incident) {
        // WRITE YOUR CODE HERE
        if (incident == null) return;

        String key = incident.getIncidentNumber();
        int index = hashFunction(key);

        LLNode<Incident> newNode = new LLNode<>(incident);
        newNode.setNext(incidentTable[index]);
        incidentTable[index] = newNode;

        totalIncidents++;

        double loadFactor = (double) totalIncidents / incidentTable.length;
        if (loadFactor >= LOAD_FACTOR_THRESHOLD) {
            rehash();
        }

    }

    /**
     * Reads the csv file, creates an Incident object for each line, and calls addIncident().
     * @param filename Path to file containing incident data
     */
    public void buildIncidentTable(String inputfile) {
        // WRITE YOUR CODE HERE
        LLNode<Incident>[] newTable = new LLNode[incidentTable.length];
        incidentTable = newTable;
        totalIncidents = 0;

        StdIn.setFile(inputfile);

        while (!StdIn.isEmpty()) {
            String line = StdIn.readLine();
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",");

            if (parts.length < 7) {
                continue;
            }

            String incidentNumber   = parts[0].trim();
            String nature           = parts[1].trim();
            String reportDate       = parts[2].trim();
            String occurrenceDate   = parts[3].trim();
            String location         = parts[4].trim();
            String disposition      = parts[5].trim();
            String generalLocation  = parts[6].trim();

            Category category = Category.fromString(nature);

            Incident incident = new Incident(
                    incidentNumber,
                    nature,
                    reportDate,
                    occurrenceDate,
                    location,
                    disposition,
                    generalLocation,
                    category
            );

            addIncident(incident);
        }
        
    }
 

    /**
     * Rehashes the incident groups in the hash table.
     * This is called when the load factor exceeds a certain threshold.
     */
    public void rehash() {
        // WRITE YOUR CODE HERE
        LLNode<Incident>[] oldTable = incidentTable;

    incidentTable = new LLNode[oldTable.length * 2];

    totalIncidents = 0;

    for (int i = 0; i < oldTable.length; i++) {
        LLNode<Incident> curr = oldTable[i];
        while (curr != null) {
            addIncident(curr.getData());
            curr = curr.getNext();
        }
    }
    } 

    /**
     * Deletes an incident based on its incident number.
     * @param incidentNumber The incident number of the incident to delete 
     */
    public void deleteIncident(String incidentNumber) {
        // WRITE YOUR CODE HERE
        if (incidentNumber == null) return;
        int index = hashFunction(incidentNumber);

    LLNode<Incident> curr = incidentTable[index];
    LLNode<Incident> prev = null;

    while (curr != null) {
        Incident inc = curr.getData();

        if (inc.getIncidentNumber().equals(incidentNumber)) {
            if (prev == null) {
                incidentTable[index] = curr.getNext();
            } else {
                prev.setNext(curr.getNext());
            }

            totalIncidents--;
            return;
        }

        prev = curr;
        curr = curr.getNext();
    }
    }

    /**
     * Iterates over another RUCrimeDatabase's incident table, and adds its incidents
     * to this table IF they do not already exist.
     * @param other RUCrimeDatabase to copy new incidents from
     */
    public void join(RUCrimeDatabase other) {
        // WRITE YOUR CODE HERE
        if (other == null) return;

    LLNode<Incident>[] otherTable = other.getIncidentTable();

    for (int i = 0; i < otherTable.length; i++) {
        LLNode<Incident> curr = otherTable[i];

        while (curr != null) {
            Incident inc = curr.getData();
            String num = inc.getIncidentNumber();

            boolean exists = false;
            int idx = hashFunction(num);
            LLNode<Incident> scan = incidentTable[idx];

            while (scan != null) {
                if (scan.getData().getIncidentNumber().equals(num)) {
                    exists = true;
                    break;
                }
                scan = scan.getNext();
            }

            if (!exists) {
                addIncident(inc);
            }

            curr = curr.getNext();
        }
    }
    }
    
    /**
     * Returns a list of the top K locations with the most incidents 
     * If K > numLocations, return all locations
     * @return ArrayList<String> containing the top K locations
     */
    public ArrayList<String> topKLocations(int K) {
        // WRITE YOUR CODE HERE
         String[] locations = {
        "ACADEMIC",
        "CAMPUS SERVICES",
        "OTHER",
        "PARKING LOT",
        "RECREATION",
        "RESIDENTIAL",
        "STREET/ROADWAY"
    };

    int[] counts = new int[locations.length];

    for (int i = 0; i < incidentTable.length; i++) {
        LLNode<Incident> curr = incidentTable[i];

        while (curr != null) {
            String gl = curr.getData().getGeneralLocation();

            
            for (int j = 0; j < locations.length; j++) {
                if (gl.equals(locations[j])) {
                    counts[j]++;
                    break;
                }
            }

            curr = curr.getNext();
        }
    }
    ArrayList<String> result = new ArrayList<>();
    boolean[] taken = new boolean[locations.length];

    int limit = K;
    if (limit > locations.length) {
        limit = locations.length;
    }

    for (int m = 0; m < limit; m++) {
        int maxIndex = -1;
        int maxCount = -1;
        for (int j = 0; j < counts.length; j++) {
            if (!taken[j] && counts[j] > maxCount) {
                maxCount = counts[j];
                maxIndex = j;
            }
        }

        if (maxIndex == -1 || maxCount == 0) {
            break;
        }

        taken[maxIndex] = true;
        result.add(locations[maxIndex]);
    }

    return result;
    }  

    /**
     * Returns the percentage of incidents for every category.
     * Categories: Property, Violent,
     *             Mischief, Trespass, or Other
     * @return A HashMap<Category, Double> with percentage of incidents of each category
     */
    public HashMap<Category, Double> natureBreakdown() { 
        // WRITE YOUR CODE HERE
        HashMap<Category, Double> map = new HashMap<>();

    for (Category c : Category.values()) {
        map.put(c, 0.0);
    }

    for (int i = 0; i < incidentTable.length; i++) {
        LLNode<Incident> curr = incidentTable[i];

        while (curr != null) {
            Category cat = curr.getData().getCategory();
            map.put(cat, map.get(cat) + 1);  
            curr = curr.getNext();
        }
    }
    if (totalIncidents > 0) {
        for (Category c : Category.values()) {
            double count = map.get(c);
            double percent = (count / totalIncidents) * 100.0;
            map.put(c, percent);
        }
    }

    return map;

        }

    //Given methods
    /**
     * DO NOT MODIFY THIS METHOD.
     * Returns the hash table array for inspection/testing
     * @return The array of LLNode<IncidentGroup> representing the hash table
     */
    public LLNode<Incident>[] getIncidentTable() {
        return incidentTable;
    }

    public void setIncidentTable(LLNode<Incident>[] incidentTable) {
        this.incidentTable = incidentTable;
    }

    public int numberOfIncidents() {
        return totalIncidents;
    }

    /**
     * DO NOT MODIFY THIS METHOD.
     * Returns the index in the hash table for a given incident number.
     * @return The index in the hash table for the incident number
     * @param incidentNumber The incident number to hash
     */
    private int hashFunction(String incidentNumber) {
        String last5Digits = incidentNumber.substring(Math.max(0, incidentNumber.length() - 5));
        int val = Integer.parseInt(last5Digits) % incidentTable.length;
        //System.out.println("Hashing incident number: " + last5Digits + " val: " + val);
        return val;
    }

}