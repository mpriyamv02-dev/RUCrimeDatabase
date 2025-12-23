package test;

import static org.junit.Assert.*;
import org.junit.*;

import crime.RUCrimeDatabase;


/*
* This is a JUnit Test Class, which uses the JUnit package to create
* and run tests (just like in Lab 2)
* 
* You can use this to evaluate your code. Examine these tests, as writing
* similar test cases will help you immensly on other Assignments/Labs, as
* well as moving forward in your CS career.
*
* You WILL NOT SUBMIT THIS CLASS. This is for your own testing purposes only.
*/
public class RUCrimesDatabaseTest {
    

    @Test
    public void testAddIncident(){
        RUCrimeDatabase db = new RUCrimeDatabase();

        // make incident objects and call db.addIncident() with them      
        // use db.getIncidentTable() to get the reference to the incident table
        // use assert statements to verify correctness

        fail("Test not implemented"); // remove this once you write the test
    }

    @Test
    public void testBuildIncidentTable(){
        RUCrimeDatabase db = new RUCrimeDatabase();

        // call db.buildIncidentTable() with a csv file
        // use db.getIncidentTable() to get the reference to the incident table
        // use assert statements to verify correctness

        fail("Test not implemented"); // remove this once you write the test
    }

    @Test
    public void testRehash(){
        RUCrimeDatabase db = new RUCrimeDatabase();

        // add some incidents to the hash table, then call db.rehash()
        // use db.getIncidentTable() to get the reference to the incident table, and check that things are in the correct index
        // you could also call db.buildIncidentTable() with a csv file that causes a rehash, like May
        // use assert statements to verify correctness

        fail("Test not implemented"); // remove this once you write the test
    }

    @Test
    public void testdeleteIncident(){
        RUCrimeDatabase db = new RUCrimeDatabase();

        // call db.buildIncidentTable() 
        // call db.deleteIncident() with an incident number
        // use db.getIncidentTable() to get the reference to the incident table
        // use assert statements to verify correctness

        fail("Test not implemented"); // remove this once you write the test
    }

    @Test
    public void testJoin(){
        RUCrimeDatabase db = new RUCrimeDatabase();
        RUCrimeDatabase db2 = new RUCrimeDatabase();

        // call db.buildIncidentTable() on both db and db2 with different csv files, use the Combined csv to best test this
        // call db.join() on db, passing in db2
        // use db.getIncidentTable() to get the reference to the incident table
        // use assert statements to verify correctness

        fail("Test not implemented"); // remove this once you write the test
    }

    @Test
    public void testTopKLocations(){
        RUCrimeDatabase db = new RUCrimeDatabase();

        // call db.buildIncidentTable() 
        // call db.topKLocations() with an int for K
        // loop through the ArrayList that is returned and 
        // use assert statements to verify correctness

        fail("Test not implemented"); // remove this once you write the test
    }

    @Test
    public void testNatureBreakdown(){
        RUCrimeDatabase db = new RUCrimeDatabase();

        // call db.buildIncidentTable() 
        // call db.natureBreakdown() 
        // check the values in the returned HashMap for each category  
        // use assert statements to verify correctness

        fail("Test not implemented"); // remove this once you write the test
    }

}
