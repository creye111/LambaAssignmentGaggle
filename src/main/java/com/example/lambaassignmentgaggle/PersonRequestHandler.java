package com.example.lambaassignmentgaggle;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class PersonRequestHandler implements RequestHandler<String, String> {

    @Override
    public String handleRequest(String x, Context context) {
        //If the json request is asking to get a person by an id


        DatabaseHelper db = new DatabaseHelper(
                System.getenv("AWSLAMBDA_GAGGLE_USER"),
                System.getenv("AWSLAMBDA_GAGGLE_PASS"),
                System.getenv("AWSLAMBDA_GAGGLE_DB_NAME"),
                System.getenv("AWSLAMBDA_GAGGLE_DBMS"),
                System.getenv("AWSLAMBDA_GAGGLE_SERVER_NAME"),
                Integer.getInteger(System.getenv("AWSLAMBDA_GAGGLE_DB_PORT")),
                System.getenv("AWSLAMBDA_GAGGLE_DB_CONN_PROPS"));


        return processJsonReq(x, db);
    }

    public JSONArray buildResultJSONObject(List<Person> results) {
        JSONArray persons = new JSONArray();
        JSONObject person;

        for (Person p : results) {
            person = new JSONObject();
            person.put("id", p.getId());
            person.put("firstName", p.getFirstName());
            person.put("lastName", p.getLastName());
            persons.put(person);
        }
        return persons;
    }

    public String processJsonReq(String req, DatabaseHelper db) {
        JSONObject jsonRequestObject = new JSONObject(req);
        ArrayList<Person> queryResults = new ArrayList<>();
        JSONObject jsonBody = new JSONObject();
        JSONArray persons;
        /*
         * Dealing with JSONObject input structured like:
         * '{"type":[id,search],"input":[id or substring of name being searched]}'
         *
         * */
        try (Connection conn = db.getConnection()) {
            if (jsonRequestObject.getString("type").equals("id")) {
                queryResults.add(db.getPersonById(conn, jsonRequestObject.getInt("input")));
            } else if (jsonRequestObject.getString("type").equals("search")) {
                queryResults.addAll(db.getPersonsWithNamesContaining(jsonRequestObject.getString("input")));
            } else {
                throw new JSONException("Invalid request.");
            }
//            ArrayList<Person> sortedResults=removeDuplicates(queryResults);
            persons = buildResultJSONObject(queryResults);

            jsonBody.put("persons", persons);

        } catch (SQLException e) {
            jsonBody.put("persons", "");
            jsonBody.put("exception", e.getMessage());
        } catch (JSONException ex) {
            jsonBody.put("persons", "");
            jsonBody.put("exception", ex.getMessage());
        }
        return jsonBody.toString();
    }


}
