package com.sattracker.backend.util;

import jakarta.annotation.PostConstruct;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class OrekitInitializer {

    @PostConstruct
    public void init() {
        try {
            File orekitData = new File("orekit-data");
            if (!orekitData.exists()) {
                throw new RuntimeException("orekit-data folder not found!");
            }

            DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
            manager.addProvider(new DirectoryCrawler(orekitData));

            System.out.println("Orekit Data Loaded Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load Orekit data: " + e.getMessage());
        }
    }
}
