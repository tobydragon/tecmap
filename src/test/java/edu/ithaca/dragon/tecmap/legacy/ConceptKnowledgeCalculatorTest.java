package edu.ithaca.dragon.tecmap.legacy;

import com.opencsv.exceptions.CsvException;
import edu.ithaca.dragon.tecmap.Settings;
import edu.ithaca.dragon.tecmap.conceptgraph.CohortConceptGraphs;
import edu.ithaca.dragon.tecmap.conceptgraph.ConceptGraph;
import edu.ithaca.dragon.tecmap.io.record.LearningResourceRecord;
import edu.ithaca.dragon.tecmap.learningresource.AssessmentItem;
import edu.ithaca.dragon.tecmap.learningresource.AssessmentItemResponse;
import edu.ithaca.dragon.tecmap.suggester.GroupSuggester.*;
import edu.ithaca.dragon.tecmap.suggester.LearningResourceSuggestion;
import edu.ithaca.dragon.tecmap.suggester.OrganizedLearningResourceSuggestions;
import edu.ithaca.dragon.tecmap.util.ErrorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Mia Kimmich Mitchell on 6/9/2017.
 */
public class ConceptKnowledgeCalculatorTest {

    static Logger logger = LogManager.getLogger(ConceptKnowledgeCalculator.class);


    @Test
    public void  realDataCreateSmallGroupTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchConceptGraph.json",
                    Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchResource1.json",
                    Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchAssessment1.csv");
        } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");
        }
        //set up for buckets
        List<List<Integer>> ranges = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        temp.add(0);
        temp.add(50);
        List<Integer> temp2 = new ArrayList<>();
        temp2.add(50);
        temp2.add(80);
        List<Integer> temp3 = new ArrayList<>();
        temp3.add(80);
        temp3.add(100);
        ranges.add(temp);
        ranges.add(temp2);
        ranges.add(temp3);

        List<Suggester> suggesterList = new ArrayList<>();
        try {
            suggesterList.add(new BucketSuggester(ranges));
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Group> groupings = null;
        try {
            groupings = ckc.calcSmallGroups(suggesterList, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(groupings.size(), 3);

        assertEquals(groupings.get(0).getRationale(), "  ,Bucket: 0 - 50");
        assertEquals(groupings.get(0).getSize(), 0);

        assertEquals(groupings.get(1).getRationale(), "  ,Bucket: 50 - 80");
        assertEquals(groupings.get(1).getStudentNames().get(0), "s3" );
        assertEquals(groupings.get(1).getStudentNames().get(1), "s4" );
        assertEquals(groupings.get(1).getStudentNames().get(2), "s6" );
        assertEquals(groupings.get(1).getStudentNames().get(3), "s7" );
        assertEquals(groupings.get(1).getStudentNames().get(4), "s2" );

        assertEquals(groupings.get(2).getRationale(), "  ,Bucket: 80 - 100");
        assertEquals(groupings.get(2).getStudentNames().get(0), "s5" );
        assertEquals(groupings.get(2).getStudentNames().get(1), "s1" );
    }

    @Test
    public void getModeTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
            assertEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.COHORTGRAPH);

        } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");
        }
    }

    @Test
    public void getCohortData(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
            assertEquals(ckc.currentStructure().get(0),Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json" );
        } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");
        }
    }


    @Test
    public void calcIndividualConceptNodesSuggestionsTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
        } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");
        }

        List<String> concepts = null;
        try {
            concepts = ckc.calcIndividualConceptNodesSuggestions("bspinache1");

            assertEquals(concepts.size(), 2);
            assertEquals(concepts.get(0), "Boolean");
            assertEquals(concepts.get(1), "Counting");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Test
    public void clearandCreateStructureGraphTest() {
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");

            List<String> struct = new ArrayList<>();
            struct.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchConceptGraph.json");

            ckc.clearAndCreateStructureData(struct);

            assertEquals(ckc.currentStructure().get(0),Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchConceptGraph.json" );

            assertEquals(ckc.currentResource().size(),0);
            assertEquals(ckc.currentAssessment().size(), 0);

            struct.clear();
            struct.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleConceptGraph.json");
            ckc.clearAndCreateStructureData(struct);
            assertEquals(ckc.currentStructure().get(0),Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleConceptGraph.json" );

            assertEquals(ckc.currentResource().size(),0);
            assertEquals(ckc.currentAssessment().size(), 0);


        } catch (Exception e) {
            fail("Unable to load default files. Test unable to run");
        }
    }


    @Test
    public void updateStructureWithAnotherFile() {
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");
            ckc.addResource(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchResource1.json");

            assertEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.STRUCTUREGRAPHWITHRESOURCE);
            assertEquals(ckc.currentResource().size(), 1);
            assertEquals(ckc.currentStructure().get(0), Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");
            assertEquals(ckc.currentResource().size(),1);
            assertEquals(ckc.currentResource().get(0), Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchResource1.json");
            assertEquals(ckc.currentAssessment().size(), 0);

            ckc.updateStructureFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchConceptGraph.json");

            assertEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.STRUCTUREGRAPHWITHRESOURCE);
            assertEquals(ckc.currentResource().size(),1);
            assertEquals(ckc.currentResource().get(0), Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchResource1.json");
            assertEquals(ckc.currentAssessment().size(), 0);

            assertEquals(ckc.currentStructure().get(0), Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchConceptGraph.json");


        } catch (Exception e) {
            fail("Unable to load default files. Test unable to run");
        }
    }

    

    @Test
    public void clearandCreateCohortGraphTest() {
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");

            List<String> struct = new ArrayList<>();
            struct.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchConceptGraph.json");
            List<String> res = new ArrayList<>();
            res.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchResource1.json");
            List<String> assess = new ArrayList<>();
            assess.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchAssessment1.csv");

            ckc.clearAndCreateCohortData(struct,res,assess);

            assertEquals(ckc.currentStructure().size(), 1);
            assertEquals(ckc.currentResource().size(),1);
            assertEquals(ckc.currentAssessment().size(), 1);
            assertEquals(ckc.currentStructure().get(0),Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchConceptGraph.json" );
            assertEquals(ckc.currentResource().get(0),Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchResource1.json" );
            assertEquals(ckc.currentAssessment().get(0), Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchAssessment1.csv");



        } catch (Exception e) {
            fail("Unable to load default files. Test unable to run");
        }


    }





    @Test
    public void calcIndividualConceptNodesSuggestionsBadInputTest() throws IOException {
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
            ckc.calcIndividualConceptNodesSuggestions("baduser");
            fail("User is not present.");

        } catch (Exception e) {}


    }

    @Test
    public void  calcIndividualGraphSuggestionsTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
        } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");
        }

        OrganizedLearningResourceSuggestions res = null;
        try {
            res = ckc.calcIndividualGraphSuggestions("bspinache1");
            List<LearningResourceSuggestion> incomTest = res.incompleteList;
            List<LearningResourceSuggestion> wrongTest = res.wrongList;

            assertEquals(incomTest.size(),3);
            assertEquals(incomTest.get(0).getId(),"Q6");

            assertEquals(incomTest.get(1).getId(),"Q13");

            assertEquals(wrongTest.size(),4);
            assertEquals(wrongTest.get(0).getId(), "Q7");
            assertEquals(wrongTest.get(1).getId(), "Q15");
            assertEquals(wrongTest.get(2).getId(), "Q9");
            assertEquals(wrongTest.get(3).getId(), "Q14");

        } catch (Exception e) {
            fail("Unable to find user");
        }


    }

    @Test
    public void  realDataCalcIndividualGraphSuggestionsTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc =new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220GraphExample.json",
                    Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220Resources.json",
                    Settings.TEST_RESOURCE_DIR + "ManuallyCreated/exampleDataAssessment.csv");

         } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");
        }

        OrganizedLearningResourceSuggestions res = null;
        try {
            res = ckc.calcIndividualGraphSuggestions("s04");
            List<LearningResourceSuggestion> incomTest = res.incompleteList;
            List<LearningResourceSuggestion> wrongTest = res.wrongList;

            assertEquals(incomTest.size(), 0);
            assertEquals(incomTest, new ArrayList<>());

            assertEquals(wrongTest.size(), 10);

            assertEquals(wrongTest.get(0).getId(), "Lab 4: Recursion");
            assertEquals(wrongTest.get(1).getId(), "Lab 6: ArrayList and Testing");
            assertEquals(wrongTest.get(2).getId(), "Lab 4: Recursion");
            assertEquals(wrongTest.get(3).getId(), "Lab 3: Comparing Array Library Efficiency");
            assertEquals(wrongTest.get(4).getId(), "Lab 3: Comparing Array Library Efficiency");
            assertEquals(wrongTest.get(5).getId(), "Lab 7: Linked List");
            assertEquals(wrongTest.get(6).getId(), "Lab 5: Comparing Searches");
            assertEquals(wrongTest.get(7).getId(), "Lab 5: Comparing Searches");
            assertEquals(wrongTest.get(8).getId(), "Lab 8: Comparing Arrays and Linked Lists");
            assertEquals(wrongTest.get(9).getId(), "Lab 8: Comparing Arrays and Linked Lists");


        } catch (Exception e) {
            fail("Unable to find user");
        }

    }



    @Test
    public void  realDataCalcIndividualGraphSuggestionsTest2(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc =new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220GraphExample.json",
                    Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220Resources.json",
                    Settings.TEST_RESOURCE_DIR + "ManuallyCreated/exampleDataAssessment.csv");

        } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");
        }

        OrganizedLearningResourceSuggestions res = null;
        try {

            res = ckc.calcIndividualGraphSuggestions("s02");

            List<LearningResourceSuggestion> incomTest = res.incompleteList;
            List<LearningResourceSuggestion> wrongTest = res.wrongList;

            assertEquals(incomTest.size(), 0);
            assertEquals(incomTest, new ArrayList<>());

            assertEquals(wrongTest.size(), 4);
            assertEquals(wrongTest.get(0).getId(), "Lab 4: Recursion");
            assertEquals(wrongTest.get(1).getId(), "Lab 6: ArrayList and Testing");
            assertEquals(wrongTest.get(2).getId(), "Lab 3: Comparing Array Library Efficiency");
            assertEquals(wrongTest.get(3).getId(), "Lab 7: Linked List");



        } catch (Exception e) {
            fail("Unable to find user");
        }

    }


    @Test
    public void  calcIndividualGraphSuggestionsWIthEmptyListTest() throws Exception {
            ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220GraphExample.json",Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220Resources.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/exampleDataAssessment.csv");
            OrganizedLearningResourceSuggestions res = ckc.calcIndividualGraphSuggestions("s05");

            List<LearningResourceSuggestion> incomTest = res.incompleteList;
            List<LearningResourceSuggestion> wrongTest = res.wrongList;

            assertEquals(incomTest,new ArrayList<>());

            assertEquals(wrongTest.size(), 5);
            assertEquals(wrongTest.get(0).getId(), "Lab 6: ArrayList and Testing");
            assertEquals(wrongTest.get(1).getId(), "Lab 5: Comparing Searches");
            assertEquals(wrongTest.get(2).getId(), "Lab 7: Linked List");
            assertEquals(wrongTest.get(3).getId(), "Lab 8: Comparing Arrays and Linked Lists");
            assertEquals(wrongTest.get(4).getId(), "Lab 8: Comparing Arrays and Linked Lists");

        } catch (IOException e) {
            fail("Unable to load default files. Test unable to run");
        }

    }


    @Test
    public void calcIndividualSpecificConceptSuggestionsTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
        } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");


        }

        OrganizedLearningResourceSuggestions resource = null;
        try {
            resource = ckc.calcIndividualSpecificConceptSuggestions("bspinache1", "If Statement");

            assertEquals(resource.incompleteList.get(0).getId(),"Q10" );
            assertEquals(resource.incompleteList.get(1).getId(),"Q6" );
            assertEquals(resource.incompleteList.get(2).getId(),"Q3" );

            assertEquals(resource.wrongList.get(0).getId(),"Q9" );
            assertEquals(resource.wrongList.get(1).getId(),"Q1" );
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void addAssignmentTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");

            List<AssessmentItemResponse> originalMasterList = new ArrayList<>();

            CohortConceptGraphs originalGraphs = ckc.getCohortConceptGraphs();
            ConceptGraph conGraph = originalGraphs.getAvgGraph();
            Map<String, AssessmentItem> origLearningMap =  conGraph.getAssessmentItemMap();

            Collection<AssessmentItem> origLearningObList = origLearningMap.values();
            for (AssessmentItem origLearningOb: origLearningObList){
                List<AssessmentItemResponse> origLOR= origLearningOb.getResponses();
                originalMasterList.addAll(origLOR);
            }



            assertEquals(originalMasterList.size(), 11);
            assertEquals(originalMasterList.get(0).getAssessmentItemId(), "Q1");
            assertEquals(originalMasterList.get(1).getAssessmentItemId(), "Q2");
            assertEquals(originalMasterList.get(2).getAssessmentItemId(), "Q4");
            assertEquals(originalMasterList.get(3).getAssessmentItemId(), "Q5");
            assertEquals(originalMasterList.get(4).getAssessmentItemId(), "Q7");
            assertEquals(originalMasterList.get(5).getAssessmentItemId(), "Q8");
            assertEquals(originalMasterList.get(6).getAssessmentItemId(), "Q9");
            assertEquals(originalMasterList.get(7).getAssessmentItemId(), "Q11");
            assertEquals(originalMasterList.get(8).getAssessmentItemId(), "Q12");
            assertEquals(originalMasterList.get(9).getAssessmentItemId(), "Q15");
            assertEquals(originalMasterList.get(10).getAssessmentItemId(), "Q14");


        } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");

        }



        try {
            ckc.addAssessement(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/mediumAssessment.csv");

            List<AssessmentItemResponse> postMasterList1 = new ArrayList<>();

            CohortConceptGraphs postGraphs = ckc.getCohortConceptGraphs();
            ConceptGraph postCG = postGraphs.getAvgGraph();
            Map<String, AssessmentItem> postLOMap =  postCG.getAssessmentItemMap();


            Collection<AssessmentItem> postLOList = postLOMap.values();
            for (AssessmentItem postlearningObject: postLOList){
                List<AssessmentItemResponse> postLOR = postlearningObject.getResponses();
                postMasterList1.addAll(postLOR);
            }


            assertEquals(postMasterList1.size(), 19);
            assertEquals(postMasterList1.get(0).getAssessmentItemId(), "Q1");
            assertEquals(postMasterList1.get(1).getAssessmentItemId(), "Q2");
            assertEquals(postMasterList1.get(2).getAssessmentItemId(), "Q3");
            assertEquals(postMasterList1.get(3).getAssessmentItemId(), "Q4");
            assertEquals(postMasterList1.get(4).getAssessmentItemId(), "Q4");
            assertEquals(postMasterList1.get(5).getAssessmentItemId(), "Q5");
            assertEquals(postMasterList1.get(6).getAssessmentItemId(), "Q5");
            assertEquals(postMasterList1.get(7).getAssessmentItemId(), "Q6");
            assertEquals(postMasterList1.get(8).getAssessmentItemId(), "Q7");
            assertEquals(postMasterList1.get(9).getAssessmentItemId(), "Q7");
            assertEquals(postMasterList1.get(10).getAssessmentItemId(), "Q8");
            assertEquals(postMasterList1.get(11).getAssessmentItemId(), "Q9");
            assertEquals(postMasterList1.get(12).getAssessmentItemId(), "Q9");
            assertEquals(postMasterList1.get(13).getAssessmentItemId(), "Q11");
            assertEquals(postMasterList1.get(14).getAssessmentItemId(), "Q11");
            assertEquals(postMasterList1.get(15).getAssessmentItemId(), "Q12");
            assertEquals(postMasterList1.get(16).getAssessmentItemId(), "Q15");
            assertEquals(postMasterList1.get(17).getAssessmentItemId(), "Q14");
            assertEquals(postMasterList1.get(18).getAssessmentItemId(), "Q14");

        } catch (Exception e) {
            e.printStackTrace();
        }



        try {
            ckc.addAssessement(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleAssessment.csv");
            List<AssessmentItemResponse> postMasterList2 = new ArrayList<>();

            CohortConceptGraphs postGraphs2 = ckc.getCohortConceptGraphs();
            ConceptGraph postCG2 = postGraphs2.getAvgGraph();
            Map<String, AssessmentItem> postLOMap2 =  postCG2.getAssessmentItemMap();

            Collection<AssessmentItem> postLOList2 = postLOMap2.values();
            for (AssessmentItem postlearningObject2: postLOList2){
                List<AssessmentItemResponse> postLOR2 = postlearningObject2.getResponses();
                postMasterList2.addAll(postLOR2);
            }

            assertEquals(postMasterList2.size(), 25);
            assertEquals(postMasterList2.get(0).getAssessmentItemId(), "Q1");
            assertEquals(postMasterList2.get(1).getAssessmentItemId(), "Q1");
            assertEquals(postMasterList2.get(2).getAssessmentItemId(), "Q2");
            assertEquals(postMasterList2.get(3).getAssessmentItemId(), "Q2");
            assertEquals(postMasterList2.get(4).getAssessmentItemId(), "Q3");
            assertEquals(postMasterList2.get(5).getAssessmentItemId(), "Q3");
            assertEquals(postMasterList2.get(6).getAssessmentItemId(), "Q4");
            assertEquals(postMasterList2.get(8).getAssessmentItemId(), "Q4");
            assertEquals(postMasterList2.get(10).getAssessmentItemId(), "Q5");
            assertEquals(postMasterList2.get(12).getAssessmentItemId(), "Q6");
            assertEquals(postMasterList2.get(14).getAssessmentItemId(), "Q7");
            assertEquals(postMasterList2.get(16).getAssessmentItemId(), "Q8");
            assertEquals(postMasterList2.get(18).getAssessmentItemId(), "Q9");
            assertEquals(postMasterList2.get(20).getAssessmentItemId(), "Q11");
            assertEquals(postMasterList2.get(22).getAssessmentItemId(), "Q15");
            assertEquals(postMasterList2.get(23).getAssessmentItemId(), "Q14");
            assertEquals(postMasterList2.get(24).getAssessmentItemId(), "Q14");




        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void removeLORFileTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
            List<String> test = new ArrayList<>();
            test.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
            assertEquals(ckc.currentAssessment(),test);
        } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");
        }


        try {
            ckc.removeAssessmentFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
            List<String> test1 = new ArrayList<>();
            assertEquals(ckc.currentAssessment(),test1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    @Test
//    public void removeEmptyLORList() throws Exception{
//        ConceptKnowledgeCalculatorAPI ckc = null;
//        try {
//            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
//        } catch (Exception e) {
//            fail("Unable to load default files. Test unable to run");
//        }
//        try {
//            ckc.removeAssessmentFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
////            ckc.removeAssessmentFile("test/remove");
////            fail("No more files to remove.");
//// should have some assert statments to check ckc after removing
//        } catch (IOException e) {
//        }
//    }

    @Test
    public void replaceLOFile (){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
            List<String> test = new ArrayList<>();
            test.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json");
            assertEquals(ckc.currentResource(),test);
            ckc.replaceResourceFile(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleChangeNameLOL.json");

            test.clear();
            test.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleChangeNameLOL.json");

            assertEquals(ckc.currentResource(), test);

        } catch (Exception e) {
            fail("Unable to load default files. Test unable to run");
        }
    }

    @Test
    public void addLOFileTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");

            List<AssessmentItem> originalMasterList = new ArrayList<>();

            CohortConceptGraphs originalGraphs = ckc.getCohortConceptGraphs();
            ConceptGraph conGraph = originalGraphs.getAvgGraph();
            Map<String, AssessmentItem> origAssessmentItemMap =  conGraph.getAssessmentItemMap();

            Collection<AssessmentItem> origAssessmentItemList = origAssessmentItemMap.values();

            originalMasterList.addAll(origAssessmentItemList);

            assertEquals(originalMasterList.size(), 15);
            assertEquals(originalMasterList.get(0).getId(), "Q1");
            assertEquals(originalMasterList.get(1).getId(), "Q2");
            assertEquals(originalMasterList.get(2).getId(), "Q3");
            assertEquals(originalMasterList.get(3).getId(), "Q4");
            assertEquals(originalMasterList.get(4).getId(), "Q5");
            assertEquals(originalMasterList.get(5).getId(), "Q6");
            assertEquals(originalMasterList.get(6).getId(), "Q7");
            assertEquals(originalMasterList.get(7).getId(), "Q8");
            assertEquals(originalMasterList.get(8).getId(), "Q9");
            assertEquals(originalMasterList.get(9).getId(), "Q11");
            assertEquals(originalMasterList.get(10).getId(), "Q10");
            assertEquals(originalMasterList.get(11).getId(), "Q13");
            assertEquals(originalMasterList.get(12).getId(), "Q12");
            assertEquals(originalMasterList.get(13).getId(), "Q15");
            assertEquals(originalMasterList.get(14).getId(), "Q14");
        } catch (IOException | CsvException e) {
            fail("Unable to load default files. Test unable to run");

        }

        try {
            ckc.addResource(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleChangeNameLOL.json");

            List<AssessmentItem> postMasterList = new ArrayList<>();

            CohortConceptGraphs postGraphs = ckc.getCohortConceptGraphs();
            ConceptGraph postCG = postGraphs.getAvgGraph();
            Map<String, AssessmentItem> postLOMap =  postCG.getAssessmentItemMap();
            Collection<AssessmentItem> postLOList = postLOMap.values();
            postMasterList.addAll(postLOList);

            assertEquals(postMasterList.size(), 15);
            assertEquals(postMasterList.get(0).getId(), "Q1");
            assertEquals(postMasterList.get(1).getId(), "Q2");
            assertEquals(postMasterList.get(2).getId(), "Q5");
            assertEquals(postMasterList.get(3).getId(), "Q7");
            assertEquals(postMasterList.get(4).getId(), "Q8");
            assertEquals(postMasterList.get(5).getId(), "resource2");
            assertEquals(postMasterList.get(6).getId(), "Q9");
            assertEquals(postMasterList.get(7).getId(), "resource3");
            assertEquals(postMasterList.get(8).getId(), "resource4");
            assertEquals(postMasterList.get(9).getId(), "resource5");
            assertEquals(postMasterList.get(10).getId(), "resource1");
            assertEquals(postMasterList.get(11).getId(), "Q10");
            assertEquals(postMasterList.get(12).getId(), "Q13");
            assertEquals(postMasterList.get(13).getId(), "Q12");
            assertEquals(postMasterList.get(14).getId(), "Q14");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void getConceptAvgTest(){
//        ConceptKnowledgeCalculatorAPI ckc = null;
//        try {
//            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
//        } catch (IOException e) {
//            fail("Unable to load default files");
//
//        }
//        try {
//            assertEquals(1, ckc.getLearningObjectAvg("Q4"), 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            assertEquals(0.75, ckc.getLearningObjectAvg("Q14"), 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Test
    public void modeTest(){

        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            List<String> getTest = new ArrayList<>();

            //COHORT MODE
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");

            assertEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.COHORTGRAPH);
            //the constructor starts out as a cohort Concept graph and there should only be one file in each of the lists of files. The structure graph should be null, because we're not on that mode
            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");
            assertEquals(ckc.currentStructure(),getTest );
            getTest.clear();

            getTest.add( Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json");
            assertEquals(ckc.currentResource(), getTest);
            getTest.clear();

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
            assertEquals(ckc.currentAssessment(), getTest);

            assertEquals(ckc.getStructureGraph(), null);
            assertNotEquals(ckc.getCohortConceptGraphs(), null);
            getTest.clear();


            //STRUCTURE MODE
            //now in structure mode. Because of this cohort Concept graph should be null as well as all the extra data (LO and LOR files). Structure graph should not be null now because we are in that mode.
            //it should hold on to blank resource and assessment files. The structure file should have one file in it.
            ckc.switchToStructure();
            assertEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.STRUCTUREGRAPH);

            assertEquals(ckc.getCohortConceptGraphs(), null);

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");
            assertEquals(ckc.currentStructure(), getTest);

            assertEquals(ckc.currentResource(), new ArrayList<>());
            assertEquals(ckc.currentAssessment(), new ArrayList<>());

            assertNotEquals(ckc.getStructureGraph(), null);
            assertEquals(ckc.getCohortConceptGraphs(), null);
            getTest.clear();


            //STRUCTURE MODE WITH ASSESSMENT
            //switched to structure mode with assessment. This should still have a structure graph, but then also hold on to the files. Because additional LOR was called from structure graph mode, the assessment file should not be filled with the one file.
            ckc.addAssessement(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/mediumAssessment.csv");
            assertEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.STRUCTUREGRAPHWITHASSESSMENT);

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");
            assertEquals(ckc.currentStructure(), getTest);
            getTest.clear();

            assertEquals(ckc.currentResource(), new ArrayList<>());

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/mediumAssessment.csv");
            assertEquals(ckc.currentAssessment(), getTest);

            assertNotEquals(ckc.getStructureGraph(), null);
            assertEquals(ckc.getCohortConceptGraphs(), null);
            getTest.clear();


            //CONCEPT GRAPH MODE
            //switched back into Concept graph mode because now the three lists of files are filled up with at least one file.
            //all of the previous files should be stored in structure files, resource files, and assessment files
            ckc.addResource(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleResource.json");
            assertEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.COHORTGRAPH);
            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");
            assertEquals(ckc.currentStructure(), getTest);
            getTest.clear();

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleResource.json");
            assertEquals(ckc.currentResource(), getTest);
            getTest.clear();

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/mediumAssessment.csv");
            assertEquals(ckc.currentAssessment(),getTest);
            getTest.clear();

            assertEquals(ckc.getStructureGraph(), null);
            assertNotEquals(ckc.getCohortConceptGraphs(), null);




            //STRUCTURE GRAPH WITH RESOURCE
            //to test more, going from cohort graph to structure graph.
            // changing from structure graph to structure graph with resources. Cohort graph should be null and structure graph shouldn't be. The structure file should have one file in it and resource file list should have one in it. The assessment file should be empty
            ckc.switchToStructure();

            ckc.addResource(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleResource.json");
            assertEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.STRUCTUREGRAPHWITHRESOURCE);
            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");
            assertEquals(ckc.currentStructure(), getTest);
            getTest.clear();

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleResource.json");
            assertEquals(ckc.currentResource(),  getTest);
            getTest.clear();

            assertEquals(ckc.currentAssessment(), new ArrayList<>());
            assertNotEquals(ckc.getStructureGraph(), null);
            assertEquals(ckc.getCohortConceptGraphs(), null);


            //COHORT GRAPH
            //this is switching from structure graph mode with resources to cohort graph mode. All the lists of files should have at least one file in them and structure graph should equal null;
            ckc.addAssessement(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleAssessment.csv");
            assertEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.COHORTGRAPH);

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");
            assertEquals(ckc.currentStructure(), getTest);
            getTest.clear();

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleResource.json");
            assertEquals(ckc.currentResource(),  getTest);
            getTest.clear();

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleAssessment.csv");
            assertEquals(ckc.currentAssessment(),getTest);
            getTest.clear();

            assertEquals(ckc.getStructureGraph(), null);
            assertNotEquals(ckc.getCohortConceptGraphs(), null);



//            //STRUCTURE GRAPH WITH RESOURCE
//            //to further testing, going back to structure more
//            //to switch from structure graph with resource, adding LOR makes all the file lists have at least one file in them, thus a cohort graph can be made. Structure graph should not equal null and all file lists should have one file in them
            ckc.switchToStructure();

            ckc.addResourceAndAssessment(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleAssessment.csv" );
            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json");
            assertEquals(ckc.currentStructure(), getTest);
            getTest.clear();

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleResource.json");
            assertEquals(ckc.currentResource(), getTest);
            getTest.clear();

            getTest.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/simpleAssessment.csv" );
            assertEquals(ckc.currentAssessment(), getTest);

            assertEquals(ckc.getStructureGraph(), null);
            assertNotEquals(ckc.getCohortConceptGraphs(), null);


        } catch (Exception e) {
            fail("Unable to load default files. Test unable to run");

        }
    }

    @Test
    public void noDataMode(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator("test.json", "test.json", "test.csv");
            assertNotEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.NODATA);

        } catch (IOException | CsvException e) {
            ckc = new ConceptKnowledgeCalculator();
            assertEquals(ckc.getCurrentMode(), ConceptKnowledgeCalculator.Mode.NODATA);
        }
    }


    @Test
    public void getUserListTest() {
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
        } catch (IOException | CsvException e) {
            fail("Unable to load default files" + ErrorUtil.errorToStr(e));
        }
        List<String> actualList = new ArrayList<>();
        actualList.add("bspinache1");
        try {
            assertEquals(actualList, ckc.getUserIdList());
        }catch (Exception e){
            fail();
        }


    }

    //TODO: Fix StudentAvgTest. Stopped working once  implementing factor analysis and column headers in RMatrix
    /**
    @Test
    public void getStudentAvgTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessment.csv");
        } catch (IOException e) {
            fail("Unable to load default files");
        }
        assertEquals(0.502, ckc.getStudentAvg("bspinache1"), OK_FLOAT_MARGIN);
    }
    */


    @Test
    public void csvToResourceTest(){
        try {
            String testFilepath = Settings.TEST_RESOURCE_DIR + "practicalExamples/SystemCreated/mediumGraphLearnObjectLinkRecordsCreationTest.json";

            List<String> csvFiles = new ArrayList<>();
            csvFiles.add(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/mediumAssessment.csv");
            ConceptKnowledgeCalculator.csvToResource(csvFiles, testFilepath);

            List<LearningResourceRecord> recordsFromFile = LearningResourceRecord.createLearningResourceRecordsFromJsonFile(testFilepath);
            assertNotNull(recordsFromFile);
            //TODO:test that these LRRecords are good compared to the input csv file, they just won't have any concepts in their lists
            LearningResourceRecord currRec = recordsFromFile.get(0);
            assertEquals("Q1", currRec.getLearningResourceId());
            LearningResourceRecord nextRec = recordsFromFile.get(13);
            assertEquals("Q14", nextRec.getLearningResourceId());

        }catch (Exception e){
            e.printStackTrace();
            fail("File(s) not found");

        }


    }

//This function is tested through output reading and should only be uncommented during debugging
    /**
    @Test
    public void getFactorMatrixTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticResource.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/basicRealisticAssessmentMoreUsers.csv");
        } catch (IOException e) {
            fail("Unable to load default files");
        }
        ckc.getFactorMatrix();

    }
*/

    //calls void function that creates a graph. Tested through output

    /**
    @Test
    public void createConfirmatoryGraphTest(){
        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220GraphExample.json",
    Settings.TEST_RESOURCE_DIR + "ManuallyCreated/comp220Resources.json",
    Settings.PRIVATE_RESOURCE_DIR +"/comp220/comp220ExampleDataPortionCleaned.csv");
            ckc.createConfirmatoryGraph();
        } catch (Exception e) {
            fail("Unable to read assessment file");
        }
    }
*/

    @Test
    public void research1test() {

        ConceptKnowledgeCalculatorAPI ckc = null;
        try {
            List<String> getTest = new ArrayList<>();

            //COHORT MODE
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchResource1.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchAssessment1.csv");

            ckc.getCohortGraphsUrl();

            OrganizedLearningResourceSuggestions sug = ckc.calcIndividualGraphSuggestions("s1");

            List<LearningResourceSuggestion> wrongTest = sug.wrongList;
            List<LearningResourceSuggestion> incomTest = sug.incompleteList;

            assertEquals(incomTest.size(), 0);

            assertEquals(wrongTest.size(), 6);
            assertEquals(wrongTest.get(0).getId(), "What is the proper while loop diction?");
            assertEquals(wrongTest.get(1).getId(), "What is the proper for loop diction?");
            assertEquals(wrongTest.get(2).getId(), "Are strings mutable?");
            assertEquals(wrongTest.get(3).getId(), "What are the things you need for a while loop?");
            assertEquals(wrongTest.get(4).getId(), "What are the differences and similarities between for loops and while loops?");
            assertEquals(wrongTest.get(5).getId(), "What are the differences and similarities between for loops and while loops?");

        } catch (Exception e) {
            fail("Unable to load default files" + ErrorUtil.errorToStr(e));
        }
    }



    @Test
    public void research2test() {
        ConceptKnowledgeCalculatorAPI ckc = null;

        try {
            ckc = new ConceptKnowledgeCalculator(Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchConceptGraph.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchResource2.json", Settings.TEST_RESOURCE_DIR + "ManuallyCreated/researchAssessment2.csv");

            OrganizedLearningResourceSuggestions sug2 = ckc.calcIndividualGraphSuggestions("s2") ;

            List<LearningResourceSuggestion> wrongTest2 = sug2.wrongList;
            List<LearningResourceSuggestion> incomTest2 = sug2.incompleteList;


            ckc.getCohortGraphsUrl();

            assertEquals(incomTest2.size(), 1);
            assertEquals(incomTest2.get(0).getId(), "What are the differences and similarities between for loops and while loops?");

            assertEquals(wrongTest2.size(), 3);
            assertEquals(wrongTest2.get(0).getId(), "Describe how the following relate to a for loop: variable, terminating condition, loop body");
            assertEquals(wrongTest2.get(1).getId(), "Describe how while loops can depend on booleans");
            assertEquals(wrongTest2.get(2).getId(), "Write a function that will calculate if the substring 'the' is in any user input");

        }catch (Exception e){
            fail("Unable to load default files" + ErrorUtil.errorToStr(e));
        }
    }



}
