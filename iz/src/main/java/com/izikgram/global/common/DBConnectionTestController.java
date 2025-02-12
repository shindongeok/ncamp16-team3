package com.izikgram.global.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class DBConnectionTestController {
    private final DBConnectionTestService dbConnectionTestService;

    @GetMapping("/db")
    public ResponseEntity<String> checkDB() {
        String dbTime = dbConnectionTestService.checkDatabaseConnection();
        return ResponseEntity.ok("DB Connection Successful! Current DB Time: " + dbTime);
    }
}
