package dictionaryapp;

public class DictionaryApp {

    public static void main(String[] args) {
        
//        String message = "Hello, i am here to talk about some new things.";
//        String[] words = dm.splitMessage(message);
//        for(String s : words)
//        {
//            dm.addFeature(s, "bad");
//        }
//        System.out.println(dm.print());
//        DictionaryManager dm = new DictionaryManager();
//        DictionaryManagerGUI gui = new DictionaryManagerGUI(dm);



        DictionaryManagerGUI gui = new DictionaryManagerGUI();
        gui.setTitle("Dictionary manager");
        gui.setVisible(true);
        

        
//       // dm.print();
//     
//       System.out.println(dm.getCount("bad"));
    }
    
}

