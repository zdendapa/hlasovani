package com.parser.xml;

import com.parser.controller.ApplicationObjects;
import com.parser.gui.FileManager;
import com.parser.gui.MainFrame;
import com.parser.model.DataModel;
import com.parser.xml.input.Deputy;
import com.parser.xml.input.Session;
import com.parser.xml.input.VotingResult;
import com.parser.xml.output.Hlasovani;
import com.parser.xml.output.Poslanec;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DataParser {

    private String csvString = "";
    private String chapters = "";
    private Calendar calStartTotal = Calendar.getInstance();

    private static DataParser instance = new DataParser();

    public static DataParser getInstance() {
        return instance;
    }

    public ArrayList<DataModel> loadDataFromFolder(File folderURL, String matchingInputString, ArrayList<DataModel> resultData) throws ParserConfigurationException, IOException, SAXException {
        String destinationFolder = folderURL.toString();
        String folderName = "";
        Boolean zahajeniBylo = false;

        for (File file : FileManager.listFilesMatching(new File(destinationFolder), matchingInputString)) {
            String destinationFile = destinationFolder + File.separator + file.getName();
            if (MainFrame.DEBUG) {
                System.out.println("output file: " + destinationFile);
            }
            File archivedFile = new File(destinationFile);

            XStream xstream = new XStream(new StaxDriver(new NoNameCoder()));
            xstream.processAnnotations(VotingResult.class);
            xstream.processAnnotations(Session.class);
            VotingResult votingResult = (VotingResult) xstream.fromXML(archivedFile);
            DataModel dataModel = new DataModel(votingResult.getNumber(), votingResult.getTopic(),"00:00:00", "00:00:00", votingResult.getTime(),
                    votingResult.getSession(), 0, votingResult.getYes(), votingResult.getNo(),
                    votingResult.getAbstained(), votingResult.getNotVoting(), "", archivedFile.getName(), votingResult.getDeputy());

            addUniqueDataModel(resultData, dataModel);



            if((resultData.size()-1)>0)
            {
                DataModel predchozi = resultData.get(resultData.size()-2);

                if(predchozi.zacatekNasledujiciho=="00:00:00")
                {
                    predchozi.zacatekNasledujiciho = votingResult.getTime();
                    resultData.set(resultData.size()-2,predchozi);
                }

                if(zahajeniBylo && !votingResult.getTopic().equals("Zahájení"))
                {
                    //predchozi.posunTime = posuTime(predchozi.getTopicContent(),predchozi.getTime(), predchozi.zacatekNasledujiciho);
                }



                DataModel h = resultData.get(resultData.size()-1);
                h.posunTime = posuTime2(h.getTopicContent(),predchozi.getTime(), h.getTime(), predchozi.getPosunTime());


            }

            if(votingResult.getTopic().equals("Zahájení"))
            {
                zahajeniBylo = true;
            }





            folderName = votingResult.session.getNumber();

        }

        ApplicationObjects.getInstance().setOutputFolderLocation(ApplicationObjects.getInstance().getOutputFolderLocation()+"\\"+folderName);

        return resultData;
    }


    private String posuTime2(String nazev, String startTime, String endTime, String predchoziPosun)
    {
        if(nazev.equals("Zahájení"))
        {
            return "00:00:00";
        }

        String videoPosun;
        Calendar calEnd = makeCal(endTime);
        Calendar calStart = makeCal(startTime);
        Calendar calPredchoziPosun = makeCal2(predchoziPosun);

        long delkaUsneseni = calEnd.getTimeInMillis() - calStart.getTimeInMillis();
        long diff = delkaUsneseni + calPredchoziPosun.getTimeInMillis();



        long second = (diff / 1000) % 60;
        long minute = (diff / (1000 * 60)) % 60;
        long hour = (diff / (1000 * 60 * 60)) % 24;

        videoPosun = lontTimeToString(hour) + ":" + lontTimeToString(minute) + ":" + lontTimeToString(second);
        return videoPosun;
    }

    private String posuTime(String nazev, String zacatek, String zacatekNasledujiciho)
    {
        if(nazev.equals("Zahájení"))
        {
            calStartTotal = makeCal(zacatek);
            return "00:00:00";
        }

        String videoPosun;
        Calendar calEnd = makeCal(zacatekNasledujiciho);
        Calendar calStart = makeCal(zacatek);

        long diffStart = calStart.getTimeInMillis() - calStartTotal.getTimeInMillis();
        long diffEnd = calEnd.getTimeInMillis() - calStartTotal.getTimeInMillis();

        // delka
        //long diffStart = calEnd.getTimeInMillis() - calStart.getTimeInMillis();


        long second = (diffStart / 1000) % 60;
        long minute = (diffStart / (1000 * 60)) % 60;
        long hour = (diffStart / (1000 * 60 * 60)) % 24;

        videoPosun = lontTimeToString(hour) + ":" + lontTimeToString(minute) + ":" + lontTimeToString(second);
        return videoPosun;
    }


    public String lontTimeToString(long l)
    {
        String s = Long.toString(l);
        if(s.length()==1) s = "0" + s;
        if(s.length()==0) s = "00";
        return s;
    }

    public void exportDataToXml() throws IOException {
        HierarchicalStreamDriver driver = new XppDriver(new NoNameCoder());
        XStream xstream = new XStream(driver);

        xstream.processAnnotations(Hlasovani.class);
        xstream.processAnnotations(Poslanec.class);
        xstream.alias("hlasovani", Hlasovani.class);
        xstream.alias("poslanec", Poslanec.class);

        ArrayList<DataModel> inputData = ApplicationObjects.getInstance().getInputDataFromXML();

        Calendar calStartTotal = Calendar.getInstance();
        Calendar calNasledujici = Calendar.getInstance();
        Calendar calPredchoziCas = Calendar.getInstance();
        boolean zahajeni = false;
        int poradiSchvaleneChapters = 1;
        csvString = "";
        long predchoziCas =0;

        HashMap hm = new HashMap();
        ArrayList<HashMap> hlasy = new ArrayList<HashMap>();

        for (int i = 0; i < inputData.size(); i++) {

            Date curDate = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat(DataModel.dateFormat);
            try {
                curDate = dateFormatter.parse(inputData.get(i).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(curDate);
            int year = calendar.get(Calendar.YEAR);

            String fileUrl = ApplicationObjects.getInstance().getOutputFolderLocation() + File.separator +
                    "ZMC_" + year + "_" + inputData.get(i).getOrderNumber() + ".xml";


            File theDir = new File(ApplicationObjects.getInstance().getOutputFolderLocation());

            // if the directory does not exist, create it
            if (!theDir.exists()) {
                System.out.println("creating directory: " );
                boolean result = false;

                try{
                    theDir.mkdir();
                    result = true;
                } catch(SecurityException se){
                    //handle it
                }
                if(result) {
                    System.out.println("DIR created");
                }
            }

            Hlasovani h = makeHlasovani(inputData.get(i));
            int dohromady = ((Hlasovani) h).yes + ((Hlasovani) h).no + ((Hlasovani) h).abstained + ((Hlasovani) h).notVoting;


            if(((Hlasovani) h).yes >= dohromady/2)
            {
                hm = new HashMap();
                hm.put("time",inputData.get(i).getTime());
                hm.put("posun",inputData.get(i).getPosunTime());
                hm.put("topic",inputData.get(i).getTopicContent());
                hm.put("number",inputData.get(i).getNumber());
                hlasy.add(hm);
            }


            // csv soubor --------------------------------------------------- konec

            File f = new File(fileUrl);
            f.createNewFile();
            PrintWriter writer = new PrintWriter(fileUrl, "UTF-8");
            writer.write(xstream.toXML(h));
            writer.close();

        }



        for (int i = 0; i < hlasy.size(); i++) {
            // csv soubor --------------------------------------------------- start
            hm = hlasy.get(i);

            if(hm.get("topic").equals("Zahájení"))
            {
                calStartTotal = makeCal2((String) hm.get("time"));
                //csvString += "00:00:00;Zahájení\n";
                csvString += hm.get("posun") + ";Zahájení\n";
                poradiSchvaleneChapters = 1;
                predchoziCas = 0;


                HashMap hmPredchozi = hlasy.get(i+1);
                String casDalsiDalsi = casDalsi((String) hmPredchozi.get("posun"));
                chapters = "1\n";
                //chapters += "00:00:00,000 --> " + casDalsiDalsi + ",000" + "\n";
                chapters += hm.get("posun")+ ",000 --> " + casDalsiDalsi + ",000" + "\n";
                chapters += hm.get("topic") + "\n\n";


                zahajeni = true;
            } else {
                if (zahajeni) {

                        /*
                        csv se pocita:
                         čas předchozího s přiděleným číslem usnesení+posun+1sec
                         inputData.get(i).getTime(i-1) + inputData.get(i).getPosunTime() + 1
                         */



                    String casDalsi = casDalsi((String) hm.get("posun"));
                    //csvString += poradiSchvalene;
                    //csvString += longTimeToString(hour) + ":" + longTimeToString(minute) + ":" + longTimeToString(second) + ";" + hm.get("number") + ". " + hm.get("topic") + "\n";
                    csvString += casDalsi + ";" + hm.get("number") + ". " + hm.get("topic") + "\n";

                    String casDalsiDalsi = "00:00:00";
                    if(i<hlasy.size()-1 && i>0)
                    {
                        HashMap hmPredchozi = hlasy.get(i+1);
                        casDalsiDalsi = casDalsi((String) hmPredchozi.get("posun"));
                    }


                    poradiSchvaleneChapters++;
                    chapters += poradiSchvaleneChapters + "\n";
                    //chapters += casDalsi + "\n";
                    chapters += casDalsi + ",000 --> " + casDalsiDalsi + ",000\n";
                    chapters += hm.get("number") + ". " + hm.get("topic") + "\n\n";
                    //predchoziCas = cas;
                }
            }
        }



        //generateCsvFile("c:\\z\\test.csv", csvString);
        //generateChaptersFile("c:\\z\\test.txt", chapters);
    }

    private String casDalsi(String s)
    {
        Calendar cal = makeCal2(s);
        long cas = cal.getTimeInMillis() + 1000;

        long second = (cas / 1000) % 60;
        long minute = (cas / (1000 * 60)) % 60;
        long hour = (cas / (1000 * 60 * 60)) % 24;

        //csvString += poradiSchvalene;
        return longTimeToString(hour) + ":" + longTimeToString(minute) + ":" + longTimeToString(second);
    }

    public Calendar makeCal(String s)
    {
        Calendar cal = Calendar.getInstance();
        String timeArr[] = s.split(" ");
        timeArr = timeArr[timeArr.length-1].split(":");

        if(s.equals("00:00:00"))
        {
            cal.setTimeInMillis(0);
        }
        else
        {
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt((timeArr[0])));
            cal.set(Calendar.MINUTE, Integer.parseInt((timeArr[1])));
            cal.set(Calendar.SECOND, Integer.parseInt((timeArr[2])));
        }



        return cal;
    }

    public Calendar makeCal2(String s)
    {
        Calendar cal = Calendar.getInstance();
        String timeArr[] = s.split(" ");
        timeArr = timeArr[timeArr.length-1].split(":");

        long secMill = Integer.parseInt(timeArr[2]) * 1000;
        long minMillise = Integer.parseInt(timeArr[1]) * 1000*60;
        long hourMillisec = Integer.parseInt(timeArr[0]) * 1000*60*60;

        cal.setTimeInMillis(secMill+minMillise+hourMillisec);

        return cal;
    }


    public String longTimeToString(long l)
    {
        String s = Long.toString(l);
        if(s.length()==1) s = "0" + s;
        if(s.length()==0) s = "00";
        return s;
    }

    public void exportDataToChapters() throws IOException {

        chapters = chapters.replaceAll("\n", System.lineSeparator());

        File f = new File(ApplicationObjects.getInstance().getOutputFolderLocation()+"\\chapters.txt");
        f.createNewFile();
        PrintWriter writer = new PrintWriter(ApplicationObjects.getInstance().getOutputFolderLocation()+"\\chapters.txt", "UTF-8");
        writer.write(chapters);
        writer.close();
    }

    private  static  void generateChaptersFile(String sFileName, String chapters)
    {
        ArrayList<DataModel> inputData = ApplicationObjects.getInstance().getInputDataFromXML();

        try
        {

            chapters = chapters.replaceAll("\n", System.lineSeparator());

            File f = new File(sFileName);
            f.createNewFile();
            PrintWriter writer = new PrintWriter(sFileName, "UTF-8");
            writer.write(chapters);
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void exportDataToCVS() throws IOException {
        csvString = csvString.replaceAll("\n", System.lineSeparator());

        File f = new File(ApplicationObjects.getInstance().getOutputFolderLocation()+"\\csv.csv");
        f.createNewFile();
        PrintWriter writer = new PrintWriter(ApplicationObjects.getInstance().getOutputFolderLocation()+"\\csv.csv", "UTF-8");
        writer.write(csvString);
        writer.close();
    }

    private static void generateCsvFile(String sFileName, String csvString)
    {
        ArrayList<DataModel> inputData = ApplicationObjects.getInstance().getInputDataFromXML();

        try
        {

            csvString = csvString.replaceAll("\n", System.lineSeparator());

            File f = new File(sFileName);
            f.createNewFile();
            PrintWriter writer = new PrintWriter(sFileName, "UTF-8");
            writer.write(csvString);
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }





    private Hlasovani makeHlasovani(DataModel votingResult) {
        ArrayList<Poslanec> poslanecs = new ArrayList<Poslanec>();
        for (Deputy deputy : votingResult.getDeputies()) {
            poslanecs.add(new Poslanec(deputy.getName(), deputy.getParty(), deputy.getVote()));
        }
        Hlasovani hlasovani = new Hlasovani(poslanecs, votingResult.getOrderNumber(), votingResult.getYes(), votingResult.getNo(), votingResult.getAbstained(), votingResult.getNotVoting());
        //todo add orderNumber
        return hlasovani;
    }

    private ArrayList<DataModel> addUniqueDataModel(ArrayList<DataModel> resultData, DataModel dataModel) {
        int i = 0;
        boolean isExistNotUnique = false;
        for (i = 0; i < resultData.size(); i++) {
            if (resultData.get(i).getTopicContent().equals(dataModel.getTopicContent()) &&
                    resultData.get(i).getRealDate() < dataModel.getRealDate()) {
                isExistNotUnique = true;
                break;
            }
        }
        if (isExistNotUnique) {
            resultData.remove(i);
        }
        resultData.add(dataModel);
        return resultData;
    }
}
