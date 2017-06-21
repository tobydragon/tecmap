package edu.ithaca.dragonlab.ckc.ui;

import edu.ithaca.dragonlab.ckc.ConceptKnowledgeCalculator;
import edu.ithaca.dragonlab.ckc.ConceptKnowledgeCalculatorAPI;
import edu.ithaca.dragonlab.ckc.conceptgraph.ConceptNode;
import edu.ithaca.dragonlab.ckc.suggester.LearningObjectSuggestion;
import edu.ithaca.dragonlab.ckc.suggester.SuggestionResource;

import java.io.IOException;
import java.util.*;

/**
 * Created by tdragon on 6/8/17.
 */
public class ConsoleUI {
    private ConceptKnowledgeCalculatorAPI ckc;


    public ConsoleUI(String structureFileName) {
        try {
            ckc = new ConceptKnowledgeCalculator(structureFileName);
        } catch (Exception e) {
            System.out.println("Unable to load default files, please choose files manually. Error follows:");
            e.printStackTrace();
            ckc = new ConceptKnowledgeCalculator();
        }
        run();
    }

    public ConsoleUI(String structureFilename, String resourceFilename, String assessmentFilename) {
        try {
            ckc = new ConceptKnowledgeCalculator(structureFilename, resourceFilename, assessmentFilename);
        } catch (Exception e) {
            System.out.println("Unable to load default files, please choose files manually. Error follows:");
            e.printStackTrace();
            ckc = new ConceptKnowledgeCalculator();
        }
        run();
    }


    public void run(){

        if(ckc.getCohortConceptGraphs()!=null || ckc.getStructureGraph()!= null) {
            System.out.println("Current graphs:\t" + ckc.getCohortGraphsUrl());
        }


        Scanner scanner = new Scanner(System.in);
        ckc.setLastWorkingStructureName(ckc.getStructureFileName());

        ckc.setPreviouslySavedCohortFiles(ckc.getSavedCohortFile());

        int contQuit = 1;
        while (contQuit == 1) {
            ConceptKnowledgeCalculator.Mode mode = ckc.getCurrentmode();

            if (mode == ConceptKnowledgeCalculator.Mode.STRUCTUREGRAPH) {

                System.out.println("What do you want to do? \n 1- replace graph \n 2- add Learning Object and Learning Object Resources");
                Integer num = scanner.nextInt();

                while (num < 1 || num > 2) {
                    System.out.println("Out of bounds");
                    System.out.println("What do you want to do? \n 1- replace graph \n 2- add Learning Object and Learning Object Resources");
                    num = scanner.nextInt();
                }

                scanner.nextLine();

                if (num==1){
                    replaceGraph(scanner, true);

                }else{
                    addLOAndLOR(scanner);
                }


            } else {

                System.out.println("What do you want to do? \n 1 - calculate a list of concept nodes to work on \n 2 - calculate learning object suggestions based on a specific concept \n 3 - automatically calculate suggestions \n 4 - View graph \n 5 - Create new graph \n 6 - Replace graph file \n 7 - Add another assessment file \n 8 - Add another LO file \n 9 - Get Learning Object Average \n 10 - View Structure Graph");
                Integer num = scanner.nextInt();

                while (num < 1 || num > 10) {
                    System.out.println("Out of bounds");
                    System.out.println("What do you want to do? \n 1 - calculate a list of concept nodes to work on \n 2 - calculate learning object suggestions based on a specific concept \n 3 - automatically calculate suggestions \n 4 - View graph \n 5 - Create new graph \n 6 - Replace graph file \n 7 - Add another assessment file \n 8 - Add another LO file \n 9 - Get Learning Object Average \n 10 - View Structure Graph");
                    num = scanner.nextInt();
                }

                scanner.nextLine();

                if (num == 1) {
                    createLearningObjectList(scanner);
                }
                else if (num == 2) {
                    specificLearningObjectSuggestion( scanner);
                }
                else if (num == 3) {
                    graphSuggestions(scanner);
                }
                else if (num == 4) {
                    viewgraph();
                }
                else  if(num ==5){
                    createNewCohortGraph(scanner, true );
                }
                else if(num ==6){
                    replaceGraphFile(scanner);
                }
                else if(num ==7){
                    additionalLOR(scanner);
                }
                else if (num ==8) {
                    addLOFile(scanner);

                }
                else if (num ==9) {
                    resourceAverage(scanner);
                }
                else{
                    switchToStructuremode();
                }
            }

            //to repeat or break loop in graph mode or structure mode
            System.out.println("1- continue 2- quit");
            contQuit = scanner.nextInt();

            while (contQuit != 1 && contQuit != 2) {
                System.out.println("Out of bounds");
                System.out.println("1- continue 2- quit");
                contQuit = scanner.nextInt();
            }

        }


    }

    public void resourceAverage(Scanner scanner){
        System.out.println("Get average learning object Grade");

        System.out.println("What learning object do you want to calculate?");
        String conceptNode = scanner.nextLine();
        System.out.println("The average is: " + ckc.getLearningObjectAvg(conceptNode));

    }



    public void replaceGraph(Scanner scanner, boolean structFileNameValid){
        System.out.println("replace graph");

        System.out.println("What file do you want to replace with?");
        String file = scanner.nextLine();

        try {
            ckc.clearAndCreateStructureData(file);
            System.out.println("Process Completed");


        } catch (Exception e) {
            System.out.println("Cannot find the file");
            structFileNameValid=false;
            try {
                ckc.clearAndCreateStructureData(ckc.getLastWorkingStructureName());
                ckc.setStructureFileName(ckc.getLastWorkingStructureName());
                System.out.println("Last working files used");
            } catch (IOException e1) {
                e1.printStackTrace();
                System.out.println("Error");
            }
        }

        if(structFileNameValid){
            ckc.setLastWorkingStructureName(ckc.getStructureFileName());
        }
    }


    public void addLOAndLOR(Scanner scanner){
        System.out.println("add LO and LOR");

        System.out.println("Type learning Object File (starting from root)");
        String LOfile = scanner.nextLine();

        System.out.println("Type learning Object Resources File (starting from root)");
        String LORFile = scanner.nextLine();

        String strucFile = ckc.getStructureFileName();

        try {
            ckc.clearAndCreateCohortData(strucFile, LOfile, LORFile);
            ckc.setCurrentMode(ConceptKnowledgeCalculator.Mode.COHORTGRAPH);
            System.out.println("Process Completed");

        } catch (Exception e) {
            System.out.println("Cannot find files");

        }

    }

    public void createLearningObjectList(Scanner scanner){
        System.out.println("calculate a list of concept nodes to work ");
        System.out.println("User ID");
        String userID = scanner.nextLine();

        try {
            List<String> suggestedConceptNodeList = ckc.calcIndividualConceptNodesSuggestions(userID);

            String st = "";
            for(String node: suggestedConceptNodeList){
                st+= node+ "\n";
            }

            System.out.println(st);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void specificLearningObjectSuggestion(Scanner scanner){
        System.out.println("calculate learning object suggestions based on a specific concept");
        System.out.println("User ID");
        String userID = scanner.nextLine();

        System.out.println("Concept ID");
        String conceptID = scanner.nextLine();
        SuggestionResource sugRes;
        try {
            sugRes = ckc.calcIndividualSpecificConceptSuggestions(userID, conceptID);
            System.out.println(" 1- Try something new 2- try something again");
            Integer option = scanner.nextInt();
            while (option > 2 || option < 1) {
                System.out.println(" 1- Try something new 2- try something again");
                option = scanner.nextInt();
            }
            if (option == 1) {
                System.out.println(sugRes.toString(0));
            } else {
                System.out.println(sugRes.toString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void graphSuggestions(Scanner scanner){
        System.out.println("automatically calculate suggestions");
        System.out.println("User ID");
        String userID = scanner.nextLine();
        try {
            SuggestionResource sugRes = ckc.calcIndividualGraphSuggestions(userID);
            System.out.println(" 1- Try something new 2- try something again");
            Integer option = scanner.nextInt();
            while (option > 2 || option < 1) {
                System.out.println(" 1- Try something new 2- try something again");
                option = scanner.nextInt();
            }
            if (option == 1) {
                System.out.println(sugRes.toString(0));

            } else {
                System.out.println(sugRes.toString(1));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void viewgraph(){
        System.out.println("view graph");

        ckc.getCohortGraphsUrl();

    }

    public void createNewCohortGraph(Scanner scanner, Boolean cohortFilesValid){
        System.out.println("create new cohort graphs");

        System.out.println("Type concept graph path: ");
        String structure = scanner.nextLine();

        System.out.println("Type learning object record path: ");
        String resource = scanner.nextLine();

        System.out.println("Type grade book path: ");
        String assessment = scanner.nextLine();

        try {
            ckc.clearAndCreateCohortData(structure, resource, assessment);
            System.out.println("Process Completed");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't find files");
            cohortFilesValid=false;
            try{
                List<String> fileList =ckc.getPreviouslySavedCohortFile();
                ckc.clearAndCreateCohortData(fileList.get(0),fileList.get(1), fileList.get(2));
                System.out.println("Last Working Files Used");
            }catch (IOException e1){
                e1.printStackTrace();
            }
        }

        if(cohortFilesValid){
            ckc.setPreviouslySavedCohortFiles(ckc.getSavedCohortFile());
        }
    }

    public void replaceGraphFile(Scanner scanner){
        System.out.println("Replace Graph File");



        System.out.println("Type Graph Path ");
        String graph = scanner.nextLine();

        if(ckc.gethasMultipleAssessment()){
            System.out.println("You're using more than one assessment file. Using only the original assessment file ");
        }

        if(ckc.getHasMultipleResource()){
            System.out.println("You're using more than one resource file. Using only the original resource file");
        }

        try {
            ckc.clearAndCreateCohortData(graph, ckc.getResourceFile(), ckc.getAssessmentFile());
            ckc.setStructureFileName(graph);
            System.out.println("Process Completed");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't find files");


        }
    }



    public void switchToStructuremode(){
        System.out.println("View Structure graph");

        try {
            ckc.clearAndCreateStructureData(ckc.getStructureFileName());
            ckc.setCurrentMode(ConceptKnowledgeCalculator.Mode.STRUCTUREGRAPH);
            System.out.println("Process Completed");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't find file");
        }

    }

    public void additionalLOR(Scanner scanner){
        System.out.println("Add another LOR to existing graph ");

        System.out.println("Type LOR path (from root)");
        String LOR = scanner.nextLine();

        try {
            ckc.additionalLOR(LOR);
            System.out.println("Process Completed");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't find file");
        }

    }


    private void addLOFile(Scanner scanner) {
        System.out.println("Add another Learning Object File");
        System.out.println("Type file path (root to file)");
        String file = scanner.nextLine();

        try{
            ckc.addAnotherLO(file);
            System.out.println("Process Completed");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Can't find file");
        }

    }


}