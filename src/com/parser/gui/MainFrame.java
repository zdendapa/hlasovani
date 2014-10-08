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

import static javax.swing.GroupLayout.Alignment.LEADING;

public class MainFrame extends JFrame {
    public static boolean DEBUG = false;
    static MainFrame mainFrame;
    Container rootContainer;
    JButton loadFileButton;
    JButton exportFileButton;
    JButton adminButton;

    JTable table;
    JFileChooser chooser;


    private static final String stringForButtonBegin = "Načíst";
    private static final String stringForExport = "Export";
    private static final String stringForAdmin = "Admin";
    private static final String stringMainWindowName = "Parser utility";
    private static final String stringSelectImportFolder = "Select input folder";
    private static final String stringMessageChooseAnotherInputFolder = "Input folder is not chosen. Please try again.";
    private static final String stringEnterPass = "Enter a password:";
    private static final String stringTruePassword = "Hilmar";
    private static final String stringCantStartImport = "Can't export. No data in table.\nPlease load data for the first.";
    private static final String stringBadCUsneseni = "Can't export. Please, initialize all Č. usneseni values.\nValue must be not equals 0.";
    private static final String stringDataSuccessfullyExported = "Data successfully exported.";
    private static final String stringDataNotSuccessfullyExported = "During export occured error.\nPlease check that output folder exists.";
    private static final String stringNoValidFileForParsing = "No valid files were found.\nPlease try to use another input folder";
    private static final String  stringNoMatchingFiles="Please choose another input folder.\nNo matching [0-9]{4}.xml files found.";
    public MainFrame() {
        initUserInterfaceElements();
    }

    private void initUserInterfaceElements() {

        rootContainer = getContentPane();
        GroupLayout groupLayout = new GroupLayout(rootContainer);
        rootContainer.setLayout(groupLayout);

        loadFileButton = new JButton(stringForButtonBegin);
        exportFileButton = new JButton(stringForExport);
        adminButton = new JButton(stringForAdmin);

        addActionForLoadFileButton();
        addActionForAdminButton();
        addActionForExportButton();

        table = new JTable(new TableModel());
        JScrollPane scrollPane = new JScrollPane(table);
        loadTable();
        rootContainer.add(scrollPane);

        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup(LEADING)
                        .addComponent(scrollPane)
                )
                .addGroup(groupLayout.createParallelGroup(LEADING)
                        .addComponent(loadFileButton)
                        .addComponent(exportFileButton)
                        .addComponent(adminButton))
        );

        groupLayout.setVerticalGroup(groupLayout.createParallelGroup()

                .addComponent(scrollPane)

                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(loadFileButton)
                        .addComponent(exportFileButton)
                        .addComponent(adminButton)
                )
        );

        pack();

        setTitle(stringMainWindowName);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
                        null, options, options[1]);
                if (option == 0) // pressing OK button
                {
                    String password = new String(pass.getPassword());

                    if (stringTruePassword.equals(password)) {
                        ApplicationObjects.getInstance().setAdminLogged(true);
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
                        if ((Integer) dataFromTable[i][3] == 0) {
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
        table.getColumnModel().getColumn(2).setPreferredWidth(280);
        table.getColumnModel().getColumn(3).setPreferredWidth(75);
        table.getColumnModel().getColumn(4).setPreferredWidth(30);
        table.getColumnModel().getColumn(5).setPreferredWidth(30);
        table.getColumnModel().getColumn(6).setPreferredWidth(30);
        table.getColumnModel().getColumn(7).setPreferredWidth(30);
        table.getColumnModel().getColumn(8).setPreferredWidth(60);
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
            inputData.get(i).setOrderNumber((Integer) dataFromTable[i][3]);
        }
        try {
            DataParser.getInstance().exportDataToXml();
            JOptionPane.showMessageDialog(null, stringDataSuccessfullyExported);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, stringDataNotSuccessfullyExported);
            e.printStackTrace();
        }
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
            for (int i = 0; i < dataModelFromXML.size(); i++) {
                tableModel.addRow(new Object[]{
                        dataModelFromXML.get(i).getNumber(),
                        dataModelFromXML.get(i).getTime(),
                        dataModelFromXML.get(i).getSessionContent(),
                        new Integer(0),
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
        p1.add(new JLabel("Please wait..."), BorderLayout.CENTER);
        loading.setUndecorated(true);
        loading.getContentPane().add(p1);
        loading.pack();
        loading.setLocationRelativeTo(mainFrame);
        loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loading.setModal(true);
        return loading;
    }
}