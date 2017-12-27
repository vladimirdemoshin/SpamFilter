package dictionaryapp;
import java.io.FileReader;
import java.util.regex.*;
import java.util.*;
public class DictionaryManager {
    public HashMap<String,Integer> categories = new HashMap<String,Integer>(); //хранит категорию и количество писем данной категории
    public HashMap<String,HashMap<String,Integer>> features = 
            new HashMap<String,HashMap<String,Integer>> (); //пополняемый список слов, хранит для каждого слова категорию и количество вхождений данного слова в письма с данной категорией
    public void addMessage(String message,String category) //добавляет слова из сообщения известной категории в словарь, увеличивает счетчик сообщений по категории
    {
        ArrayList<String> uniqueWords = new ArrayList<String>(); //список содержащий только неповторяющиеся экземпляры слов из сообщения
        String[] words = splitMessage(message);
        for(String word : words)
        {
            if(!uniqueWords.contains(word))
            {
                addFeature(word,category);
                uniqueWords.add(word);
            }
        }
        addCategory(category);
    }
    public void addFeature(String feature, String category) // добавить слово в словарь по категории; если слово уже было, увеличить число его вхождений в данную категорию
    {
        feature = feature.toLowerCase(); //добавил преобразование всех слов к нижнему регистру
        HashMap<String,Integer> categoriesOfFeature = features.get(feature);
        if(categoriesOfFeature == null) categoriesOfFeature = new HashMap<String,Integer>();
        if(categoriesOfFeature.get(category) == null) 
            categoriesOfFeature.put(category, 1);   
        else 
            categoriesOfFeature.put(category, categoriesOfFeature.get(category) + 1);
        features.put(feature, categoriesOfFeature);
    }
      public int getFeatureValue(String feature, String category) //получает количество вхождений данного слова в данную категорию
    {
        HashMap<String,Integer> categoriesOfFeature = features.get(feature);
        if(categoriesOfFeature == null) return 0;
        else 
        {
           if(categoriesOfFeature.get(category)!= null)
                return categoriesOfFeature.get(category);
           else return 0;
        }
    }
    public void addCategory(String category) //увеличить счётчик применения категории (для писем)
    {
        if(categories.get(category) == null) categories.put(category, 1);
        else categories.put(category, categories.get(category) + 1);
    }
      public int getCategoryCount(String category) //получить количество писем по заданной категории
    {
        if(categories.get(category) == null) return 0;
        else return categories.get(category);
    }
      public String[] getAllCategories() // получает список всех категорий (не тестил)
      {
          ArrayList<String> output = new ArrayList<String>();
          Set<String> allCategories = categories.keySet();
          for(String category : allCategories)
          {
              output.add(category);
          }
          return (String[])output.toArray();
      }
       public int getAllFeaturesCount(String category) //получить количество всех слов в заданной категории
    {
        Collection<HashMap<String,Integer>> allCategories = features.values(); //создаём коллекцию для хранения категорий и количества вхождений в категорию для каждого признака
        Iterator<HashMap<String,Integer>> iterator =  allCategories.iterator(); //создаём итератор для просматривания всей коллекции
        int countAllFeaturesInCategory = 0; //считаем количество всех признаков для данной категории
        while(iterator.hasNext())
        {
            Integer count = iterator.next().get(category);
            if(count != null)
            {
                countAllFeaturesInCategory += count;
            }
        }
        return countAllFeaturesInCategory;
    }
    public String[] splitMessage(String message) //разбитие сообщения на слова
    {
        // return Pattern.compile("[^\\w]+").split(message); 
        ArrayList<String> tempWordsList = new ArrayList<String>();
        Pattern p = Pattern.compile("[\\w]+");  
        Matcher m = p.matcher(message);  
        while(m.find())
        {
           String word = message.substring(m.start(),m.end());
           tempWordsList.add(word);
        }
        String[] output = new String[tempWordsList.size()];
        for(int i=0;i<output.length;i++)
        {
            output[i] = tempWordsList.get(i);
            output[i] = output[i].toLowerCase(); //добавил преобразование всех слов к нижнему регистру
        }
        return output;
    }
    public String print() // печатает все слова и для каждого слова количество его вхождений в каждую категорию
    {
       Set<String> allFeatures = features.keySet();
       String output = "";
       for(String feature : allFeatures)
       {
           output += "'" + feature + "'\n";
           HashMap<String,Integer> allCategoriesForFeature = features.get(feature);
           Set<String> allCategoriesKeys = allCategoriesForFeature.keySet();
           Iterator<String> iterator = allCategoriesKeys.iterator();
           while(iterator.hasNext())
           {
               String category = iterator.next();
               output += category + " - " + allCategoriesForFeature.get(category) + "\n";
           }
           output += "\n";
       }
       return output;
    }
    public String getFileContent(String path)
    {
        String output = "";
        try
        {
            FileReader reader = new FileReader(path);
            int tempChar;
            while((tempChar=reader.read())!=-1){ output += (char)tempChar; }
            reader.close();
        }
        catch(Exception e){}
        return output;
    }
}
