import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Bot{

    boolean lastQuestionWereName = false;
    private final String[][] chatData = {
            {"hej", "hi", "hello", "tjena", "hejsan", "halloj", "tja", "goddag", "hallå"},
            {"HEJ.", "Hello!", "Hej på dig!", "Hejhej", "Hej, kul att se dig här!", "Halloj, fantastiskt att du är här!"},
            //Frågor
            {"hur är läget", "hur mår du", "läget"},
            {"Det är fint, tack för att du frågar.", "Bara bra.", "Mycket fint."},
            {"vad gör du", "whatsup", "vad händer", "zupp"},
            {"Kollar in parken.", "Räknar hur många dagar i sträck det varit sol", "Bygger lego", "Kastar pil!", "Tränar på karate", "Fyller dagarna med korsord"},
            {"Jag förstår inte riktigt frågan.", "Vad heter du?", "Vem är du?", "Vad är du?", "Fint väder idag.", "Kul att ha en besökare en dag som denna."}
    };

    public String getReply(String message){
        String answer ="";
        byte respons = 0;
        try {
            message = URLDecoder.decode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }


        int i = 0;
        message = trimMessage(message);
        if (lastQuestionWereName){
            lastQuestionWereName = false;
            return getNameAnswer(message);
        }

        while (respons == 0){
            if (phraseExsis(message.toLowerCase(), chatData[i*2])){
                respons = 2;
                Random r = new Random();
                int random = r.nextInt(chatData[i*2+1].length);
                answer = chatData[i*2+1][random];

            }
            i++;
            if (i*2 == chatData.length-1 && respons == 0){
                respons = 1;
            }

        }

        if (respons == 1){
            Random random = new Random();
            int r = random.nextInt(chatData[chatData.length-1].length);
            answer = chatData[chatData.length-1][r];
            if (answer == "Vad heter du?"){
                lastQuestionWereName = true;
            }

        }



        try {
            answer = URLEncoder.encode(answer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return answer;
    }

    private boolean phraseExsis(String phrase, String[] data){
        boolean match = false;
        for (String message : data) {
            if (message.equals(phrase)){
                match = true;
            }
        }
        return match;
    }
    public String trimMessage(String msg){

        while (
                msg.charAt(msg.length()-1) == '?' ||
                        msg.charAt(msg.length()-1) == ' ' ||
                        msg.charAt(msg.length()-1) == '!'){
            msg = msg.substring(0, msg.length()-1);
        }
        msg = msg.trim();
        return msg;

    }

    public String getNameInfo(String name){
        String inputName = name;
        if (name.contains(" ")){
            name.replace(" ", "-");
        }
        try{
            org.jsoup.nodes.Document doc = Jsoup.connect("http://historiska.se/nomina/?nomina_name="+name).get();
            org.jsoup.select.Elements paragraphs = doc.select("p");
            boolean returnNext = false;
            for(org.jsoup.nodes.Element p : paragraphs) {
                if (returnNext == true){
                    return "Visste du att "+inputName+" härleder från? "+ p.text();
                }

                if (p.text() == "Härledning"){
                    returnNext = true;
                }

            }
    } catch (IOException e) {
        // TODO Auto-generated catch block
    }
        return "Vilket fint namn!";
    }

    public void getNameInfo2(String name){
        String inputName = name;
        if (name.contains(" ")){
            name.replace(" ", "-");
        }
        try{
            org.jsoup.nodes.Document doc = Jsoup.connect("http://historiska.se/nomina/?nomina_name="+name).get();
            org.jsoup.select.Elements paragraphs = doc.select("p");
            boolean returnNext = false;
            for(org.jsoup.nodes.Element p : paragraphs) {
                System.out.println("__________");
                //rSystem.out.println(p.text());
                System.out.println(p.className());
                System.out.println("__________");


            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }

    }

    public String getNameAnswer(String name){
        String inputName = name;
        if (name.contains(" ")){
            name.replace(" ", "-");
        }
        try{
            org.jsoup.nodes.Document doc = Jsoup.connect("http://www.svenskaakademien.se/svenska-akademien/almanackan/namnens-ursprung-och-betydelse").get();

            Elements elements = doc.select("p");
            for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext();)
            {

                Element element = iterator.next();
                String str = element.text();
                int i = str.indexOf(' ');
                if (i>=0){
                    String firstName = str.substring(0,i-1);
                    if (firstName.equals(name)){
                        String message = str.substring(i);
                        char[] chars = message.toCharArray();
                        chars[0] = Character.toLowerCase(chars[0]);
                        return "Vilket fint namn! Det är ett "+new String(chars);
                    }
                  //  System.out.println(firstName);
                }
              /*  str = str.trim();*/
                //String firstName = str.substring(0,str.indexOf(' ')); // "72"
                //System.out.println(str.indexOf(' '));
                //  System.out.println(element.text());

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return "Vilket fint namn!";

    }
}
