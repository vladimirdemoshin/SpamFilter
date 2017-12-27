package dictionaryapp;
import java.util.*;
public class NeuralNetwork {
    private DictionaryManager manager;
    private final int N; // количество нейронов во входном и скрытом слоях
    public final int MAX_EPOCH = 300;
    private final double EPSILON = 0.1; // приемлимая величина для квадратичной ошибки
    private final double E = 0.8; // скорость обучения
    private final double A = 0.1; // момент
    //структуры данных для хранения значений слоев
    private  HashMap<String,Integer> inputLayer; //значения входного слоя
    private double[] hiddenLayer; //значения скрытого слоя
    private double outputLayer; //в выходном слое только один нейрон
    //структуры данных для хранения весов
    private HashMap<String,double[]> fromInputToHiddenWeights;
    private double[] fromHiddenToOutputWeights;
    //структуры данных для хранения значений дельта
    private double[] hiddenDelta;
    private double outputDelta;
    //структуры данных для хранения значений градиента
    private HashMap<String,double[]> fromInputToHiddenGradients;
    private double[] fromHiddenToOutputGradients;
    //структуры данных для хранения значений изменений веса
    private HashMap<String,double[]> fromInputToHiddenDifferences;
    private double[] fromHiddenToOutputDifferences;
    public NeuralNetwork(DictionaryManager manager)
    {
        this.manager = manager;
        N = this.manager.features.size();
        createLayers();
        createWeights();
        createDeltas();
        createGradients();
        createDifferences();
    }
    private void createLayers()
    {
        inputLayer = new HashMap<String,Integer>();
        Set<String> allFeatures = manager.features.keySet();
        for(String feature : allFeatures) { inputLayer.put(feature,0); }
        hiddenLayer = new double[N];
    }
    private void createWeights()
    {
        Random rand = new Random();
        fromInputToHiddenWeights = new HashMap<String,double[]>();
        Set<String> allFeatures = manager.features.keySet();
        for(String feature : allFeatures)
        {
            fromInputToHiddenWeights.put(feature,new double[N]);
            for(int j=0;j<N;j++) { fromInputToHiddenWeights.get(feature)[j] = 1*1.0/(j+2); }//-0.2; }
        }
        fromHiddenToOutputWeights = new double[N];
        for(int i=0;i<N;i++) { fromHiddenToOutputWeights[i] = 1*1.0/(i+2); }//0; }
    }
    private void createDeltas()
    {
        hiddenDelta = new double[N];
    }
    private void createGradients()
    {
        fromInputToHiddenGradients = new HashMap<String,double[]>();
        Set<String> allFeatures = manager.features.keySet();
        for(String feature : allFeatures) { fromInputToHiddenGradients.put(feature,new double[N]); }
        fromHiddenToOutputGradients = new double[N];
    }
    private void createDifferences()
    {
        fromInputToHiddenDifferences = new HashMap<String,double[]>();
        Set<String> allFeatures = manager.features.keySet();
        for(String feature : allFeatures)
        {
            fromInputToHiddenDifferences.put(feature, new double[N]);
            for(int i=0;i<N;i++)  fromInputToHiddenDifferences.get(feature)[i] = 0; 
        }
        fromHiddenToOutputDifferences = new double[N];
    }
    
    public double trainingSet(String message) //возвращает результат проверки нейросети (между 1 и 0) результат сохраняется в outputLayer
    {
        String[] features = manager.splitMessage(message); 
        for(int i=0;i<features.length;i++) { inputLayer.put(features[i], 1); } //для всех слов из сообщения задаем входное значение 1
        for(int i = 0;i<N;i++) //цикл по всем нейронам в скрытом слое
        {
            double summa = 0;
            //будет ошибка, если слова из сообщения нет в словаре
            for(int j=0;j<features.length;j++) //цикл по всем словам из входного слоя(берем только те,у кого значения не ноль(точнее,=1))
            {
                //костыль для случая когда нет слова
//                if(inputLayer.containsKey(features[j])) summa += 0.5 * fromInputToHiddenWeights.get(features[j])[i];
//                else
                summa += inputLayer.get(features[j]) * fromInputToHiddenWeights.get(features[j])[i];
            }
            hiddenLayer[i] = activationFunction(summa);
        }
        double summa = 0;
        for(int j = 0;j<N;j++) summa += hiddenLayer[j] * fromHiddenToOutputWeights[j]; 
        outputLayer = activationFunction(summa);
        return outputLayer;
    }
    public void backpropagation(String message, double perfectResult) //имеется в виду получившиеся значение на выходном слое и идеальное значение соответственно
    {
        String[] features = manager.splitMessage(message);
        outputDelta = (perfectResult-outputLayer) * activationFunctionDerivate(outputLayer); //считаем значение дельта для выходного нейрона
        //считаем значение дельта для скрытого слоя
        for(int i=0;i<N;i++) { hiddenDelta[i] = activationFunctionDerivate(hiddenLayer[i]) * (outputDelta * fromHiddenToOutputWeights[i]); } 
        //считаем градиент для весов из скрытого слоя в выходной
        for(int i=0;i<N;i++) { fromHiddenToOutputGradients[i] = hiddenLayer[i] * outputDelta; }
        //считаем величину, на которую нужно изменить вес для весов из скрытого в выходной, и изменяем вес
        for(int i=0;i<N;i++)
        {
            double difW = E * fromHiddenToOutputGradients[i] + A * fromHiddenToOutputDifferences[i];
            fromHiddenToOutputDifferences[i] = difW;
            fromHiddenToOutputWeights[i] += difW; //изменяем вес текущего синапса
        }
        //теперь меняем веса синапсов из входного слоя в скрытый (дельту для входного слоя считать не надо)
        //считаем градиент для всех весов из входного в скрытый (считаем только те слова, которые входят в слово => имеют вход = 1)
        for(int i=0;i<features.length;i++)
            for(int j=0;j<N;j++) { fromInputToHiddenGradients.get(features[i])[j] = inputLayer.get(features[i]) * hiddenDelta[j]; }      
        //также сразу считаем новый вес для каждого синапса
        for(int i=0;i<features.length;i++)
            for(int j=0;j<N;j++)
            {
                double difW = E * fromInputToHiddenGradients.get(features[i])[j] + A * fromInputToHiddenDifferences.get(features[i])[j];
                fromInputToHiddenDifferences.get(features[i])[j] = difW;
               // System.out.println(fromInputToHiddenDifferences.get(features[i])[j]);
                fromInputToHiddenWeights.get(features[i])[j] = fromInputToHiddenWeights.get(features[i])[j] + difW; //изменили вес синапса для i-ого входного нейрона и j-го скрытого
            } 
    }
    private double activationFunction(double x)
    {
        return 1/(1+Math.exp(-x));  //сигмоид
    }
    private double activationFunctionDerivate(double x)
    {
        return (1-x)*x;  //сигмоид 
    }
    public void showWeights()
    {
        Set<String> allFeatures = manager.features.keySet();
        for(String feature : allFeatures)
        {
            for(int i=0;i<N;i++)
            {
                System.out.println(fromInputToHiddenWeights.get(feature)[i]  + "  " +  feature);
            }   
        }
        System.out.println();
         for(int i=0;i<N;i++)
         {
            System.out.println(fromHiddenToOutputWeights[i] + "  "+i);
         }        
    }
}
