package com.br.app.authapi.modules.services;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class LogService {

    private static final String LOG_FILE = "src/main/resources/log.txt";

    public void logAction(String action) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(LocalDateTime.now() + " - " + action + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
