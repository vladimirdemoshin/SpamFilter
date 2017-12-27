package dictionaryapp;
import java.util.*;
public class BayesClassifier {
    public DictionaryManager manager;
    public final double STANDART_PROBABILITY = 0.5, WEIGHT = 1, BAD_FILTRATION_FACTOR = 2, GOOD_FILTRATION_FACTOR = 1;
    public BayesClassifier(DictionaryManager manager)
    {
        this.manager = manager;
    }
    //считает условную вероятность
    public double conditionalProbability(String feature, String category)
    {
        int countFeatureInCategory = manager.getFeatureValue(feature,category);
        int countMessagesInCategory = manager.getCategoryCount(category); 
        return (countFeatureInCategory*1.0)/(countMessagesInCategory*1.0);
    }
    //считает взвешенную вероятность
    public double weightedProbability(String feature,String category) 
    {
        int Nx = manager.getFeatureValue(feature,category);
        double result = WEIGHT * STANDART_PROBABILITY + Nx * conditionalProbability(feature,category);
        result /= Nx+WEIGHT;
        return result;
    }
    //принятие решения о категории сообщения 
     public String naiveBayesClassifier(String document) 
    {
        String[] features = manager.splitMessage(document);
        Set<String> allCategories = manager.categories.keySet();
        HashMap<String,Double> categoryDocumentProbability = new HashMap<String,Double>();
        for(String category : allCategories)
        {
             //System.out.println(category);
            double multiP = 1;
            for(int i=0;i<features.length;i++)
                multiP *= conditionalProbability(features[i],category);// weightedProbability(features[i],category);//conditionalProbability(features[i],category);
             //System.out.println(multiP);
            categoryDocumentProbability.put(category, multiP * manager.getCategoryCount(category));
        }
//        double goodCategoryProbability = categoryDocumentProbability.get("good");
//        System.out.println(goodCategoryProbability);
//        double badCategoryProbability = categoryDocumentProbability.get("bad");
//        System.out.println(badCategoryProbability);
//        if(goodCategoryProbability > badCategoryProbability) 
//        {
//            if(goodCategoryProbability > GOOD_FILTRATION_FACTOR*badCategoryProbability)
//            {
//                System.out.println("good");
//                return "good";
//            } 
//            else
//            {
//                System.out.println("unknown");
//                return "unknown";
//            }
//        }
//        else 
//        {
//            if(badCategoryProbability > BAD_FILTRATION_FACTOR*goodCategoryProbability)
//            {
//                System.out.println("bad");
//                return "bad";
//            } 
//            else
//            {
//                System.out.println("unknown");
//                return "unknown";
//            }
//        }
        double goodP = categoryDocumentProbability.get("good");
        double badP = categoryDocumentProbability.get("bad");
        String outputMessage = goodP > badP ? "good" : "bad";
        if(badP>goodP)//if(Math.max(goodP, badP)>Math.min(goodP, badP)*BAD_FILTRATION_FACTOR)
        {
            //System.out.println(outputMessage);
            return "bad";//outputMessage;
        }
        else
        {
            //System.out.println("unknown");
            return "good";
        }
    }
}
