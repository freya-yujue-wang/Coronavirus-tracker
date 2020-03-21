package io.javabrains.coronavirustracker.services;





import ch.qos.logback.core.net.SyslogOutputStream;
import io.javabrains.coronavirustracker.moduls.LocationStates;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CoronaVirusDataService {
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private List<LocationStates> allStats = new ArrayList<>();

    public List<LocationStates> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStates> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(httpResponse.body());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);


        for (CSVRecord record : records) {
            LocationStates locationStates = new LocationStates();
            locationStates.setState(record.get("Province/State"));
            locationStates.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locationStates.setLatestTotalCases(latestCases);
            locationStates.setDiffFromPreday(latestCases - prevDayCases);
            //System.out.println(locationStates);
            newStats.add(locationStates);
        }
        int usTotal = newStats.stream().filter(stat -> stat.getCountry().equals("US")).mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int usDiff = newStats.stream().filter(stat -> stat.getCountry().equals("US")).mapToInt(stat -> stat.getDiffFromPreday()).sum();
        LocationStates us = new LocationStates();
        us.setDiffFromPreday(usDiff);
        us.setLatestTotalCases(usTotal);
        us.setCountry("US");

        Collections.sort(newStats, (s1, s2) -> {
            if(s1.getCountry().equals("US") && !s2.getCountry().equals("US")) {
                return -1;
            }
            if(!s1.getCountry().equals("US") && s2.getCountry().equals("US")) {
                return 1;
            }

            if(s1.getCountry().equals(s2.getCountry())) {
                return s1.getState().compareTo(s2.getState());
            } else {
                return s1.getCountry().compareTo(s2.getCountry());
            }
        });
        newStats.add(0, us);
        this.allStats = newStats;


    }
}
