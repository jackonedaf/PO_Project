package org.project;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AirQualityDataFetcher {
    private static final Logger logger = LogManager.getLogger(AirQualityDataFetcher.class);

    public static List<DanePomiarowe> fetchData(String url, String location) throws IOException {
        List<DanePomiarowe> dataList = new ArrayList<>();

        Document document = Jsoup.connect(url).get();
        Elements rows = document.select("tr");

        for (Element row : rows) {
            Elements columns = row.select("td");
            if (columns.size() >= 6) {
                String currentLocation = columns.get(1).text();
                if (currentLocation.contains(location)) {
                    DanePomiarowe danePomiarowe = new DanePomiarowe();
                    danePomiarowe.setIdStacji(columns.get(0).text());
                    danePomiarowe.setStacja(columns.get(1).text());
                    danePomiarowe.setDataPomiaru(columns.get(2).text());
                    danePomiarowe.setGodzinaPomiaru(columns.get(3).text());
                    danePomiarowe.setTemperatura(columns.get(4).text());
                    danePomiarowe.setCisnienie(columns.get(9).text());
                    dataList.add(danePomiarowe);
                }
            }
        }
        logger.debug("Fetched data: {}", dataList);
        return dataList;
    }

    public static List<Wojewodztwo> fetchAirQualityBlocks(String url) throws IOException {

        List<Wojewodztwo> ListOfRegions = new ArrayList<>();

        Document document = Jsoup.connect(url).get();
        int whichValue=0;
        for(int i=2; i<=17; i++){
            Elements rows = document.select("body table:last-child tr:nth-child(" + i +") td");
            Wojewodztwo tempRegion = new Wojewodztwo();
            for(Element row:rows){
                switch (whichValue){
                    case 0:
                          tempRegion.setNazwaWojewodztwa(row.text());
                        break;
                    case 1:
                        try{
                            tempRegion.setPm25(Integer.parseInt(row.text()));
                        }catch (Exception er){
                            logger.error("Cannot get PM 2.5, error occured: " + er.getMessage());
                        }
                        break;
                    case 4:
                        try{
                            tempRegion.setPm10(Integer.parseInt(row.text()));
                        }catch (Exception er){
                            logger.error("Cannot get PM 10, error occured: " + er.getMessage());
                        }
                        break;
                }

                whichValue++;
            }
            whichValue = 0;
            ListOfRegions.add(tempRegion);
        }
//        for(Element row:rows){
//            System.out.println(row.text());
//        }

//        System.out.println(document);

//        logger.info("Fetched {} rows", rows.size());
//
//        for (Element row : rows) {
//            String currentLocation = row.select(".row").text();
//                blocks.add(row);
//                logger.debug("Block added: {}", row);
//
//        }

        return ListOfRegions;
    }

    public static class CommonData {
        private String searchableName;

        public CommonData(String searchableName) {
            this.searchableName = searchableName;
        }

        public String getSearchableName() {
            return searchableName;
        }
    }
    public static DanePomiarowe parseAirQualityData(Element colMd3Element) {
        DanePomiarowe danePomiarowe = new DanePomiarowe();

        danePomiarowe.setStacja(colMd3Element.select(".h3").text());

        Elements measurements = colMd3Element.select("li");
        for (Element measurement : measurements) {
            String key = measurement.ownText();
            String value = measurement.select("b").text();

            switch (key) {
                case "PM2.5:":
                    danePomiarowe.setPm25(value);
                    break;
                case "PM10:":
                    danePomiarowe.setPm10(value);
                    break;
                // Dodaj pozostałe przypadki w razie potrzeby
            }
        }
        logger.debug("dane {}",danePomiarowe);
        return danePomiarowe;
    }





    public static class DanePomiarowe  {
        private String idStacji;
        private String stacja;
        private String dataPomiaru;
        private String godzinaPomiaru;
        private String temperatura;
        private String cisnienie;
        private String pm10;
        private String pm25;


        // Getters and setters (you can generate them automatically in your IDE)

        public String getIdStacji() {
            return idStacji;
        }

        public void setIdStacji(String idStacji) {
            this.idStacji = idStacji;
        }

        public String getStacja() {
            return stacja;
        }

        public void setStacja(String stacja) {
            this.stacja = stacja;
        }

        public String getDataPomiaru() {
            return dataPomiaru;
        }

        public void setDataPomiaru(String dataPomiaru) {
            this.dataPomiaru = dataPomiaru;
        }

        public String getGodzinaPomiaru() {
            return godzinaPomiaru;
        }

        public void setGodzinaPomiaru(String godzinaPomiaru) {
            this.godzinaPomiaru = godzinaPomiaru;
        }

        public String getTemperatura() {
            return temperatura;
        }

        public void setTemperatura(String temperatura) {
            this.temperatura = temperatura;
        }

        public String getCisnienie() {
            return cisnienie;
        }

        public void setCisnienie(String cisnienie) {
            this.cisnienie = cisnienie;
        }
        public String getPm10() {return pm10;}

        public void setPm10(String pm10) {this.pm10 = pm10;}

        public String getPm25() {return pm25;}

        public void setPm25(String pm25) {this.pm25 = pm25;}

    }

    public static class Wojewodztwo {

        public Wojewodztwo() {
        }

        String nazwaWojewodztwa;

        int pm25;

        int pm10;

        public String getNazwaWojewodztwa()  {
            return nazwaWojewodztwa;
        }

        public void setNazwaWojewodztwa(String nazwaWojewodztwa) {
            this.nazwaWojewodztwa = nazwaWojewodztwa;
        }

        public int getPm25() {
            return pm25;
        }

        public void setPm25(int pm25) {
            this.pm25 = pm25;
        }

        public int getPm10() {
            return pm10;
        }

        public void setPm10(int pm10) {
            this.pm10 = pm10;
        }



        @Override
        public String toString() {
            return "Wojewodztwo{" +
                    "nazwaWojewodztwa='" + nazwaWojewodztwa + '\'' +
                    ", pm25=" + pm25 +
                    ", pm10=" + pm10 +
                    '}';
        }
    }
}

