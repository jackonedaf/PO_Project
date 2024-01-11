package org.project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;


public class AirQualityDataTablePanel extends JPanel {
    private final DefaultTableModel tableModel;
    private boolean isBasicView;
    private  List<AirQualityDataFetcher.DanePomiarowe> dataList;
    private  List<AirQualityDataFetcher.Wojewodztwo> wojewodztwoList;


    public AirQualityDataTablePanel(boolean isBasicView) {
        this.isBasicView = isBasicView;
        this.dataList = new ArrayList<>();
        this.wojewodztwoList=new ArrayList<>();

        tableModel = new DefaultTableModel(getColumnHeaders(), 0);
        JTable dataTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(dataTable);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateTable(AirQualityDataFetcher.DanePomiarowe danePomiarowe) {
        SwingUtilities.invokeLater(() -> {
            if (isBasicView) {
                      tableModel.addRow(new String[]{
                        danePomiarowe.getIdStacji(),
                        danePomiarowe.getStacja(),
                        danePomiarowe.getDataPomiaru(),
                        danePomiarowe.getGodzinaPomiaru(),
                        danePomiarowe.getTemperatura(),
                        danePomiarowe.getCisnienie()
                });
            } else {
                tableModel.addRow(new String[]{
                        danePomiarowe.getIdStacji(),
                        danePomiarowe.getStacja(),
                        danePomiarowe.getDataPomiaru(),
                        danePomiarowe.getGodzinaPomiaru(),
                        danePomiarowe.getPm10(),
                        danePomiarowe.getPm25()
                });
            }

        });
    }

    public void updateTable(AirQualityDataFetcher.Wojewodztwo woj) {
        SwingUtilities.invokeLater(() -> {
            if (isBasicView) {} else {
                tableModel.addRow(new String[]{
                        woj.getNazwaWojewodztwa(),
                        String.valueOf(woj.getPm25()),
                        String.valueOf(woj.getPm10())
                });
            }

        });
    }

    public void clearTable() {
        SwingUtilities.invokeLater(() -> {
            int rowCount = tableModel.getRowCount();
            for (int i = rowCount - 1; i >= 0; i--) {
                tableModel.removeRow(i);
            }
        });
    }

    public void searchData(String location,boolean view) {
        SwingUtilities.invokeLater(() -> {
            if(view) {
                clearTable();  // Clear the table before populating with search results
                for (AirQualityDataFetcher.DanePomiarowe danePomiarowe : dataList) {
                    if (danePomiarowe.getStacja().contains(location)) {
                        updateTable(danePomiarowe);
                    }
                }
            }else{
                clearTable();  // Clear the table before populating with search results
                for (AirQualityDataFetcher.Wojewodztwo woj : wojewodztwoList) {
                    if (woj.getNazwaWojewodztwa().contains(location)) {
                        updateTable(woj);
                    }
                }
            }
        });
    }
    public void setDataList(List<AirQualityDataFetcher.DanePomiarowe> dataList) {
        this.dataList = dataList;
    }
    public void setDataList2(List<AirQualityDataFetcher.Wojewodztwo> wojewodztwoList) {
        this.wojewodztwoList = wojewodztwoList;
    }
    private String[] getColumnHeaders() {
        if (isBasicView) {
            return new String[]{"ID Stacji", "Stacja", "Data Pomiaru", "Godzina Pomiaru", "Temperatura", "Cisnienie"};
        } else {
            return new String[]{"Wojewodztwo", "PM25", "PM10"};
        }
    }

    public void setBasicView(boolean basicView) {
        isBasicView = basicView;
        updateTableHeaders();
    }

    private void updateTableHeaders() {
        SwingUtilities.invokeLater(() -> {
            String[] headers = getColumnHeaders();
            tableModel.setColumnIdentifiers(headers);
        });
    }
}
