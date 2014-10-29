package com.parser.gui;

import com.parser.controller.ApplicationObjects;
import com.parser.model.DataModel;
import com.parser.xml.DataParser;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static javax.swing.GroupLayout.Alignment.LEADING;

public class MainFrame extends JFrame {
    public static boolean DEBUG = false;
    static MainFrame mainFrame;
    Container rootContainer;
    JButton loadFileButton;
    JButton pridelitButton;
    JButton exportFileButton;
    JButton adminButton;
    JLabel lbUserName;
    JLabel lbPrihlasen;

    JTable table;
    JFileChooser chooser;

    private static final String stringForButtonBegin = "Načíst";
    private static final String stringForButtonPridelit = "Přiděl";
    private static final String stringForExport = "Export";
    private static final String stringForAdmin = "Admin";
    private static final String stringMainWindowName = "Hlasování";
    private static final String stringSelectImportFolder = "Vyberte zdrojový adresář";
    private static final String stringMessageChooseAnotherInputFolder = "Nebyl vybrán zdrojový adresář. Vyberte prosím znovu.";
    private static final String stringEnterPass = "Vložte heslo:";
    private static final String stringTruePassword = "Hilmar";
    private static final String stringCantStartImport = "Nelze exportovat, tabulka neobsahuje žádná data.\nNejříve načtěte data.";
    private static final String stringBadCUsneseni = "Nelze exportovat.\nPřiřaďte čísla usnesení.";
    private static final String stringDataSuccessfullyExported = "Vyexportováno.";
    private static final String stringDataNotSuccessfullyExported = "Chyba během exportu.\nZkontrolujte prosím výstupní adresář..";
    private static final String stringNoValidFileForParsing = "Nebyly nalezeny vstupní soubory.\nZkontrolujte znovu, nebo vyberte jiný adreář";
    private static final String  stringNoMatchingFiles="Nebyly nalezeny vstupní soubory.\nŽádné neodpovídají filtru: [0-9]{4}.xml";
    public MainFrame() {
        initUserInterfaceElements();
    }

    private void initUserInterfaceElements() {

        rootContainer = getContentPane();
        GroupLayout groupLayout = new GroupLayout(rootContainer);
        rootContainer.setLayout(groupLayout);

        loadFileButton = new JButton(stringForButtonBegin);
        pridelitButton = new JButton(stringForButtonPridelit);
        exportFileButton = new JButton(stringForExport);
        adminButton = new JButton(stringForAdmin);
        lbUserName = new JLabel("Admin",SwingConstants.CENTER);
        lbUserName.setVisible(false);
        lbPrihlasen = new JLabel("přihlášen",SwingConstants.CENTER);
        lbPrihlasen.setVisible(false);

        addActionForLoadFileButton();
        addActionForNacistButton();
        addActionForPridelitButton();
        addActionForAdminButton();
        addActionForExportButton();

        table = new JTable(new TableModel());
        JScrollPane scrollPane = new JScrollPane(table);
        loadTable();
        rootContainer.add(scrollPane);


/*
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        buttonPanel.add(loadFileButton);
        buttonPanel.add(pridelitButton);

        JPanel east = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 1;
        east.add(buttonPanel, gbc);
        //rootContainer.add(east, BorderLayout.EAST);
*/

        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(LEADING)
                                        .addComponent(scrollPane)
                        )
                        .addGroup(groupLayout.createParallelGroup(LEADING,false)
                                .addComponent(loadFileButton,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(exportFileButton,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pridelitButton,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(adminButton,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbUserName,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lbPrihlasen,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        )
        );

        groupLayout.setVerticalGroup(groupLayout.createParallelGroup()

                        .addComponent(scrollPane)

                        .addGroup(groupLayout.createSequentialGroup()
                                        .addComponent(loadFileButton)
                                        .addComponent(exportFileButton)
                                        .addComponent(pridelitButton)
                                        .addComponent(adminButton)
                                        .addComponent(lbUserName)
                                        .addComponent(lbPrihlasen)
                        )
        );


        pack();

        setTitle(stringMainWindowName);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }




    private void addActionForNacistButton() {


    }

    private void addActionForLoadFileButton() {
        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                emptyApplicationData();


                runChooser(stringSelectImportFolder, FolderType.INPUT_FOLDER);
                String inputFolder = ApplicationObjects.getInstance().getInputFolderLocation();

                if (inputFolder == null || inputFolder.equals("")) {
                    JOptionPane.showMessageDialog(null, stringMessageChooseAnotherInputFolder);
                    emptyApplicationData();
                } else {

                    int countOfCorrectFiles = 0;
                    File []listCorrectInputFiles = FileManager.listFilesMatching(new File(inputFolder),FileManager.matchingInputString);
                    if(listCorrectInputFiles!=null){
                        countOfCorrectFiles = listCorrectInputFiles.length;
                    }
                    if(countOfCorrectFiles==0){
                        JOptionPane.showMessageDialog(null, stringNoMatchingFiles);
                    }else{

                        processCopyingFilesFromInputToBackup();
                    }
                }
            }
        });
    }

    private void emptyApplicationData() {
        TableModel tableModel = (TableModel) table.getModel();
        tableModel.setData(new Object[0][0]);
        tableModel.fireTableDataChanged();
        ApplicationObjects.getInstance().setInputDataFromXML(new ArrayList<DataModel>());
        ApplicationObjects.getInstance().setInputFolderLocation("");

        if(MainFrame.DEBUG){
            System.out.println("inputFolderLocation after clean:"+ApplicationObjects.getInstance().getInputFolderLocation());
        }
    }


    private void addActionForPridelitButton() {
        pridelitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectedRow() != -1) {
                    //Object[][] dataFromTable = ((TableModel) table.getModel()).getData();

                    int startNumber = Integer.valueOf((Integer) table.getValueAt(table.getSelectedRow(),4));
                    for (int i=table.getSelectedRow()+1;i<table.getRowCount();i++)
                    {
                        if( (Boolean) table.getValueAt(i,5)== true)
                        {
                            startNumber ++;
                            table.setValueAt(startNumber,i,4);
                        }

                    }


                    System.out.println("Saa");
                    // remove selected row from the model
                    //model.removeRow(table.getSelectedRow());
                }
            }
        });
    }

    private void addActionForAdminButton() {
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JPanel panel = new JPanel();
                JLabel label = new JLabel(stringEnterPass);
                JPasswordField pass = new JPasswordField(10);
                panel.add(label);
                panel.add(pass);
                String[] options = new String[]{"OK", "Cancel"};
                int option = JOptionPane.showOptionDialog(null, panel, "Admin",
                        JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);
                if (option == 0) // pressing OK button
                {
                    String password = new String(pass.getPassword());

                    if (stringTruePassword.equals(password)) {
                        ApplicationObjects.getInstance().setAdminLogged(true);
                        lbPrihlasen.setVisible(true);
                        lbUserName.setVisible(true);
                    } else {
                        ApplicationObjects.getInstance().setAdminLogged(false);
                    }
                    if (DEBUG) {
                        System.out.println("Your password is: " + password);
                    }
                }
            }
        });
    }

    private void addActionForExportButton() {
        exportFileButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //check for the first if export available
                Object[][] dataFromTable = ((TableModel) table.getModel()).getData();
                if (dataFromTable.length > 0) {
                    boolean isNotInitializedCUsneseni = false;
                    for (int i = 0; i < dataFromTable.length; i++) {
                        if ((Integer) dataFromTable[i][4] == 0 && (Boolean) dataFromTable[i][5]== true) {
                            isNotInitializedCUsneseni = true;
                        }
                    }
                    if (isNotInitializedCUsneseni) {
                        JOptionPane.showMessageDialog(null, stringBadCUsneseni);
                    } else {
                        // runChooser(stringSelectOutputFolder, FolderType.DESTINATION_OUTPUT_FOLDER);
                        processExportDataWithDialog();


                    }
                } else {
                    JOptionPane.showMessageDialog(null, stringCantStartImport);
                }


            }
        });
    }


    private void loadTable() {
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(280);
        table.getColumnModel().getColumn(4).setPreferredWidth(75);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(30);
        table.getColumnModel().getColumn(7).setPreferredWidth(30);
        table.getColumnModel().getColumn(8).setPreferredWidth(30);
        table.getColumnModel().getColumn(9).setPreferredWidth(30);
        table.getColumnModel().getColumn(10).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setCellEditor(
                new IntegerEditor(1, Integer.MAX_VALUE));

        MaskFormatter mask = null;
        try {
            mask = new MaskFormatter("##.##.#### ##:##:##");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JFormattedTextField distanceText = new JFormattedTextField(mask);
        table.getColumnModel().getColumn(1).setCellEditor(
                new DefaultCellEditor(distanceText));
    }

    private void runChooser(String chooserTitle, FolderType folderType) {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(chooserTitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(rootContainer) == JFileChooser.APPROVE_OPTION) {
            switch (folderType) {
                case INPUT_FOLDER:
                    ApplicationObjects.getInstance().setInputFolderLocation(chooser.getSelectedFile().toString());

                    if (MainFrame.DEBUG) {
                        System.out.println(folderType.toString() + ApplicationObjects.getInstance().getInputFolderLocation());
                    }
                    break;
                case BACKUP_COPY_FOLDER:
                    break;
                case DESTINATION_OUTPUT_FOLDER:
                    break;
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            }
        });
    }

    private void processExportData() {
        ArrayList<DataModel> inputData = ApplicationObjects.getInstance().getInputDataFromXML();
        Object[][] dataFromTable = ((TableModel) table.getModel()).getData();
        for (int i = 0; i < inputData.size(); i++) {
            inputData.get(i).setTime((String) dataFromTable[i][1]);
            inputData.get(i).setPosunTime((String) dataFromTable[i][2]);
            inputData.get(i).setOrderNumber((Integer) dataFromTable[i][4]);

        }

        Boolean vyexportovano = true;

        try {
            DataParser.getInstance().exportDataToXml();
            //JOptionPane.showMessageDialog(null, stringDataSuccessfullyExported);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, stringDataNotSuccessfullyExported);
            vyexportovano = false;
            e.printStackTrace();
        }

        try {
            DataParser.getInstance().exportDataToCVS();
            //JOptionPane.showMessageDialog(null, stringDataSuccessfullyExported);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Soubor csv.csv se nepodařilo vyexportovat");
            vyexportovano = false;
            e.printStackTrace();
        }

        try {
            DataParser.getInstance().exportDataToChapters();
            //JOptionPane.showMessageDialog(null, stringDataSuccessfullyExported);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Soubor chapters.txt se nepodařilo vyexportovat");
            vyexportovano = false;
            e.printStackTrace();
        }

        if(vyexportovano) JOptionPane.showMessageDialog(null, stringDataSuccessfullyExported);

    }

    private void processCopyingFilesFromInputToBackup() {
        FileManager fileManager = new FileManager();
        try {
            fileManager.copyAllFilesAndFolders();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //load data from output directory, which was copied from input directory
        ArrayList<DataModel> dataModelFromXML = null;
        try {
            //dataModelFromXML = DataParser.getInstance().
            dataModelFromXML = loadDataFromFolderWithWaitingDialog(new File(ApplicationObjects.getInstance().getBackupFolderLocation()),
                    FileManager.matchingInputString);
            ApplicationObjects.getInstance().setInputDataFromXML(dataModelFromXML);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        TableModel tableModel = (TableModel) table.getModel();

        if (dataModelFromXML != null && dataModelFromXML.size() > 0) {


            String videoPosun="";
            Calendar calStartTotal = Calendar.getInstance();


            for (int i = 0; i < dataModelFromXML.size(); i++) {

                /* vzpocet posunTime delam v DataParser
                if(i==0)
                {
                    videoPosun = "00:00:00";
                    calStartTotal = makeCal(dataModelFromXML.get(i).getTime());


                } else
                {
                    Calendar calEnd = makeCal(dataModelFromXML.get(i).zacatekNasledujiciho);
                    Calendar calStart = makeCal(dataModelFromXML.get(i).getTime());

                    long diffStart = calStart.getTimeInMillis() - calStartTotal.getTimeInMillis();
                    long diffEnd = calEnd.getTimeInMillis() - calStartTotal.getTimeInMillis();

                    // delka
                    //long diffStart = calEnd.getTimeInMillis() - calStart.getTimeInMillis();


                    long second = (diffStart / 1000) % 60;
                    long minute = (diffStart / (1000 * 60)) % 60;
                    long hour = (diffStart / (1000 * 60 * 60)) % 24;

                    videoPosun = lontTimeToString(hour) + ":" + lontTimeToString(minute) + ":" + lontTimeToString(second);




                    //videoPosun = diff / (60 * 60 * 1000) + ":" + diff / (60 * 1000) + ":" + diff / 1000;
                    //videoPosun = c.HOUR + ":" + c.MINUTE + ":" + c.MILLISECOND;
                }
                */

                tableModel.addRow(new Object[]{
                        dataModelFromXML.get(i).getNumber(),
                        dataModelFromXML.get(i).getTime(),

                        dataModelFromXML.get(i).getPosunTime(),//dataModelFromXML.get(i).getTime(),//"0:00:00",
                        //dataModelFromXML.get(i).getSessionContent(),
                        dataModelFromXML.get(i).getTopicContent(),
                        new Integer(0),
                        true,
                        dataModelFromXML.get(i).getYes(),
                        dataModelFromXML.get(i).getNo(),
                        dataModelFromXML.get(i).getAbstained(),
                        dataModelFromXML.get(i).getNotVoting(),
                        ""});
                tableModel.fireTableDataChanged();
            }
        } else {
            JOptionPane.showMessageDialog(null, stringNoValidFileForParsing);
        }
    }

    public String lontTimeToString(long l)
    {
        String s = Long.toString(l);
        if(s.length()==1) s = "0" + s;
        if(s.length()==0) s = "00";
        return s;
    }

    public Calendar makeCal(String s)
    {
        Calendar cal = Calendar.getInstance();
        String timeArr[] = s.split(" ");
        timeArr = timeArr[timeArr.length-1].split(":");

        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt((timeArr[0])));
        cal.set(Calendar.MINUTE, Integer.parseInt((timeArr[1])));
        cal.set(Calendar.SECOND, Integer.parseInt((timeArr[2])));

        return cal;
    }

    public ArrayList<DataModel> loadDataFromFolderWithWaitingDialog(final File folderURL, final String matchingInputString) throws ParserConfigurationException, IOException, SAXException {
        final ArrayList<DataModel> resultData = new ArrayList<DataModel>();

        final JDialog loading = initWaitingDialog();

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws InterruptedException {
                try {
                    DataParser.getInstance().loadDataFromFolder(folderURL, matchingInputString, resultData);
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                return "ok";
            }

            @Override
            protected void done() {
                loading.dispose();
            }
        };
        worker.execute();
        loading.setVisible(true);
        try {
            worker.get();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (MainFrame.DEBUG) {
            System.out.println();
            System.out.println("resultData.size(): " + resultData.size());
            for (DataModel d : resultData) {
                System.out.println("resultData content: " + d.getSessionContent());
            }
        }
        return resultData;
    }


    private void processExportDataWithDialog() {
        final JDialog loading = initWaitingDialog();

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws InterruptedException {
                processExportData();
                return "ok";
            }

            @Override
            protected void done() {
                loading.dispose();
            }
        };
        worker.execute();
        loading.setVisible(true);
        try {
            worker.get();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private JDialog initWaitingDialog() {
        final JDialog loading = new JDialog(mainFrame);
        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(new JLabel("Počkejte prosím..."), BorderLayout.CENTER);
        loading.setUndecorated(true);
        loading.getContentPane().add(p1);
        loading.pack();
        loading.setLocationRelativeTo(mainFrame);
        loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loading.setModal(true);
        return loading;
    }
}