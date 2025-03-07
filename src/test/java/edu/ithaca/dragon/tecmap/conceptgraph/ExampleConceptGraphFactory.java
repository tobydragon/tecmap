package edu.ithaca.dragon.tecmap.conceptgraph;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ithaca.dragon.tecmap.Settings;
import edu.ithaca.dragon.tecmap.io.reader.*;
import edu.ithaca.dragon.tecmap.io.record.ConceptGraphRecord;
import edu.ithaca.dragon.tecmap.io.record.LearningResourceRecord;
import edu.ithaca.dragon.tecmap.learningresource.ExampleLearningObjectLinkRecordFactory;
import edu.ithaca.dragon.tecmap.learningresource.ExampleLearningObjectResponseFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tdragon
 * 2/15/17
 */
public class ExampleConceptGraphFactory {

    public static ConceptGraph makeSimpleStructure(){
        return new ConceptGraph(ExampleConceptGraphRecordFactory.makeSimple());
    }

    public static ConceptGraph makeSimpleStructureAndLearningObjects(){
        return new ConceptGraph(ExampleConceptGraphRecordFactory.makeSimple(),
                ExampleLearningObjectLinkRecordFactory.makeSimpleLOLRecords());
    }

    public static ConceptGraph makeSimpleCompleteWithData(){
        return new ConceptGraph(ExampleConceptGraphRecordFactory.makeSimple(),
                ExampleLearningObjectLinkRecordFactory.makeSimpleLOLRecords(),
                ExampleLearningObjectResponseFactory.makeSimpleResponses());
    }




    public static ConceptGraph makeMedium(){
        return new ConceptGraph(ExampleConceptGraphRecordFactory.makeMedium());
    }

    public static ConceptGraph makeComplex(){
        return new ConceptGraph(ExampleConceptGraphRecordFactory.makeComplex());
    }

    public static ConceptGraph makeSuperComplex(){
        return new ConceptGraph(ExampleConceptGraphRecordFactory.makeSuperComplex());
    }

    //TODO: update these to new format
//    public static ConceptGraph makeSimpleInputTree(){
//        List<ConceptNode> cnList = new ArrayList<>();
//        List<LinkRecord> clList = new ArrayList<>();
//
//        cnList.add(new ConceptNode("A"));
//        cnList.add(new ConceptNode("B"));
//        cnList.add(new ConceptNode("C"));
//        cnList.add(new ConceptNode("D"));
//        cnList.add(new ConceptNode("E"));
//
//        clList.add( new LinkRecord("A","B"));
//        clList.add(new LinkRecord("A","C"));
//        clList.add(new LinkRecord("B","D"));
//        clList.add(new LinkRecord("C","E"));
//
//        ConceptGraphRecordOld inputNodesAndLinks = new ConceptGraphRecordOld(cnList, clList);
//        return new ConceptGraph(inputNodesAndLinks);
//    }
//
//    public static ConceptGraph makeComplexInputTree(){
//        List<ConceptNode> cnList = new ArrayList<>();
//        List<LinkRecord> linkList = new ArrayList<>();
//
//        cnList.add(new ConceptNode("A"));
//        cnList.add(new ConceptNode("B"));
//        cnList.add(new ConceptNode("C"));
//        cnList.add(new ConceptNode("D"));
//        cnList.add(new ConceptNode("E"));
//        cnList.add(new ConceptNode("F"));
//        cnList.add(new ConceptNode("G"));
//        cnList.add(new ConceptNode("H"));
//        cnList.add(new ConceptNode("I"));
//
//        LinkRecord link = new LinkRecord("A","B");
//        linkList.add(link);
//        link = new LinkRecord("A","C");
//        linkList.add(link);
//        link = new LinkRecord("B","D");
//        linkList.add(link);
//        link = new LinkRecord("C","E");
//        linkList.add(link);
//        link = new LinkRecord("C","F");
//        linkList.add(link);
//        link = new LinkRecord("D","G");
//        linkList.add(link);
//        link = new LinkRecord("G","H");
//        linkList.add(link);
//        link = new LinkRecord("G","I");
//        linkList.add(link);
//
//        ConceptGraphRecordOld lists = new ConceptGraphRecordOld(cnList, linkList);
//        return new ConceptGraph(lists);
//    }


     public static ConceptGraph willExampleConceptGraphTestOneStudent() {
        try{
            List<String[]> rows = CsvFileLibrary.parseRowsFromFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
            List<CsvProcessor> processors = new ArrayList<>();
            processors.add(new CanvasConverter());
            TecmapCSVReader tecmapCsvReader = new CanvasReader(rows, processors);

            ConceptGraphRecord graphRecord = ConceptGraphRecord.buildFromJson(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");
            List<LearningResourceRecord> learningResourceRecords = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json");
            ConceptGraph graph = new ConceptGraph(graphRecord, learningResourceRecords, tecmapCsvReader.getManualGradedResponses());
            graph.calcKnowledgeEstimates();

            return graph;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }

    public static ConceptGraph simpleTestGraphTest() {
        ObjectMapper graphMapper = new ObjectMapper();

        graphMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{
            List<String[]> rows = CsvFileLibrary.parseRowsFromFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleAssessment.csv");
            List<CsvProcessor> processors = new ArrayList<>();
            processors.add(new CanvasConverter());
            TecmapCSVReader tecmapCsvReader = new CanvasReader(rows, processors);
            ConceptGraphRecord graphRecord = ConceptGraphRecord.buildFromJson(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleConceptGraph.json");
            List<LearningResourceRecord> LOLRlist = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleResource.json");
             ConceptGraph graph = new ConceptGraph(graphRecord, LOLRlist, tecmapCsvReader.getManualGradedResponses());

            graph.calcKnowledgeEstimates();
//                        System.out.println(graph.toString());

            return graph;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static ConceptGraph mediumTestGraphTest() {
        try {
            return new ConceptGraph(ConceptGraphRecord.buildFromJson(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/mediumConceptGraph.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
