package com.example.romaninsurance;

import com.example.romaninsurance.ewa.EwaRestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vin")
public class Api {

    private final EwaRestClient ewaRestClient;

    @Value("${security.token}")
    private String securityToken;

    @GetMapping
    public ResponseEntity vinBy(@RequestParam String number,
                                @RequestHeader(name = HttpHeaders.AUTHORIZATION) String auth) {
        try {

            if (!securityToken.equals(auth)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            String vin = ewaRestClient.vinBy(number);
            stopWatch.stop();
            log.info("Fetched vin: {} by number: {}, duration: {} secs", vin, number, stopWatch.getTotalTimeSeconds());
            Map<String, String> map = new HashMap<>(1);
            map.put("vin", vin);
            return ResponseEntity.ok(map);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("System exception, contact to administrator.");
        }
    }
}
