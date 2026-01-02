## RU Crime Database

### Problem  
This project analyzes Rutgers University crime incident data using a hash table with separate chaining. The goal is to efficiently store, manage, and query incident records while practicing core data structure concepts.

---

### Dataset  
The data comes from Rutgers University’s Daily Crime & Fire Safety Log and is provided in CSV format. Each row represents a reported incident with information such as incident number, dates, location, and category.

---

### Approach  
- Incidents are stored in a hash table implemented with an array of linked lists  
- Collisions are handled using separate chaining  
- The table resizes automatically when the load factor exceeds a threshold  
- Data is parsed from CSV files into `Incident` objects  

The database supports:
- Adding and deleting incidents  
- Rehashing when the table grows  
- Merging two databases without duplicating incidents  
- Finding the top K locations with the most incidents  
- Computing percentage breakdowns by incident category  

---

### Limitations  
- General locations in `topKLocations` are predefined and dataset-specific  
- The focus is on correctness and data structure usage rather than advanced optimization  

---

### How to Run  
1. Clone the repository  
2. Ensure the CSV files are present  
3. Compile the Java files  
4. Run the `Driver` class  

---

### Why This Project Matters  
This project demonstrates how hash tables and linked lists operate when managing real datasets, emphasizing manual implementation of hashing, collision handling, and traversal rather than relying on built-in abstractions.
