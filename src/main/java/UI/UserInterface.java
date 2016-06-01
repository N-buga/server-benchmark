package UI;

import client.Client;
import client.ClientTCP;
import client.ClientUDP;
import com.google.common.primitives.Doubles;
import javafx.embed.swing.SwingFXUtils;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import servers.BaseServer;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by n_buga on 28.05.16.
 */
public final class UserInterface {

    private static final JFrame FRAME = new JFrame("Client");
    private static final Container CONTENT_PANE = FRAME.getContentPane();
    private static final String DESCRIPTION_SIZE_ARRAY = "Array size:";
    private static final String DESCRIPTION_COUNT_OF_CLIENTS = "Count of clients:";
    private static final String DESCRIPTION_DELTA_TIME = "Delta time:";
    private static final String DESCRIPTION_COUNT_OF_QUERIES = "Count of queries:";
    private static final String HIDE_SEPARATE_GRAPHS = "hide separate";
    private static final String SHOW_SEPARATE_GRAPHS = "show separate";
    private static Map<String, Parameter> parameters = new HashMap<>();
    private static JFrame frameClient = new JFrame();
    private static JFrame frameQueryHandler = new JFrame();
    private static JFrame frameQueryCount = new JFrame();
    private static JComboBox showHide;

    static {
        parameters.put(DESCRIPTION_COUNT_OF_CLIENTS, new Parameter(1, 1, 1, false));
        parameters.put(DESCRIPTION_DELTA_TIME, new Parameter(0, 0, 1, false));
        parameters.put(DESCRIPTION_SIZE_ARRAY, new Parameter(1, 1, 1, false));
        parameters.put(DESCRIPTION_COUNT_OF_QUERIES, new Parameter(1, 1, 1, false));
    }

    private void createChart() throws Exception {
        double[] xData = new double[] { 0.0, 1.0, 2.0 };
        double[] yData = new double[] { 2.0, 1.0, 0.0 };

        // Create Chart
        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);

        // Show it
        new SwingWrapper(chart).displayChart();
    }

    public static void main(String[] args) {
        JTextField jTextField = new JTextField("127.0.0.1");
        JLabel labelEnterIP = new JLabel("server ip:");
        JLabel labelArchitecture = new JLabel("Architecture:");

        String[] architectures = {"TCP.OneClientOneThread", "NIO.OneThread", "TCP.CachedThreadPoolServer",
                "TCP.NewQueryNewConnection", "NIO.CachedThreadPool", "NIO.FixedThreadPool", "NIO.NewQueryNewThread",
                "UDP.FixedThreadPool", "UDP.NewQueryNewThread", "CountForAll"};
        String[] changeableParameter = {"clients", "Delta", "size"};
        String[] showHideParameter = {HIDE_SEPARATE_GRAPHS, SHOW_SEPARATE_GRAPHS};

        showHide = new JComboBox<>(showHideParameter);
        showHide.addActionListener(e -> {
            JComboBox source = (JComboBox) e.getSource();
            String selectedItem = (String) source.getSelectedItem();
            if (selectedItem.equals(SHOW_SEPARATE_GRAPHS)) {
                frameClient.setVisible(true);
                frameQueryHandler.setVisible(true);
                frameQueryCount.setVisible(true);
            } else {
                frameClient.setVisible(false);
                frameQueryHandler.setVisible(false);
                frameQueryCount.setVisible(false);
            }
        });
        JComboBox choiceArchitecture = new JComboBox<>(architectures);
        JComboBox choiceParameter = new JComboBox<>(changeableParameter);

        Box upBox = Box.createHorizontalBox();
        upBox.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        upBox.add(labelEnterIP);
        upBox.add(jTextField);
        upBox.add(Box.createHorizontalStrut(7));
        upBox.add(labelArchitecture);
        upBox.add(choiceArchitecture);
        upBox.add(Box.createHorizontalStrut(7));
        upBox.add(new JLabel("Changeable parameter:"));
        upBox.add(choiceParameter);
        upBox.add(Box.createHorizontalGlue());
        upBox.add(new JLabel("Graphs:"));
        upBox.add(showHide);
        upBox.add(Box.createHorizontalGlue());

        choiceParameter.addActionListener(e -> {
            JComboBox source = (JComboBox) e.getSource();
            String selectedItem = (String) source.getSelectedItem();
            JPanel settingsPanel;
            if (selectedItem.equals(changeableParameter[0])) {
                settingsPanel = createSettingsForClients();
            } else if (selectedItem.equals(changeableParameter[1])) {
                settingsPanel = createSettingsForDelta();
            } else {
                settingsPanel = createSettingsForSize();
            }
            BorderLayout contentLayout = (BorderLayout) CONTENT_PANE.getLayout();
            CONTENT_PANE.remove(contentLayout.getLayoutComponent(BorderLayout.WEST));
            CONTENT_PANE.add(settingsPanel, BorderLayout.WEST);
            drawFrame();
        });

        JPanel settingsPanel = createSettingsForClients();

        JButton doCount = new JButton("Count!");
        doCount.setPreferredSize(new Dimension(350, 25));
        doCount.addActionListener(e -> {
            String selectedItem = (String) choiceArchitecture.getSelectedItem();
            if (selectedItem.equals("CountForAll")) {
                SwingUtilities.invokeLater(() -> {
                            final JDialog dlg = new JDialog(FRAME, "Progress Dialog", true);
                            JProgressBar dpb = new JProgressBar(0, architectures.length - 1);
                            dlg.add(BorderLayout.CENTER, dpb);
                            dlg.add(BorderLayout.NORTH, new JLabel("Progress..."));
                            dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                            dlg.setSize(300, 75);
                            dlg.setLocationRelativeTo(FRAME);

                            (new Thread(() -> {
                                for (int i = 0; i < architectures.length - 1; i++) {
                                    final int j = i;
                                    SwingUtilities.invokeLater(() -> dpb.setValue(j));
                                    accountManager(architectures[i], jTextField.getText());
                                }
                                SwingUtilities.invokeLater(() -> dpb.setValue(architectures.length - 1));
                                SwingUtilities.invokeLater(() -> dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE));
//                                Utils.mergeFiles();
                            })).start();
                            dlg.setVisible(true);
                        }
                );
            } else {
                accountManager(selectedItem, jTextField.getText());
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(doCount);

        CONTENT_PANE.add(upBox, BorderLayout.NORTH);
        CONTENT_PANE.add(settingsPanel, BorderLayout.WEST);
        CONTENT_PANE.add(buttonPanel, BorderLayout.SOUTH);

        XYChart chart = QuickChart.getChart("Sample chart", "X", "Y", "Y(X)", new double[1], new double[1]);
        XChartPanel chartPanel = new XChartPanel(chart);

        CONTENT_PANE.add(chartPanel, BorderLayout.EAST);

        FRAME.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        FRAME.setSize(1200, 600);
        FRAME.setResizable(false);
        FRAME.setVisible(true);
    }

    private static JPanel createSettingsForClients() {
        JPanel settings = createSettings(DESCRIPTION_COUNT_OF_CLIENTS);

        JPanel settingArraySize = createSetting(DESCRIPTION_DELTA_TIME, "0");
        settings.add(settingArraySize);

        JPanel settingDeltaTime = createSetting(DESCRIPTION_SIZE_ARRAY, "1");
        settings.add(settingDeltaTime);

        settings.add(Box.createVerticalGlue());

        return settings;
    }

    private static JPanel createSettingsForDelta() {
        JPanel settings = createSettings(DESCRIPTION_DELTA_TIME);

        JPanel settingArraySize = createSetting(DESCRIPTION_SIZE_ARRAY, "1");
        settings.add(settingArraySize);

        JPanel settingDeltaTime = createSetting(DESCRIPTION_COUNT_OF_CLIENTS, "0");
        settings.add(settingDeltaTime);

        settings.add(Box.createVerticalGlue());

        return settings;
    }

    private static JPanel createSettingsForSize() {
        JPanel settings = createSettings(DESCRIPTION_SIZE_ARRAY);

        JPanel settingArraySize = createSetting(DESCRIPTION_COUNT_OF_CLIENTS, "1");
        settings.add(settingArraySize);

        JPanel settingDeltaTime = createSetting(DESCRIPTION_DELTA_TIME, "0");
        settings.add(settingDeltaTime);

        settings.add(Box.createVerticalGlue());

        return settings;
    }

    private static JPanel createSettings(String parameterDescription) {
        JPanel settings = createBaseSettingsPanel();

        parameters.get(parameterDescription).setChangeable(true);

        BorderLayout layoutForSetting = new BorderLayout();
        JPanel settingPanel = new JPanel(layoutForSetting);
        Box changeableSetting = Box.createHorizontalBox();
        changeableSetting.add(new JLabel(parameterDescription));
        changeableSetting.add(Box.createHorizontalStrut(5));
        changeableSetting.add(new JLabel("from "));
        JTextField textFieldFrom = new JTextField("1", 5);
        textFieldFrom.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                parameters.get(parameterDescription).setFrom(Integer.parseInt(textFieldFrom.getText()));
            }
        });
        changeableSetting.add(textFieldFrom);
        changeableSetting.add(Box.createHorizontalStrut(10));
        changeableSetting.add(new JLabel("to "));
        JTextField textFieldTo = new JTextField("1", 5);
        textFieldTo.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                parameters.get(parameterDescription).setTo(Integer.parseInt(textFieldTo.getText()));
            }
        });

        changeableSetting.add(textFieldTo);
        changeableSetting.add(Box.createHorizontalStrut(10));
        changeableSetting.add(new JLabel("with step "));
        JTextField textFieldStep = new JTextField("1", 5);
        textFieldStep.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                parameters.get(parameterDescription).setStep(Integer.parseInt(textFieldStep.getText()));
            }
        });

        changeableSetting.add(textFieldStep);
        settingPanel.add(changeableSetting, BorderLayout.NORTH);
        settingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        settings.add(settingPanel);

        return settings;
    }

    private static JPanel createBaseSettingsPanel() {
        JPanel settings = new JPanel();
        BoxLayout boxLayoutSettings = new BoxLayout(settings, BoxLayout.Y_AXIS);
        settings.setLayout(boxLayoutSettings);
        settings.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JLabel settingsLabel = new JLabel("Settings:");
        settingsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        settings.add(settingsLabel);
        settings.add(Box.createVerticalStrut(20));

        JPanel firstSettingPanel = createSetting(DESCRIPTION_COUNT_OF_QUERIES, "1");
        settings.add(firstSettingPanel);
        return settings;
    }

    private static JPanel createSetting(String name, String initialText) {
        BorderLayout layoutForSetting = new BorderLayout();
        JPanel settingPanel = new JPanel(layoutForSetting);
        Box setting = Box.createHorizontalBox();
        setting.add(new JLabel(name));
        setting.add(Box.createHorizontalStrut(5));
        JTextField textFieldForX = new JTextField(initialText, 5);
        textFieldForX.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                Parameter curParameter = parameters.get(name);
                int fieldValue = Integer.parseInt(textFieldForX.getText());
                curParameter.setFrom(fieldValue);
                curParameter.setTo(fieldValue);
                curParameter.setStep(1);
            }
        });
        setting.add(textFieldForX);
        settingPanel.add(setting, BorderLayout.NORTH);
        settingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return settingPanel;
    }

    private static void drawFrame() {
        FRAME.repaint();
        FRAME.setVisible(true);
    }

    public static void accountManager(String architectureName, String ip) {
        BaseServer server;
        try {
            Class architectureClass = Class.forName("servers." + architectureName);
            server = (BaseServer) architectureClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        server.start();

        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        int countOfQueries = parameters.get(DESCRIPTION_COUNT_OF_QUERIES).getFrom();

        Parameter arraySizeParameter = parameters.get(DESCRIPTION_SIZE_ARRAY);
        Parameter deltaTimeParameter = parameters.get(DESCRIPTION_DELTA_TIME);
        Parameter clientsParameter = parameters.get(DESCRIPTION_COUNT_OF_CLIENTS);

        ArrayList<Double> xDataClient = new ArrayList<>();
        ArrayList<Double> xDataQueryHandler = new ArrayList<>();
        ArrayList<Double> xDataQueryCount = new ArrayList<>();
        ArrayList<Double> yDataClient = new ArrayList<>();
        ArrayList<Double> yDataQueryHandler = new ArrayList<>();
        ArrayList<Double> yDataQueryCount = new ArrayList<>();

        String xString = "M";

        for (int arraySize = arraySizeParameter.getFrom(); arraySize <= arraySizeParameter.getTo();
                arraySize += arraySizeParameter.getStep()) {
            for (int deltaTime = deltaTimeParameter.getFrom(); deltaTime <= deltaTimeParameter.getTo();
                    deltaTime += deltaTimeParameter.getStep()) {
                for (int countOfClients = clientsParameter.getFrom(); countOfClients <= clientsParameter.getTo();
                        countOfClients += clientsParameter.getStep()) {
                    double averageClient = 0;
                    double averageQueryHandler = 0;
                    double averageQueryCount = 0;
                    ArrayList<Future<Long>> tasks = new ArrayList<>();
                    ArrayList<Client> clients = new ArrayList<>();
                    for (int j = 0; j < countOfClients; j++) {
                        final int finalArraySize = arraySize;
                        final int finalDeltaTime = deltaTime;
                        Client client;
                        if (architectureName.charAt(0) == 'U') {
                            client = new ClientUDP();
                        } else {
                            client = new ClientTCP();
                        }
                        clients.add(client);
                        Future<Long> averageTime = cachedThreadPool.submit(
                                () -> Utils.getQueries(server, countOfQueries, finalArraySize,
                                        finalDeltaTime, ip, client));
                        tasks.add(averageTime);
                    }
                    for (int j = 0; j < countOfClients; j++) {
                        try {
                            averageClient += tasks.get(j).get()/countOfClients;
                            averageQueryHandler += clients.get(j).getHandlerTimeQuery()/countOfClients;
                            averageQueryCount += clients.get(j).getHandlerCountQuery()/countOfClients;
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    yDataClient.add(averageClient);
                    yDataQueryHandler.add(averageQueryHandler);
                    yDataQueryCount.add(averageQueryCount);
                    if (arraySizeParameter.isChangeable()) {
                        xDataClient.add((double)arraySize);
                        xDataQueryHandler.add((double) arraySize);
                        xDataQueryCount.add((double) arraySize);
                        xString = "N";
                    } else if (deltaTimeParameter.isChangeable()) {
                        xDataClient.add((double) deltaTime);
                        xDataQueryHandler.add((double) deltaTime);
                        xDataQueryCount.add((double) deltaTime);
                        xString = "Delta";
                    } else {
                        xDataClient.add((double) countOfClients);
                        xDataQueryHandler.add((double) countOfClients);
                        xDataQueryCount.add((double) countOfClients);
                        xString = "M";
                    }
                    createXChart(architectureName, xDataClient, yDataClient, xDataQueryHandler, yDataQueryHandler,
                            xDataQueryCount, yDataQueryCount, xString);
                }
            }
        }
        server.close();
        if (showHide.getSelectedItem() == SHOW_SEPARATE_GRAPHS) {
            frameClient.setVisible(true);
            frameQueryHandler.setVisible(true);
            frameQueryCount.setVisible(true);
        }

        toFile(architectureName, xString, arraySizeParameter, deltaTimeParameter, clientsParameter,
                    xDataClient, yDataClient, "Client handler", countOfQueries);
        toFile(architectureName, xString, arraySizeParameter, deltaTimeParameter, clientsParameter,
                    xDataQueryHandler, yDataQueryHandler, "Query handler", countOfQueries);
        toFile(architectureName, xString, arraySizeParameter, deltaTimeParameter, clientsParameter,
                    xDataQueryCount, yDataQueryCount, "Query count", countOfQueries);
    }

    private static void createXChart(String architectureName, List<Double> xDataClient, List<Double> yDataClient,
                                     List<Double> xDataQueryHandler, List<Double> yDataQueryHandler,
                                     List<Double> xDataQueryCount, List<Double> yDataQueryCount,
                                     String xString) {

        XYChart chartClient = QuickChart.getChart("Client time", xString, "T, ms", "T(" + xString + ")",
                xDataClient, yDataClient);

        XYChart chartQueryHandler = QuickChart.getChart("Handler query time on Server", xString, "T, ms",
                "T(" + xString + ")", xDataQueryHandler, yDataQueryHandler);

        XYChart chartQueryCount = QuickChart.getChart("Execute query time on Server", xString, "T, ms",
                "T(" + xString + ")", xDataQueryCount, yDataQueryCount);

        XYChart chart = new XYChartBuilder().width(600).height(600).title(architectureName).
                xAxisTitle(xString).yAxisTitle("T, ms").build();

        chart.addSeries("Client time", xDataClient, yDataClient);

        chart.addSeries("Handler query time on Server", xDataQueryHandler, yDataQueryHandler);

        chart.addSeries("Execute query time on Server", xDataQueryCount, yDataQueryCount);

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setYAxisLabelAlignment(Styler.TextAlignment.Right);
        chart.getStyler().setPlotMargin(0);
        chart.getStyler().setPlotContentSize(.95);

        XChartPanel chartPanel = new XChartPanel(chart);
        // Show it
        BorderLayout contentLayout = (BorderLayout) CONTENT_PANE.getLayout();
        if (contentLayout.getLayoutComponent(BorderLayout.EAST) != null) {
            CONTENT_PANE.remove(contentLayout.getLayoutComponent(BorderLayout.EAST));
        }
        CONTENT_PANE.add(chartPanel, BorderLayout.EAST);
        drawFrame();

        XChartPanel chartPanelClient = new XChartPanel(chartClient);
        frameClient.getContentPane().add(chartPanelClient);
        frameClient.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frameClient.setSize(600, 400);

        XChartPanel chartPanelQueryHandler = new XChartPanel(chartQueryHandler);

        frameQueryHandler.getContentPane().add(chartPanelQueryHandler);
        frameQueryHandler.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frameQueryHandler.setSize(600, 400);

        XChartPanel chartPanelQueryCount = new XChartPanel(chartQueryCount);

        frameQueryCount.getContentPane().add(chartPanelQueryCount);
        frameQueryCount.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frameQueryCount.setSize(600, 400);
    }

    private static void toFile(String architecture, String xString,
                               Parameter arraySizeParameter, Parameter deltaParameter, Parameter clientsParameter,
                               List<Double> metricX, List<Double> metricY, String metric, int countOfQueries) {

        Path pathDirectoryResults = Paths.get(".", "results");
        if (!Files.exists(pathDirectoryResults)) {
            try {
                Files.createDirectory(pathDirectoryResults);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path pathDirectoryFiles = Paths.get(pathDirectoryResults.toString(), "Data_files");
        if (!Files.exists(pathDirectoryFiles)) {
            try {
                Files.createDirectory(pathDirectoryFiles);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String fileName = architecture + "_change_" + xString + "_metric_" + metric + ".csv";
        Path dataFilePath = Paths.get(pathDirectoryFiles.toString(), fileName);
        try {
            Files.deleteIfExists(dataFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Files.createFile(dataFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter printer = new PrintWriter(new FileOutputStream(dataFilePath.toString(), true))) {
            printer.printf("Count of queries = %d\n", countOfQueries);
            if (arraySizeParameter.isChangeable()) {
                printer.printf("Array size changes from %d to %d with step %d\n", arraySizeParameter.getFrom(),
                        arraySizeParameter.getTo(), arraySizeParameter.getStep());
                printer.printf("Delta = %d\n", deltaParameter.getFrom());
                printer.printf("Clients = %d\n", clientsParameter.getFrom());
            } else if (deltaParameter.isChangeable()) {
                printer.printf("Array size = %d\n", arraySizeParameter.getFrom());
                printer.printf("Delta changes from %d to %d with step %d\n", deltaParameter.getFrom(),
                        deltaParameter.getTo(), deltaParameter.getStep());
                printer.printf("Clients = %d\n", clientsParameter.getFrom());
            } else {
                printer.printf("Array size = %d\n", arraySizeParameter.getFrom());
                printer.printf("Delta = %d\n", deltaParameter.getFrom());
                printer.printf("Count of clients changes from %d to %d with step %d\n", clientsParameter.getFrom(),
                        clientsParameter.getTo(), clientsParameter.getStep());
            }
            printer.println(xString + ", Time ms");
            for (int i = 0; i < metricX.size(); i++) {
                printer.printf("%f %f\n", metricX.get(i), metricY.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
