package Verve.assignment.Verve.controller;

import Verve.assignment.Verve.service.RequestProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController

public class RequestController {

    @Autowired
    protected RequestProcessorService requestProcessorService;

    @GetMapping("/api/verve/accept")
    public ResponseEntity<String> acceptRequest(
            @RequestParam("id") Integer id,
            @RequestParam(value = "endpoint", required = false) String endpoint) {

        try {

            requestProcessorService.processRequest(id, endpoint);
            return ResponseEntity.ok("Request processed successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("failed");
        }
    }
}