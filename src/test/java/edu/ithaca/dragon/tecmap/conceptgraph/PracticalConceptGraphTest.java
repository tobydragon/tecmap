package edu.ithaca.dragon.tecmap.conceptgraph;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ithaca.dragon.tecmap.Settings;
import edu.ithaca.dragon.tecmap.io.reader.*;
import edu.ithaca.dragon.tecmap.io.record.ConceptGraphRecord;
import edu.ithaca.dragon.tecmap.io.record.LearningResourceRecord;
import edu.ithaca.dragon.tecmap.util.DataUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by willsuchanek on 5/2/17.
 */
public class PracticalConceptGraphTest {
    public static final String TEST_DIR = Settings.TEST_RESOURCE_DIR + "practicalExamples/";

    @Test
    public void basicRealisticConceptGraphTest(){
        ObjectMapper graphMapper = new ObjectMapper();

        graphMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{
            List<String[]> rows = CsvFileLibrary.parseRowsFromFile(TEST_DIR+"basicRealisticAssessment.csv");
            List<CsvProcessor> processors = new ArrayList<>();
            processors.add(new CanvasConverter());
            TecmapCSVReader tecmapCsvReader = new CanvasReader(rows, processors);


            ConceptGraphRecord graphRecord = ConceptGraphRecord.buildFromJson(TEST_DIR+"mediumRealisticConceptGraph.json");

            List<LearningResourceRecord> LOLRlist = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(TEST_DIR + "mediumRealisticResource.json");
            ConceptGraph graph = new ConceptGraph(graphRecord, LOLRlist, tecmapCsvReader.getManualGradedResponses());

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(Settings.TEST_RESOURCE_DIR + "practicalExamples/SystemCreated/blankRealisticExample.json"), TreeConverter.makeTreeCopy(graph).buildConceptGraphRecord());

            graph.calcDataImportance();
            graph.calcKnowledgeEstimates();

            assertEquals("Intro CS", graph.findNodeById("Intro CS").getID());
            assertEquals(5,graph.findNodeById("Boolean").getAssessmentItemMap().size());
            assertEquals(15,graph.getAssessmentItemMap().size());

            ConceptGraphRecord tree = TreeConverter.makeTreeCopy(graph).buildConceptGraphRecord();
            //Object to JSON in file
            mapper = new ObjectMapper();
            mapper.writeValue(new File(Settings.TEST_RESOURCE_DIR + "practicalExamples/SystemCreated/basicRealisticExample.json"), tree);


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Test
    public void basicRealisticTest(){
        ObjectMapper graphMapper = new ObjectMapper();

        graphMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{
            List<String[]> rows = CsvFileLibrary.parseRowsFromFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
            List<CsvProcessor> processors = new ArrayList<>();
            processors.add(new CanvasConverter());
            TecmapCSVReader tecmapCsvReader = new CanvasReader(rows, processors);

            ConceptGraphRecord graphRecord = ConceptGraphRecord.buildFromJson(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");

            List<LearningResourceRecord> LOLRlist = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json");
            ConceptGraph graph = new ConceptGraph(graphRecord, LOLRlist);

            CohortConceptGraphs gcg = new CohortConceptGraphs(graph, tecmapCsvReader.getManualGradedResponses());

            ConceptGraph testGraph = gcg.getAvgGraph();


            assertEquals("Intro CS", testGraph.findNodeById("Intro CS").getID());
            assertEquals(7, testGraph.findNodeById("Boolean").getAssessmentItemMap().size());
            assertEquals(15,testGraph.getAssessmentItemMap().size());


            assertEquals(0.806, testGraph.findNodeById("Boolean").getKnowledgeEstimate(), DataUtil.OK_FLOAT_MARGIN);
            assertEquals(0.783090, testGraph.findNodeById("Boolean Expressions").getKnowledgeEstimate(), DataUtil.OK_FLOAT_MARGIN);
            assertEquals(0.746, testGraph.findNodeById("If Statement").getKnowledgeEstimate(), DataUtil.OK_FLOAT_MARGIN);
            assertEquals(0.722, testGraph.findNodeById("While Loop").getKnowledgeEstimate(), DataUtil.OK_FLOAT_MARGIN);
            assertEquals(0.566, testGraph.findNodeById("Counting").getKnowledgeEstimate(), DataUtil.OK_FLOAT_MARGIN);
            assertEquals(0.575, testGraph.findNodeById("For Loop").getKnowledgeEstimate(), DataUtil.OK_FLOAT_MARGIN);


        }catch (Exception e){
            e.printStackTrace();
        }

    }



    @Test
    public void advancedRealisticConceptGraphTest(){
        ObjectMapper graphMapper = new ObjectMapper();

        graphMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{
            List<String[]> rows = CsvFileLibrary.parseRowsFromFile(TEST_DIR+"advancedRealisticAssessment.csv");
            List<CsvProcessor> processors = new ArrayList<>();
            processors.add(new CanvasConverter());
            TecmapCSVReader tecmapCsvReader = new CanvasReader(rows, processors);

            ConceptGraphRecord graphRecord = ConceptGraphRecord.buildFromJson(TEST_DIR+"mediumRealisticConceptGraph.json");

            List<LearningResourceRecord> LOLRlist = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(TEST_DIR + "mediumRealisticResource.json");
            ConceptGraph graph = new ConceptGraph(graphRecord, LOLRlist);

            CohortConceptGraphs gcg = new CohortConceptGraphs(graph, tecmapCsvReader.getManualGradedResponses());
            graph.calcDataImportance();
            graph.calcKnowledgeEstimates();


            assertEquals("Intro CS", graph.findNodeById("Intro CS").getID());
            assertEquals(5, graph.findNodeById("Boolean").getAssessmentItemMap().size());
            assertEquals(15,graph.getAssessmentItemMap().size());




            ObjectMapper mapper = new ObjectMapper();
            ConceptGraphRecord tree = TreeConverter.makeTreeCopy(graph).buildConceptGraphRecord();
            //Object to JSON in file
            mapper.writeValue(new File(Settings.TEST_RESOURCE_DIR + "practicalExamples/SystemCreated/advancedRealisticExample.json"), tree);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void SingleStudentRealisticConceptGraphTest(){
        ObjectMapper graphMapper = new ObjectMapper();

        graphMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{
            List<String[]> rows = CsvFileLibrary.parseRowsFromFile(TEST_DIR+"singleStudentRealisticAssessment.csv");
            List<CsvProcessor> processors = new ArrayList<>();
            processors.add(new CanvasConverter());
            TecmapCSVReader tecmapCsvReader = new CanvasReader(rows, processors);

            ConceptGraphRecord graphRecord = ConceptGraphRecord.buildFromJson(TEST_DIR+"mediumRealisticConceptGraph.json");
            List<LearningResourceRecord> LOLRlist = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(TEST_DIR+"mediumRealisticResource.json");
            ConceptGraph graph = new ConceptGraph(graphRecord, LOLRlist, tecmapCsvReader.getManualGradedResponses());
            graph.calcDataImportance();
            graph.calcKnowledgeEstimates();


            assertEquals("Intro CS", graph.findNodeById("Intro CS").getID());
            assertEquals(5, graph.findNodeById("Boolean").getAssessmentItemMap().size());
            assertEquals(15, graph.getAssessmentItemMap().size());
            assertEquals(-.5,graph.findNodeById("Sequence Types").getKnowledgeEstimate(), DataUtil.OK_FLOAT_MARGIN);
            assertEquals(-0.545,graph.findNodeById("For Loop").getKnowledgeEstimate(), DataUtil.OK_FLOAT_MARGIN);
            assertEquals(.346,graph.findNodeById("Loops").getKnowledgeEstimate(), DataUtil.OK_FLOAT_MARGIN);



            ConceptGraphRecord tree = TreeConverter.makeTreeCopy(graph).buildConceptGraphRecord();
            ObjectMapper mapper = new ObjectMapper();
            //Object to JSON in file
            mapper.writeValue(new File(Settings.TEST_RESOURCE_DIR + "practicalExamples/SystemCreated/singleStudentRealisticExample.json"), tree);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
