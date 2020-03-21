package io.javabrains.coronavirustracker.moduls;

import org.springframework.context.annotation.Bean;

public class LocationStates {
    private String state;
    private String country;
    private int latestTotalCases;
    private int diffFromPreday;


    @Override
    public String toString() {
        return "LocationStates{" +
                "state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", latestTotalCases=" + latestTotalCases +
                ", diffFromPreday=" + diffFromPreday +
                '}';
    }

    public int getDiffFromPreday() {
        return diffFromPreday;
    }

    public void setDiffFromPreday(int diffFromPreday) {
        this.diffFromPreday = diffFromPreday;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getLatestTotalCases() {
        return latestTotalCases;
    }

    public void setLatestTotalCases(int latestTotalCases) {
        this.latestTotalCases = latestTotalCases;
    }


}
