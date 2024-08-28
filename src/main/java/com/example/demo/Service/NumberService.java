package com.example.demo.Service;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

@Service
public class NumberService {
    private final RestTemplate restTemplate;
    private final LinkedList<Integer> window;
    private List<Integer> prevWindowState;
    private static final int WINDOW_SIZE = 10;


    @Value("${auth.url}")
    private String authUrl;

    @Value("${numbers.primes.url}")
    private String primesUrl;

    @Value("${numbers.fibo.url}")
    private String fiboUrl;

    @Value("${numbers.even.url}")
    private String evenUrl;

    @Value("${numbers.rand.url}")
    private String randUrl;

    @Value("${companyName}")
    private String companyName;

    @Value("${clientID}")
    private String clientID;

    @Value("${clientSecret}")
    private String clientSecret;

    @Value("${ownerName}")
    private String ownerName;

    @Value("${ownerEmail}")
    private String ownerEmail;

    @Value("${rollNo}")
    private String rollNo;

    public NumberService() {
        restTemplate = new RestTemplate();
        window = new LinkedList<>();
        prevWindowState = new ArrayList<>();
    }

    private String obtainAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> authRequest = new HashMap<>();
        authRequest.put("companyName", companyName);
        authRequest.put("clientID", clientID);
        authRequest.put("clientSecret", clientSecret);
        authRequest.put("ownerName", ownerName);
        authRequest.put("ownerEmail", ownerEmail);
        authRequest.put("rollNo", rollNo);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(authRequest, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, entity, Map.class);
        return (String) response.getBody().get("access_token");
    }

    private List<Integer> fetchNumbers(String type) {
        String url = getUrlForType(type);
        String token = obtainAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            token = obtainAuthToken();
            headers.setBearerAuth(token);
            entity = new HttpEntity<>(headers);
            response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        }

        return (List<Integer>) response.getBody().get("numbers");
    }

    private String getUrlForType(String type) {
        switch (type) {
            case "p":
                return primesUrl;
            case "f":
                return fiboUrl;
            case "e":
                return evenUrl;
            case "r":
                return randUrl;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

    public Map<String, Object> processNumbers(String type) {
        List<Integer> numbers = fetchNumbers(type);
        prevWindowState = new ArrayList<>(window);

        numbers.stream().distinct().forEach(this::addToWindow);

        Map<String, Object> result = new HashMap<>();
        result.put("numbers", numbers);
        result.put("windowPrevState", prevWindowState);
        result.put("windowCurrState", new ArrayList<>(window));
        result.put("Average", calculateAverage());

        return result;
    }

    private void addToWindow(int num) {
        if (window.contains(num)) {
            return;
        }

        if (window.size() >= WINDOW_SIZE) {
            window.removeFirst();
        }
        window.addLast(num);
    }

    public double calculateAverage() {
        return window.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
}