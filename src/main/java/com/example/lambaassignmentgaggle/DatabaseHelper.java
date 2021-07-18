package com.example.lambaassignmentgaggle;

import org.json.JSONObject;
import org.springframework.data.relational.core.sql.SQL;

import java.sql.*;
import java.util.*;

/**
 * Class that handles database interaction.
 */
public class DatabaseHelper {

    String user;
    String password;
    String dbName;
    String dbms;
    String serverName;
    int portNumber;
    String connProperties;

    public DatabaseHelper() {

    }

    public DatabaseHelper(String user, String password, String dbName, String dbms, String serverName, int portNumber, String connProperties) {
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.dbms = dbms;
        this.serverName = serverName;
        this.portNumber = portNumber;
        this.connProperties = connProperties;
    }

    /**
     *  Establishes connection with mysql database with the fields.
     * @return Connection with database.
     * @throws SQLException when no connection can be established with given params.
     */
    public Connection getConnection() throws SQLException {
        Connection dbConn = null;
        if (dbms.equals("mysql")) {
            dbConn = DriverManager.getConnection(
                    "jdbc:" + dbms + "://" +
                            serverName +
                            ":" + portNumber + "/" +
                            connProperties);
        }

        return dbConn;
    }

    /**
     * Queries database for a Person with id = @id
     * @param conn  Database Connection
     * @param id    id being queried
     * @return  Person object of the result of the query if a person with that id exists, otherwise a null Person object
     * @throws SQLException Exception with database connection or query.
     */
    public Person getPersonById(Connection conn, int id) throws SQLException {
        Person res = null;
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM PERSON WHERE id = "+id + ";");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.isBeforeFirst()) {
                rs.next();
                res = new Person(id,rs.getString("first_name"),rs.getString("last_name"));
            }
        }
        return res;
    }

    /**
     * Gets all Persons from database that match substrings of the query.
     * @param searchInput Search term entered by user
     * @return  An Person arraylist containing no duplicates
     * @throws SQLException Exception due to connection or query.
     */
    public List<Person> getPersonsWithNamesContaining(String searchInput) throws SQLException {
        Connection conn = this.getConnection();
        Person person = null;
        String searchSegment;
        StringTokenizer st = new StringTokenizer(searchInput," ");
        String searchQuery;
        LinkedHashMap<Integer,Person> persons = new LinkedHashMap<>();

        //for each word separated by a space, search in first name and last name and add results to the persons LinkedHashMap
        while(st.hasMoreTokens()) {
            searchSegment = st.nextToken();
            searchQuery = "SELECT * FROM PERSON WHERE PERSON.first_name LIKE '%" + searchSegment + "%';";
            try (PreparedStatement stmt = conn.prepareStatement(searchQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        person = new Person();
                        person.setId(rs.getInt("id"));
                        person.setFirstName(rs.getString("first_name"));
                        person.setLastName(rs.getString("last_name"));
                        persons.put(person.getId(),person);
                    }
                }
            }
            searchQuery = "SELECT * FROM PERSON WHERE PERSON.last_name LIKE '%" + searchSegment + "%';";
            try (PreparedStatement stmt = conn.prepareStatement(searchQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        person = new Person();
                        person.setLastName(rs.getString("last_name"));
                        person.setFirstName(rs.getString("first_name"));
                        person.setId(rs.getInt("id"));
                        persons.put(person.getId(),person);
                    }
                }
            }
        }
        ArrayList <Person> resultsNoDuplicates = new ArrayList<>();
        for(Map.Entry<Integer,Person >  p:persons.entrySet()){
            resultsNoDuplicates.add(p.getValue());
        }
        return resultsNoDuplicates;
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbms() {
        return dbms;
    }

    public void setDbms(String dbms) {
        this.dbms = dbms;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getConnProperties() {
        return connProperties;
    }

    public void setConnProperties(String connProperties) {
        this.connProperties = connProperties;
    }
}
