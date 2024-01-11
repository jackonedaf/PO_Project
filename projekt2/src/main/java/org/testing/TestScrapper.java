package org.testing;

import org.project.AirQualityDataFetcher;

import java.io.IOException;

public class TestScrapper {
    AirQualityDataFetcher test;

    public TestScrapper() {
        test = new AirQualityDataFetcher();
    }

    public void testAir() throws IOException{
        test.fetchAirQualityBlocks("https://powietrze.gios.gov.pl/pjp/content/measuring_air_assessment_measurings");

    }
}
