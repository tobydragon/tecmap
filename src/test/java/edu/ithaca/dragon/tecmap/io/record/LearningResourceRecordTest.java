package edu.ithaca.dragon.tecmap.io.record;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvException;
import edu.ithaca.dragon.tecmap.Settings;
import edu.ithaca.dragon.tecmap.io.reader.*;
import edu.ithaca.dragon.tecmap.learningresource.AssessmentItem;
import edu.ithaca.dragon.tecmap.learningresource.LearningResource;
import edu.ithaca.dragon.tecmap.learningresource.LearningResourceType;
import edu.ithaca.dragon.tecmap.util.DataUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class LearningResourceRecordTest {


    @Test
    public void testBuildingResourcesFromRecords(){
        Collection<AssessmentItem> assessments = new ArrayList<>();
        Collection<LearningResource> materials = new ArrayList<>();
        try {
            Collection<LearningResourceRecord> fromFile = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/LearningRecordResourceTest-MissingFields.json");

            for (LearningResourceRecord record : fromFile){
                //set defaults if there aren't any resources
                if (record.getResourceTypes().size() == 0){
                    record.setResourceTypes(LearningResourceType.getDefaultResourceTypes());
                }
                //may create duplicate records if one resource is both an assessment and a material
                if (record.isType(LearningResourceType.ASSESSMENT)){
                    assessments.add(new AssessmentItem(record));
                }
                if (record.isType(LearningResourceType.INFORMATION) || record.isType(LearningResourceType.PRACTICE)){
                    //since we've already added an assessment for this record, remove it so the list can be used to create the material directly from the list
                    record.getResourceTypes().remove(LearningResourceType.ASSESSMENT);
                    materials.add(new LearningResource(record));
                }
            }
            assertEquals(2, assessments.size());
            assertEquals(3, materials.size());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public static Collection<LearningResourceRecord> buildTestInstances(){
        Collection<LearningResourceRecord> list = new ArrayList<>();

        list.add( new LearningResourceRecord(
                "variableHiddenQuizQuestion",
                Arrays.asList(LearningResourceType.ASSESSMENT),
                Arrays.asList("Variables", "Assignments"),
                1,
                1
        ));

        list.add( new LearningResourceRecord(
                "reassignReturnedQuizQuestion",
                Arrays.asList(LearningResourceType.ASSESSMENT, LearningResourceType.PRACTICE),
                Arrays.asList("Variables", "Assignments"),
                1,
                1
        ));

        list.add( new LearningResourceRecord(
                "VariablesChapter",
                Arrays.asList(LearningResourceType.INFORMATION),
                Arrays.asList("Variables", "Assignments"),
                1,
                1
        ));

        list.add( new LearningResourceRecord(
                "reassignQuizQuestion",
                Arrays.asList(LearningResourceType.INFORMATION, LearningResourceType.PRACTICE),
                Arrays.asList("Variables", "Assignments"),
                1,
                1
        ));

        return list;
    }

    public static void main(String[] args){
        Collection<LearningResourceRecord> toWrite = buildTestInstances();
        try {
            LearningResourceRecord.resourceRecordsToJSON(toWrite, Settings.TEST_RESOURCE_DIR + "AutoCreated/LearningRecordResourceTest.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ///////////////  Legacy tests from LearningObjectLinkRecordTest (which this class replaced)

    @Test
    public void addConceptIDTest(){
        ArrayList<String> concepts = new ArrayList<>();
        concepts.add("Concept 1");
        String id = "id 1";
        LearningResourceRecord loObject = new LearningResourceRecord(id,Arrays.asList(LearningResourceType.ASSESSMENT, LearningResourceType.PRACTICE), concepts, 1, 1);
        assertEquals("Concept 1",loObject.getConceptIds().iterator().next());
        assertEquals(1,loObject.getConceptIds().size());
        assertEquals("id 1", loObject.getLearningResourceId());
        loObject.addConceptId("Concept 2");
        Iterator<String> checker = loObject.getConceptIds().iterator();
        checker.next();
        assertEquals("Concept 2", checker.next());
        assertEquals(2,loObject.getConceptIds().size());
    }

    @Test
    public void toStringTest(){
        ArrayList<String> concepts = new ArrayList<>();
        concepts.add("Concept 1");
        concepts.add("Concept 2");
        String id = "id 1";
        LearningResourceRecord loObject = new LearningResourceRecord(id,Arrays.asList(LearningResourceType.ASSESSMENT, LearningResourceType.PRACTICE), concepts, 1, 1);
        assertEquals("(Learning Resource ID: id 1 Concept IDs: Concept 1, Concept 2)",loObject.toString());
    }

    @Test
    public void toJsonTest(){
        ArrayList<String> concepts = new ArrayList<>();
        concepts.add("Concept 1");
        concepts.add("Concept 2");
        String id = "id 1";
        LearningResourceRecord loObject = new LearningResourceRecord(id,Arrays.asList(LearningResourceType.ASSESSMENT, LearningResourceType.PRACTICE), concepts, 1, 1);

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(Settings.TEST_RESOURCE_DIR + "practicalExamples/SystemCreated/recordToJson.json"), loObject);
        }catch (Exception e){
            e.printStackTrace();
        }

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            LearningResourceRecord  record = mapper.readValue(new File(Settings.TEST_RESOURCE_DIR + "practicalExamples/SystemCreated/recordToJson.json"), LearningResourceRecord.class);

            assertEquals("id 1", record.getLearningResourceId());
            assertEquals(2, record.getConceptIds().size());
            Iterator<String> checker = loObject.getConceptIds().iterator();
            checker.next();
            assertEquals("Concept 2", checker.next());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readSimpleFromFile(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


            List<LearningResourceRecord> list = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleResource.json");

            assertEquals(6, list.size());
            assertEquals("Q1", list.get(0).getLearningResourceId());
            assertEquals(1, list.get(0).getConceptIds().size());
            assertEquals("C", list.get(2).getConceptIds().iterator().next());
            assertEquals(1, list.get(0).getDataImportance(), DataUtil.OK_FLOAT_MARGIN);
            assertEquals(1, list.get(1).getDataImportance(), DataUtil.OK_FLOAT_MARGIN);
            assertEquals(.3, list.get(2).getDataImportance(), DataUtil.OK_FLOAT_MARGIN);
        }
        catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void createLearningObjectLinkRecordsTest() throws IOException, CsvException {
        List<String[]> rows = CsvFileLibrary.parseRowsFromFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/complexRealisticAssessment.csv");
        List<CsvProcessor> processors = new ArrayList<>();
        processors.add(new CanvasConverter());
        try {
            TecmapCSVReader test = new CanvasReader(rows, processors);
            Collection<AssessmentItem> list = test.getManualGradedLearningObjects();
            List<AssessmentItem> list2 = test.getManualGradedLearningObjects();
            List<LearningResourceRecord> lolrList = LearningResourceRecord.createLearningResourceRecordsFromAssessmentItems(list);
            List<String> resultString = new ArrayList<String>();
            for (LearningResourceRecord lolr : lolrList) {
                resultString.add(lolr.getLearningResourceId());
            }

            List<String> list2string = new ArrayList<String>();
            for (AssessmentItem lo : list2) {
                list2string.add(lo.getId());
            }
            assertEquals(list2string.toString(), resultString.toString());
        }catch (IOException e){
            e.printStackTrace();
            fail();
        }
    }
}
