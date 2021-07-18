# LambaAssignmentGaggle
Solution for Candidate Assignment

## LambdaFunction Implementation
Implementation for AWS lambda function is at
src/main/java/com/example/lambaassignmentgaggle/PersonRequestHandler.java

It interacts with the databse using src/main/java/com/example/lambaassignmentgaggle/DatabaseHelper to obtain results for queries.
Query used for obtaining a person with id: "SELECT * FROM PERSON WHERE id = "+id + ";"

It does a search by splitting the search terms by spaces and then for each segment it executes:
"SELECT * FROM PERSON WHERE PERSON.first_name LIKE '%" + searchSegment + "%';
And adds the results to the hashmap.

## Tests for both operations
LambaAssignmentGaggle/src/test/java/com/example/lambaassignmentgaggle/PersonHandlerTests.java
