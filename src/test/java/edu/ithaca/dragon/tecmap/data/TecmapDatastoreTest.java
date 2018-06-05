package edu.ithaca.dragon.tecmap.data;

import edu.ithaca.dragon.tecmap.Settings;
import edu.ithaca.dragon.tecmap.TecmapAPI;
import edu.ithaca.dragon.tecmap.io.Json;
import edu.ithaca.dragon.tecmap.io.record.ConceptGraphRecord;
import edu.ithaca.dragon.tecmap.tecmapExamples.Cs1ExampleJsonStrings;
import edu.ithaca.dragon.tecmap.tecmapstate.TecmapState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.util.AssertionErrors.fail;

class TecmapDatastoreTest {

    private TecmapDatastore tecmapDatastore;

    @BeforeEach
    void setup() throws IOException {
        tecmapDatastore = TecmapFileDatastore.buildFromJsonFile(Settings.DEFAULT_TEST_DATASTORE_FILE);
    }

    @Test
    void retrieveTecmapForId() throws IOException {
        //check invalid options
        assertNull(tecmapDatastore.retrieveTecmapForId("noSuchId"));
        assertNull(tecmapDatastore.retrieveTecmapForId("BadPaths"));

        //check a valid TecmapAPI
        TecmapAPI cs1ExampleMap = tecmapDatastore.retrieveTecmapForId("Cs1Example");

        assertEquals(10, cs1ExampleMap.createBlankLearningResourceRecordsFromAssessment().size());

        assertEquals(Cs1ExampleJsonStrings.structureWithResourceConnectionsAsTree, cs1ExampleMap.createStructureTree().toJsonString());
        Collection<String> twoAssessmentsConnectedConcepts = cs1ExampleMap.createConceptIdListToPrint();
        assertEquals(Cs1ExampleJsonStrings.allConceptsString, twoAssessmentsConnectedConcepts.toString());
        List<ConceptGraphRecord> records =  cs1ExampleMap.createCohortTree().getGraphRecords();
        assertEquals(4, records.size());
        for (ConceptGraphRecord record : records){
            //Bart
            if (record.getName().equals("s02")){
                //System.out.println(Json.toJsonString(record));
                assertEquals(Cs1ExampleJsonStrings.bartDataTree, Json.toJsonString(record));
            }
        }

    }

    @Test
    void retrieveTecmapForIdExtraParameter(){
        TecmapAPI noAssessmentModeMap = tecmapDatastore.retrieveTecmapForId("Cs1Example", TecmapState.noAssessment);
        TecmapAPI assessmentAddedModeMap = tecmapDatastore.retrieveTecmapForId("Cs1Example", TecmapState.assessmentAdded);
        TecmapAPI assessmentConnectedModeMap = tecmapDatastore.retrieveTecmapForId("Cs1Example", TecmapState.assessmentConnected);

        assertEquals(TecmapState.noAssessment, noAssessmentModeMap.getCurrentState());
        assertNotNull(noAssessmentModeMap.createStructureTree());
        assertEquals(0, noAssessmentModeMap.createBlankLearningResourceRecordsFromAssessment().size());
        assertNull( noAssessmentModeMap.createCohortTree());

        assertEquals(TecmapState.assessmentAdded, assessmentAddedModeMap.getCurrentState());
        assertNotNull(assessmentAddedModeMap.createStructureTree());
        assertEquals(10, assessmentAddedModeMap.createBlankLearningResourceRecordsFromAssessment().size());
        assertNull(assessmentAddedModeMap.createCohortTree());

        assertEquals(TecmapState.assessmentConnected, assessmentConnectedModeMap.getCurrentState());
        assertNotNull(assessmentConnectedModeMap.createStructureTree());
        assertEquals(10, assessmentConnectedModeMap.createBlankLearningResourceRecordsFromAssessment().size());
        assertNotNull(assessmentConnectedModeMap.createCohortTree());
    }

    @Test
    void retrieveValidIdsAndStates(){
        Map<String, List<String>> validMap = tecmapDatastore.retrieveValidIdsAndStates();
        assertEquals(2, validMap.size());
        fail("TODO");
        //assertEquals(());
    }
}