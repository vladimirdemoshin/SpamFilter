package dictionaryapp;
import java.util.*;
public class FisherClassifier {
    public DictionaryManager manager;
    public final double BAD_FILTRATION_LEVEL = 0.5, GOOD_FILTRATION_LEVEL = 0.5;
    public FisherClassifier(DictionaryManager manager)
    {
        this.manager = manager;
    }
    //считает значение вероятности по формуле фишера
    public double FisherFormula(String category, String document)
    {
        String[] features = manager.splitMessage(document);
        double probabilityMultiplication = 1;
        for(int i=0;i<features.length;i++)
        {
            int countFeatureInCategory = manager.getFeatureValue(features[i], category);
            Set<String> allCategories = manager.categories.keySet();
            int countFeatureInAllCategories = 0;
            for(String currentCategory : allCategories)
                countFeatureInAllCategories += manager.getFeatureValue(features[i], currentCategory);
            double currentProbability = (countFeatureInCategory*1.0)/(countFeatureInAllCategories*1.0);
            probabilityMultiplication *= currentProbability;
        }
        return chi2inv(-2*Math.log(probabilityMultiplication),2*features.length);
    }
    //функция обратного распределния хи-квадрат
    private double chi2inv(double chi,int df)
    {
	double m = chi/2, term = Math.exp(-m), sum = Math.exp(-m);
	for(int i=1;i<=df/2;i++)
        {
	    term *= m/i;
	    sum += term;
	}
	return Math.min(sum,1.0);
    }
    //результат классификации
    public String classificationResult(String document)
    {
        double goodCategoryProbability = FisherFormula("good",document);
        double badCategoryProbability = FisherFormula("bad",document);
        if(goodCategoryProbability > badCategoryProbability)
        {
            if(goodCategoryProbability > GOOD_FILTRATION_LEVEL) return "good";
            else if (badCategoryProbability > BAD_FILTRATION_LEVEL) return "bad";
            else return "unknown";
        }
        else
        {
            if (badCategoryProbability > BAD_FILTRATION_LEVEL) return "bad";
            else if(goodCategoryProbability > GOOD_FILTRATION_LEVEL) return "good";
            else return "unknown";
        }
    }
}
