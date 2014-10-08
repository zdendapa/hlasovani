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
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DataParser {
    private static DataParser instance = new DataParser();

    public static DataParser getInstance() {
        return instance;
    }

    public ArrayList<DataModel> loadDataFromFolder(File folderURL, String matchingInputString, ArrayList<DataModel> resultData) throws ParserConfigurationException, IOException, SAXException {
        String destinationFolder = folderURL.toString();
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
            DataModel dataModel = new DataModel(votingResult.getNumber(), votingResult.getTopic(), votingResult.getTime(),
                    votingResult.getSession(), 0, votingResult.getYes(), votingResult.getNo(),
                    votingResult.getAbstained(), votingResult.getNotVoting(), "", archivedFile.getName(), votingResult.getDeputy());
            addUniqueDataModel(resultData, dataModel);
        }

        return resultData;
    }


    public void exportDataToXml() throws IOException {
        HierarchicalStreamDriver driver = new XppDriver(new NoNameCoder());
        XStream xstream = new XStream(driver);

        xstream.processAnnotations(Hlasovani.class);
        xstream.processAnnotations(Poslanec.class);
        xstream.alias("hlasovani", Hlasovani.class);
        xstream.alias("poslanec", Poslanec.class);

        ArrayList<DataModel> inputData = ApplicationObjects.getInstance().getInputDataFromXML();
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
            File f = new File(fileUrl);
            f.createNewFile();
            PrintWriter writer = new PrintWriter(fileUrl, "UTF-8");
            Hlasovani h = makeHlasovani(inputData.get(i));
            writer.write(xstream.toXML(h));
            writer.close();
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
