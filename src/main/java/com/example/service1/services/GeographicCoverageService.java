package com.example.service1.services;

import jakarta.ejb.Singleton;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Singleton
public class GeographicCoverageService {

    private List<String> supportedRegions = new ArrayList<>();

    private GeographicCoverageService() {
        supportedRegions = new ArrayList<>(Arrays.asList("cairo", "giza", "alexandria"));
    }
    private static GeographicCoverageService instance;
    public static synchronized GeographicCoverageService getInstance() {
        if (instance == null) {
            instance = new GeographicCoverageService();
        }
        return instance;
    }
    public boolean isRegionSupported(String region) {return supportedRegions.contains(region);}
}



