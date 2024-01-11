package org.project;

import org.apache.log4j.BasicConfigurator;
import org.testing.TestScrapper;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    static {
        // Set up log4j configuration
        // (You can also configure log4j through an external configuration file)
        BasicConfigurator.configure();
    }
    public static void main(String[] args) throws SQLException, IOException {
        AirQualityMonitorApp airQualityMonitorApp = new AirQualityMonitorApp();
        airQualityMonitorApp.run();
//        TestScrapper test =new TestScrapper();
//        test.testAir();
        // https://powietrze.gios.gov.pl/pjp/content/measuring_air_assessment_measurings
    }
}