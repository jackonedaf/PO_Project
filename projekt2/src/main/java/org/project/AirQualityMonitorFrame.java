package org.project;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.jsoup.nodes.Element;

public class AirQualityMonitorFrame extends JFrame {
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AirQualityMonitorFrame.class);

    private final JButton refreshButton;
    private final JButton searchButton;
    private final AirQualityDataTablePanel dataTablePanel;
    private final JTextField locationTextField;
    private final AirQualityMonitorApp airQualityMonitorApp;
    private final JMenuBar menuBar;
    private final JMenu dataSourceMenu;
    private final JMenuItem source1MenuItem;
    private final JMenuItem source2MenuItem;
    private String currentDataSourceUrl;
    private boolean isBasicView = true;


    public AirQualityMonitorFrame(AirQualityMonitorApp parentApp) {
        setTitle("Air Quality Monitor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> refreshData());

        locationTextField = new JTextField("Enter Location");
        locationTextField.addActionListener(e -> searchData(isBasicView));

        dataTablePanel = new AirQualityDataTablePanel(isBasicView);

        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchData(isBasicView));

        menuBar = new JMenuBar();
        dataSourceMenu = new JMenu("Data Source");
        source1MenuItem = new JMenuItem("Source 1");
        source2MenuItem = new JMenuItem("Source 2");

        source1MenuItem.addActionListener(e -> {
            isBasicView = true;
            currentDataSourceUrl = "https://danepubliczne.imgw.pl/api/data/synop/format/html";
            dataTablePanel.setBasicView(true);
            refreshData();
        });


        source2MenuItem.addActionListener(e -> {
            isBasicView = false;
            currentDataSourceUrl = "https://powietrze.gios.gov.pl/pjp/current";
            dataTablePanel.setBasicView(false);
            refreshData();
        });

        dataSourceMenu.add(source1MenuItem);
        dataSourceMenu.add(source2MenuItem);
        menuBar.add(dataSourceMenu);

        setJMenuBar(menuBar);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(refreshButton, BorderLayout.WEST);
        controlPanel.add(locationTextField, BorderLayout.CENTER);
        controlPanel.add(searchButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(dataTablePanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
        airQualityMonitorApp = parentApp;
    }

    private void refreshData() {
        String location = "";
        refreshButton.setEnabled(false);

        SwingWorker<Void, Void> dataRefreshWorker = createDataRefreshWorker(location);
        dataRefreshWorker.execute();

        try {
            dataRefreshWorker.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logError("Error during data refresh", e);
        }
    }

    private SwingWorker<Void, Void> createDataRefreshWorker(String location) {
        return new SwingWorker<>() {
            List<AirQualityDataFetcher.DanePomiarowe> fetchedData;
            List<AirQualityDataFetcher.Wojewodztwo> airQualityBlocks;
            @Override
            protected Void doInBackground() {
                try {
                    if (isBasicView) {
                        fetchedData = AirQualityDataFetcher.fetchData(currentDataSourceUrl, location);
                        SwingUtilities.invokeLater(() -> {
                            dataTablePanel.clearTable();
                            fetchedData.forEach(dataTablePanel::updateTable);
                        });
                    } else {
                        airQualityBlocks = AirQualityDataFetcher.fetchAirQualityBlocks("https://powietrze.gios.gov.pl/pjp/content/measuring_air_assessment_measurings");
                        logger.debug("Air quality blocks: " + airQualityBlocks);
                        SwingUtilities.invokeLater(() -> {
                            dataTablePanel.clearTable();
                            airQualityBlocks.forEach(dataTablePanel::updateTable);
                        });
                    }
                } catch (IOException e) {
                    handleDataFetchError(e);
                    logger.error("Error fetching data: {}", e.getMessage());
                } finally {
                    refreshButton.setEnabled(true);
                }
                return null;
            }
            @Override
            protected void done() {
                // Update dataList reference in the AirQualityDataTablePanel
                boolean test=isBasicView;
                if(test) {
                    dataTablePanel.setDataList(fetchedData);
                }else{
                    dataTablePanel.setDataList2(airQualityBlocks);
                }
            }
        };
    }

    private void searchData(boolean view) {
        String location = locationTextField.getText().trim();
        if (!location.isEmpty()) {
            dataTablePanel.searchData(location,view);
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a location before searching.", "Empty Location", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleDataFetchError(IOException e) {
        logError("Error fetching data", e);
    }

    private void logError(String message, Throwable throwable) {
        Logger logger = Logger.getLogger(AirQualityMonitorFrame.class);
        logger.error(message, throwable);
    }
}
