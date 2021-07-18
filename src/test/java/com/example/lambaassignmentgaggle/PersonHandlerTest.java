package com.example.lambaassignmentgaggle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.amazonaws.services.lambda.runtime.Context;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;;

public class PersonHandlerTest {
    DatabaseHelper db;
    Connection conn;
    PreparedStatement preparedStatement ;
    ResultSet resultSet;
    @BeforeEach
    public void setup() {
        db = mock(DatabaseHelper.class);
        conn = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
    }

    @Test
    public void When_getPersonById_Expect_JsonString() throws SQLException, JSONException {
        when(db.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(db.getPersonById(any(Connection.class),anyInt())).thenReturn(new Person(2, "Bruce", "Wayne"));


        PersonRequestHandler prh = new PersonRequestHandler();
        JSONObject testRequest = new JSONObject();
        testRequest.put("type","id");
        testRequest.put("input","2");
        String val = testRequest.toString();
        Context contextMock = mock(Context.class);
        String result =prh.processJsonReq(testRequest.toString(),db);
        JSONObject resultJson = new JSONObject(result);
        assertNotNull(resultJson.getString("persons"));
        JSONArray arr = resultJson.getJSONArray("persons");
       assertEquals(2, arr.getJSONObject(0).getInt("id"));
        assertEquals("Bruce", arr.getJSONObject(0).getString("firstName"));
        assertEquals("Wayne", arr.getJSONObject(0).getString("lastName"));

    }
    @Test
    public void When_getPersonsWithNamesContaining_Expect_JsonArrayString() throws SQLException, JSONException {
        when(db.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        ArrayList<Person> personsRes = new ArrayList<>();
        personsRes.add(new Person(2,"Bruce","Wayne"));
        personsRes.add(new Person(3,"Bruce","Doe"));
        personsRes.add(new Person(1,"Bruce","Smith"));
        when(db.getPersonsWithNamesContaining(anyString())).thenReturn(personsRes);

        PersonRequestHandler prh = new PersonRequestHandler();
        JSONObject testRequest = new JSONObject();
        testRequest.put("type","search");
        testRequest.put("input","Bruce");
        String val = testRequest.toString();
        Context contextMock = mock(Context.class);
        String result =prh.processJsonReq(testRequest.toString(),db);
        JSONObject resultJson = new JSONObject(result);
        JSONArray arr = resultJson.getJSONArray("persons");
        assertEquals(2, arr.getJSONObject(0).getInt("id"));
        assertEquals("Bruce", arr.getJSONObject(0).getString("firstName"));
        assertEquals("Wayne", arr.getJSONObject(0).getString("lastName"));

        assertEquals(3, arr.getJSONObject(0).getInt("id"));
        assertEquals("Bruce", arr.getJSONObject(0).getString("firstName"));
        assertEquals("Doe", arr.getJSONObject(0).getString("lastName"));

        assertEquals(1, arr.getJSONObject(0).getInt("id"));
        assertEquals("Bruce", arr.getJSONObject(0).getString("firstName"));
        assertEquals("Smith", arr.getJSONObject(0).getString("lastName"));

        assertEquals("{\"persons\":[{\"firstName\":\"Bruce\",\"lastName\":\"Wayne\",\"id\":2},{\"firstName\":\"Bruce\",\"lastName\":\"Doe\",\"id\":3},{\"firstName\":\"Bruce\",\"lastName\":\"Smith\",\"id\":1}]}",result);

    }
}
