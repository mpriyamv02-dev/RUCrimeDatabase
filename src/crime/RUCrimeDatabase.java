package crime;
import edu.rutgers.cs112.LL.LLNode;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class RUCrimeDatabase {
    private LLNode<Incident>[] incidentTable; // Array of LLNodes
    private int totalIncidents;
    private static final double LOAD_FACTOR_THRESHOLD = 4.0;

    public RUCrimeDatabase() {
        incidentTable = new LLNode[10];
        totalIncidents = 0;
    }

    /**
     * Adds a new incident to the hash table
     * @param incident An incident object which we will use to add to the hash table:
     */
    public void addIncident(Incident incident) {
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

    public void join(RUCrimeDatabase other) {
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
    
    public ArrayList<String> topKLocations(int K) {
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

    public HashMap<Category, Double> natureBreakdown() { 
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


    public LLNode<Incident>[] getIncidentTable() {
        return incidentTable;
    }

    public void setIncidentTable(LLNode<Incident>[] incidentTable) {
        this.incidentTable = incidentTable;
    }

    public int numberOfIncidents() {
        return totalIncidents;
    }
 
    private int hashFunction(String incidentNumber) { 
    String last5Digits = 
    incidentNumber.substring(Math.max(0, incidentNumber.length() - 5)); 
    int val = Integer.parseInt(last5Digits) % incidentTable.length; 
     System.out.println("Hashing incident number: " + last5Digits + " val: " + val); 
     return val;
    }
}