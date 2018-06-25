package edu.ithaca.dragon.tecmap.prediction;

import ch.netzwerg.paleo.*;
import de.daslaboratorium.machinelearning.classifier.Classifier;
import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import edu.ithaca.dragon.tecmap.conceptgraph.eval.KnowledgeEstimateMatrix;
import edu.ithaca.dragon.tecmap.learningresource.AssessmentItem;
import io.vavr.collection.Iterator;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.DoubleStream;

public class BayesPredictor implements Predictor {

    private Classifier<Double, String> bayesClassifier;

    /**
     * Default constructor
     * Sets memory capacity to 500 as a default
     */
    public BayesPredictor() {
        bayesClassifier = new BayesClassifier<>();
        bayesClassifier.setMemoryCapacity(500);
    }

    private static ColumnId getColumnId(DataFrame dataFrame, String columnId) {
        for (ColumnId colId : dataFrame.getColumnIds()) {
            if (colId.getName().equals(columnId)) {
                return colId;
            }
        }
        return null;
    }

    /**
     * Converts KnowledgeEstimateMatrix into a Dataframe
     * @param rawData
     * @return DataFrame that is easier to use (IN ORDER TO GET VALUES, YOU MUST USE THE COLUMNID TYPE THAT IS DICTATED BY THE DATAFRAME
     */
    public static DataFrame toDataFrame(KnowledgeEstimateMatrix rawData) {
        if (rawData != null) {
            //Column for Student IDs
            List<String> studentIDs = rawData.getUserIdList();
            StringColumn studentIDCol = StringColumn.ofAll(ColumnIds.StringColumnId.of("Students"), studentIDs);

            //List of Columns for Each Assessment
            List<DoubleColumn> assessmentCols = new ArrayList<>();
            List<String> assessmentIDs = new ArrayList<>(); //list of IDs for each assessment
            for (AssessmentItem assessment : rawData.getObjList()) {
                assessmentIDs.add(assessment.getId());
            }

            double[][] assessmentMatrix = rawData.getStudentKnowledgeEstimates();
            for (int i = 0; i < assessmentMatrix.length; i++) {
                String currAssessmentId = assessmentIDs.get(i);
                DoubleColumn assessmentCol = DoubleColumn.ofAll(ColumnIds.DoubleColumnId.of(currAssessmentId), DoubleStream.of(assessmentMatrix[i]));
                assessmentCols.add(assessmentCol);
            }

            List<Column<?>> allCols = new ArrayList<>();
            allCols.add(studentIDCol);
            allCols.addAll(assessmentCols);

            return DataFrame.ofAll(allCols);

        } else {
            return null;
        }
    }

    /**
     * Discretizes a column for an assignment (type double) and gives back a dataframe with that column replaced by a
     * categorical variable
     * @param original
     * @param assessmentId
     * @return new DataFrame with a category column replacing the double column, null if assessmentId doesn't match any column
     */
    public static DataFrame discretizeGradeColumn(@NotNull DataFrame original, String assessmentId) {
        Iterator<ColumnId> columnIds = original.getColumnIds().iterator();
        ColumnId assessmentColumnId = null;
        assessmentColumnId = getColumnId(original, assessmentId);
        if (assessmentColumnId == null || assessmentColumnId.getType() != ColumnType.DOUBLE) {
            return null;
        } else {
            DoubleColumn assessmentColumn = (DoubleColumn) original.getColumn(assessmentColumnId);
            List<String> discreteValues = new ArrayList<>();
            for (Double grade : assessmentColumn.valueStream().toArray()) {
                if (grade <= 0.70) {
                    discreteValues.add("AT-RISK");
                } else {
                    discreteValues.add("OK");
                }
            }
            CategoryColumn discreteColumn = CategoryColumn.ofAll(ColumnIds.CategoryColumnId.of(assessmentId), discreteValues);

            List<Column<?>> allCols = new ArrayList<>();
            for (Column<?> column : original.iterator()) {
                if (column.getId() != assessmentColumnId) {
                    allCols.add(column);
                }
            }
            allCols.add(discreteColumn);

            return DataFrame.ofAll(allCols);
        }
    }

    /**
     * Gets rows from the dataframe
     * @param learningData Dataframe to be parsed
     * @param discreteColumn The column that is discretized
     * @return a Map of studentId -> (a Map of category -> Collection of Grades)
     */
    public static Map<String, Map<String,Collection<Double>>> getRowsToLearn(@NotNull DataFrame learningData, String discreteColumn) {
        Map<String, Map<String, Collection<Double>>> dataframeRows = new HashMap<>();
        for (int i = 0; i < learningData.getRowCount(); i++) {
            Map<String, Collection<Double>> currentRow = new HashMap<>();

            //Get the student ID for the first string in the map
            ColumnIds.StringColumnId studentIdColumn = (ColumnIds.StringColumnId) getColumnId(learningData, "Students");
            String studentId = learningData.getValueAt(i, studentIdColumn);

            //Get the category string for the currentRow map
            ColumnIds.CategoryColumnId discreteColumnId = (ColumnIds.CategoryColumnId) getColumnId(learningData, discreteColumn);
            String currentCategory = learningData.getValueAt(i, discreteColumnId);

            //Get the rest of the grades
            Collection<Double> grades = new ArrayList<>();
            List<ColumnIds.DoubleColumnId> columnIds = new ArrayList<>();
            for (ColumnId columnId : learningData.getColumnIds()) {
                if (columnId.getType() == ColumnType.DOUBLE) {
                    columnIds.add((ColumnIds.DoubleColumnId) columnId);
                }
            }
            for (ColumnIds.DoubleColumnId columnId : columnIds) {
                grades.add(learningData.getValueAt(i, columnId));
            }

            currentRow.put(currentCategory, grades);
            dataframeRows.put(studentId, currentRow);
        }

        return dataframeRows;
    }

    /**
     * Resets and unlearns everything, resets category count
     * TESTED ON PACKAGE BUILD, NOT NATIVELY
     */
    public void reset() {
        bayesClassifier.reset();
    }

    /**
     * Learns given the necessary data, category that assignment belongs to based on features given
     * @param category
     * @param grades
     * TESTED ON PACKAGE BUILD, NOT NATIVELY
     */
    public void learn(String category, Collection<Double> grades) {
        bayesClassifier.learn(category, grades);
    }

    /**
     * Trains the data based on the raw data matrix you give it
     * @param rawTrainingData in the form of KnowledgeEstimateMatrix
     * @param assessmentId string of the assessment you want to learn on (will be the categorical variable), should have doubles as grade type
     * TRAINING DATA MUST BE MANIPULATED IN ORDER TO USE THE BAYES LEARN METHOD
     */
    public void learnSet(KnowledgeEstimateMatrix rawTrainingData, String assessmentId) {

    }

    /**
     * classifies a single "row" of data in the form of a collection
     * @param testData a "row" of doubles(grades) for a single student
     * Uses Bayes Classify Method
     * TESTED ON PACKAGE BUILD, NOT NATIVELY
     * @return String classification for a student
     */
    private String classify(Collection<Double> testData) {
        return bayesClassifier.classify(testData).getCategory();
    }

    /**
     * Classifies the data based on the raw testing matrix you give it
     * @param rawTestingData in the form of KnowledgeEstimateMatrix
     * TESTING DATA MUST BE MANIPULATED IN ORDER TO GET ROWS FOR THE BAYES CLASSIFY METHOD
     * @return Map of String to String (Student id -> Classification) MAY CHANGE
     */
    public Map<String, String> classifySet(KnowledgeEstimateMatrix rawTestingData) {
        return null;
    }
}
