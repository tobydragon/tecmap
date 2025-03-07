package edu.ithaca.dragon.tecmap.suggester;

import com.opencsv.exceptions.CsvException;
import edu.ithaca.dragon.tecmap.Settings;
import edu.ithaca.dragon.tecmap.conceptgraph.*;
import edu.ithaca.dragon.tecmap.io.reader.*;
import edu.ithaca.dragon.tecmap.io.record.ConceptGraphRecord;
import edu.ithaca.dragon.tecmap.io.record.LearningResourceRecord;
import edu.ithaca.dragon.tecmap.learningresource.AssessmentItemResponse;
import edu.ithaca.dragon.tecmap.learningresource.ExampleLearningObjectLinkRecordFactory;
import edu.ithaca.dragon.tecmap.learningresource.ExampleLearningObjectResponseFactory;
import edu.ithaca.dragon.tecmap.learningresource.LearningResourceType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by home on 5/20/17.
 */
public class ConceptGraphSuggesterLibraryTest {

    @Test
    public void suggestionResourceSimpleTest() {
        ConceptGraph orig = ExampleConceptGraphFactory.simpleTestGraphTest();

        List<ConceptNode> concepts = ConceptGraphSuggesterLibrary.suggestConcepts(orig);
        OrganizedLearningResourceSuggestions res = new OrganizedLearningResourceSuggestions(orig, concepts);
        List<LearningResourceSuggestion> incomTest = res.incompleteList;
        List<LearningResourceSuggestion> wrongTest = res.wrongList;

        assertEquals(wrongTest.size(), 3);
        assertEquals(wrongTest.get(0).getId(), "Q3");
        assertEquals(wrongTest.get(1).getId(), "Q4");
        assertEquals(wrongTest.get(2).getId(), "Q5");

        assertEquals(incomTest.size(), 0);
    }

    @Test
    public void suggestionResourceMediumTest() {
        ConceptGraph orig = ExampleConceptGraphFactory.willExampleConceptGraphTestOneStudent();

        List<ConceptNode> concepts = ConceptGraphSuggesterLibrary.suggestConcepts(orig);
        OrganizedLearningResourceSuggestions res = new OrganizedLearningResourceSuggestions(orig, concepts);

        List<LearningResourceSuggestion> incomTest = res.incompleteList;
        List<LearningResourceSuggestion> wrongTest = res.wrongList;

        assertEquals(incomTest.size(), 3);
        assertEquals(incomTest.get(0).getId(), "Q6");
        assertEquals(incomTest.get(1).getId(), "Q13");
        assertEquals(incomTest.get(2).getId(), "Q10");

        assertEquals(wrongTest.size(), 4);
        assertEquals(wrongTest.get(0).getId(), "Q7");
        assertEquals(wrongTest.get(1).getId(), "Q15");
        assertEquals(wrongTest.get(2).getId(), "Q9");
        assertEquals(wrongTest.get(3).getId(), "Q14");

    }

    @Test
    public void conceptsToWorkOnTest() {
        ConceptGraph orig = ExampleConceptGraphFactory.willExampleConceptGraphTestOneStudent();
        List<ConceptNode> concepts = ConceptGraphSuggesterLibrary.suggestConcepts(orig);

        assertEquals(concepts.size(), 2);

        assertEquals(concepts.get(0).getID(), "Boolean");
        assertEquals(concepts.get(1).getID(), "Counting");

    }

    @Test
    public void RealDataConceptsTOWorkOn() throws IOException, CsvException {

        CohortConceptGraphs cohortConceptGraphs = null;

        //create the graph structure to be copied for each user
        ConceptGraphRecord structureRecord = ConceptGraphRecord.buildFromJson(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220GraphExample.json");
        List<LearningResourceRecord> linkRecord = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220Resources.json");
        ConceptGraph graph = new ConceptGraph(structureRecord, linkRecord);

        //create the data to be used to create and populate the graph copies
        List<String[]> rows = CsvFileLibrary.parseRowsFromFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/exampleDataAssessment.csv");
        List<CsvProcessor> processors = new ArrayList<>();
        processors.add(new CanvasConverter());
        TecmapCSVReader tecmapCsvReader = new CanvasReader(rows, processors);
        List<AssessmentItemResponse> assessments = tecmapCsvReader.getManualGradedResponses();

        //create the average and individual graphs
        cohortConceptGraphs = new CohortConceptGraphs(graph, assessments);

        ConceptGraph userGraph = cohortConceptGraphs.getUserGraph("s04");
        List<ConceptNode> concepts = ConceptGraphSuggesterLibrary.suggestConcepts(userGraph);

        assertEquals(concepts.size(), 4);
        assertEquals(concepts.get(0).getID(), "Recursion");
        assertEquals(concepts.get(1).getID(), "Testing Time Efficiency");
        assertEquals(concepts.get(2).getID(), "Pointers");
        assertEquals(concepts.get(3).getID(), "List");

    }


    @Test
    public void RealDataConceptsTOWorkOnZeroSugg() throws IOException, CsvException {

        CohortConceptGraphs cohortConceptGraphs = null;

        //create the graph structure to be copied for each user
        ConceptGraphRecord structureRecord = ConceptGraphRecord.buildFromJson(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220GraphExample.json");
        List<LearningResourceRecord> linkRecord = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220Resources.json");
        ConceptGraph graph = new ConceptGraph(structureRecord, linkRecord);

        //create the data to be used to create and populate the graph copies
        List<String[]> rows = CsvFileLibrary.parseRowsFromFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/exampleDataAssessment.csv");
        List<CsvProcessor> processors = new ArrayList<>();
        processors.add(new CanvasConverter());
        TecmapCSVReader tecmapCsvReader = new CanvasReader(rows, processors);
        List<AssessmentItemResponse> assessments = tecmapCsvReader.getManualGradedResponses();

        //create the average and individual graphs
        cohortConceptGraphs = new CohortConceptGraphs(graph, assessments);

        ConceptGraph userGraph = cohortConceptGraphs.getUserGraph("s03");
        List<ConceptNode> concepts = ConceptGraphSuggesterLibrary.suggestConcepts(userGraph);
        assertEquals(concepts.size(), 1);
        assertEquals(concepts.get(0).getID(), "List");



        ConceptGraph userGraph2 = cohortConceptGraphs.getUserGraph("s02");
        List<ConceptNode> concepts2 = ConceptGraphSuggesterLibrary.suggestConcepts(userGraph2);
        assertEquals(concepts2.size(), 2);
        assertEquals(concepts2.get(0).getID(), "Pointers");
        assertEquals(concepts2.get(1).getID(), "List");

    }



    @Test
    public void buildSuggestionMapSimpleTest() {
        ConceptGraph orig = ExampleConceptGraphFactory.simpleTestGraphTest();
        //what it is

        List<ConceptNode> concepts = ConceptGraphSuggesterLibrary.suggestConcepts(orig);
        HashMap<String, List<LearningResourceSuggestion>> objectSuggestionMap = ConceptGraphSuggesterLibrary.buildSuggestionMap(concepts, 1, orig);

        //what it should be
        List<LearningResourceSuggestion> testList3 = new ArrayList<>();
        assertEquals(1, objectSuggestionMap.size());
        assertEquals(testList3, objectSuggestionMap.get("C"));

    }

    @Test
    public void buildSuggestionMapWillOneStudentTest() {
        ConceptGraph orig = ExampleConceptGraphFactory.willExampleConceptGraphTestOneStudent();

        List<ConceptNode> concepts = ConceptGraphSuggesterLibrary.suggestConcepts(orig);
        HashMap<String, List<LearningResourceSuggestion>> objectSuggestionMap = ConceptGraphSuggesterLibrary.buildSuggestionMap(concepts, 1, orig);

        assertEquals(2, objectSuggestionMap.size());

        assertEquals(objectSuggestionMap.get("Boolean").get(0).getId(), "Q6");
        assertEquals(objectSuggestionMap.get("Boolean").get(1).getId(), "Q10");
        assertEquals(objectSuggestionMap.get("Counting").get(0).getId(), "Q13");

    }

    @Test
    public void suggestedOrderBuildLearningObjectListTest() {
        List<LearningResourceRecord> myList = ExampleLearningObjectLinkRecordFactory.makeSimpleLOLRecords();
        myList.add(new LearningResourceRecord("Q10", Arrays.asList(LearningResourceType.ASSESSMENT, LearningResourceType.PRACTICE), Arrays.asList("A"), 1, 1));
        ConceptGraph orig = new ConceptGraph(ExampleConceptGraphRecordFactory.makeSimple(),
                myList, ExampleLearningObjectResponseFactory.makeSimpleResponses());

//        //from A nodes
        HashMap<String, Integer> testCompareA = new HashMap<String, Integer>();
        testCompareA.put("Q3", 2);
        testCompareA.put("Q4", 2);
        testCompareA.put("Q5", 2);
        testCompareA.put("Q6", 2);
        testCompareA.put("Q1", 1);
        testCompareA.put("Q2", 1);
        testCompareA.put("Q10", 1);

        Map<String, Integer> learningSummaryFromA = orig.buildLearningResourcePathCount("A");
        Map<String, Integer> linkMap = orig.buildDirectConceptLinkCount();


        //makes sure that buildLearningMaterialPathCount works
        assertEquals(testCompareA, learningSummaryFromA);

        //build the suggested learning object list
        List<LearningResourceSuggestion> suggestedList = ConceptGraphSuggesterLibrary.buildLearningResourceSuggestionList(learningSummaryFromA, orig.getAssessmentItemMap(), "A", linkMap);


        //this is ordered based on "level"
        List<LearningResourceSuggestion> suggestListTest = new ArrayList<>();
        suggestListTest.add(new LearningResourceSuggestion("Q1", 1, LearningResourceSuggestion.Level.RIGHT, "A", 1));
        suggestListTest.add(new LearningResourceSuggestion("Q2", 1, LearningResourceSuggestion.Level.RIGHT, "A", 1));
        suggestListTest.add(new LearningResourceSuggestion("Q3", 2, LearningResourceSuggestion.Level.WRONG, "A", 1));
        suggestListTest.add(new LearningResourceSuggestion("Q4", 2, LearningResourceSuggestion.Level.WRONG, "A", 1));
        suggestListTest.add(new LearningResourceSuggestion("Q5", 2, LearningResourceSuggestion.Level.WRONG, "A", 1));
        suggestListTest.add(new LearningResourceSuggestion("Q6", 2, LearningResourceSuggestion.Level.WRONG, "A", 1));
        suggestListTest.add(new LearningResourceSuggestion("Q10", 1, LearningResourceSuggestion.Level.INCOMPLETE, "A", 1));

        //orders the list based off of "level"
        ConceptGraphSuggesterLibrary.sortSuggestions(suggestedList);

        for (int i = 0; i < suggestedList.size(); i++) {

            assertEquals(suggestedList.get(i).getId(), suggestListTest.get(i).getId());
            assertEquals(suggestedList.get(i).getPathNum(), suggestListTest.get(i).getPathNum());
            assertEquals(suggestedList.get(i).getLevel(), suggestListTest.get(i).getLevel());
            assertEquals(suggestedList.get(i).getReasoning(), suggestListTest.get(i).getReasoning());

        }
    }


    @Test
    public void toStringTest() {
        ConceptGraph orig = ExampleConceptGraphFactory.willExampleConceptGraphTestOneStudent();

        List<ConceptNode> concepts = ConceptGraphSuggesterLibrary.suggestConcepts(orig);
        OrganizedLearningResourceSuggestions res = new OrganizedLearningResourceSuggestions(orig, concepts);

        String incomString = res.toString(0);

        String wrongString = res.toString(1);


        assertEquals(incomString, "Resource: Q6\t Concepts it relates to: Boolean\t Importance: 1\t Direct Concept Links: 1" +
                "\nResource: Q13\t Concepts it relates to: Counting\t Importance: 1\t Direct Concept Links: 1"+
                "\nResource: Q10\t Concepts it relates to: Boolean\t Importance: 1\t Direct Concept Links: 4\n");


        assertEquals(wrongString, "Resource: Q7\t Concepts it relates to: Boolean\t Importance: 1\t Direct Concept Links: 1" +
                "\nResource: Q15\t Concepts it relates to: Counting\t Importance: 1\t Direct Concept Links: 1"+
                "\nResource: Q9\t Concepts it relates to: Boolean\t Importance: 1\t Direct Concept Links: 3"+
                "\nResource: Q14\t Concepts it relates to: Counting\t Importance: 1\t Direct Concept Links: 1\n");

    }


    @Test
    public void toStringTest2() throws IOException, CsvException {
        CohortConceptGraphs cohortConceptGraphs = null;

        //create the graph structure to be copied for each user
        ConceptGraphRecord structureRecord = ConceptGraphRecord.buildFromJson(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220GraphExample.json");
        List<LearningResourceRecord> linkRecord = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220Resources.json");
        ConceptGraph graph = new ConceptGraph(structureRecord, linkRecord);

        //create the data to be used to create and populate the graph copies
        List<String[]> rows = CsvFileLibrary.parseRowsFromFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/exampleDataAssessment.csv");
        List<CsvProcessor> processors = new ArrayList<>();
        processors.add(new CanvasConverter());
        TecmapCSVReader tecmapCsvReader = new CanvasReader(rows, processors);
        List<AssessmentItemResponse> assessments = tecmapCsvReader.getManualGradedResponses();

        //create the average and individual graphs
        cohortConceptGraphs = new CohortConceptGraphs(graph, assessments);

        ConceptGraph userGraph = cohortConceptGraphs.getUserGraph("s01");

        List<ConceptNode> concepts = ConceptGraphSuggesterLibrary.suggestConcepts(userGraph);


        OrganizedLearningResourceSuggestions res = new OrganizedLearningResourceSuggestions(userGraph, concepts);


        String incomString = res.toString(0);

        String wrongString = res.toString(1);


        assertEquals(incomString, "");

        assertEquals(wrongString,"Resource: Lab 4: Recursion\t Concepts it relates to: Pointers\t Importance: 1\t Direct Concept Links: 3" +
                "\nResource: Lab 7: Linked List\t Concepts it relates to: List\t Importance: 1\t Direct Concept Links: 3"+
                "\nResource: Lab 8: Comparing Arrays and Linked Lists\t Concepts it relates to: Testing Time Efficiency & List\t Importance: 1\t Direct Concept Links: 5"+
                "\nResource: Lab 2: Array Library\t Concepts it relates to: Pointers\t Importance: 1\t Direct Concept Links: 3\n");

    }
}