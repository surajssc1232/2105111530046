package com.example.demo.Service;

import java.util.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

@Service
public class NumberService {
    private final RestTemplate restTemplate;
    private final LinkedList<Integer> window;
    private List<Integer> prevWindowState;
    private static final int WINDOW_SIZE = 10;
    

    public NumberService() {
        restTemplate = new RestTemplate();
        window = new LinkedList<>();
        prevWindowState = new ArrayList<>();
    }

    public List<Integer> fetchNumbers(String type) {
        String url = "";
        if (type.equals("p")) {
            url = "http://20.244.56.144/test/primes";
        } else if (type.equals("f")) {
            url = "http://20.244.56.144/test/fibo";
        } else if (type.equals("e")) {
            url = "http://20.244.56.144/test/even";
        } else if (type.equals("r")) {
            url = "http://20.244.56.144/test/rand";
        } else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiZXhwIjoxNzI0ODI3NDE3LCJpYXQiOjE3MjQ4MjcxMTcsImlzcyI6IkFmZm9yZG1lZCIsImp0aSI6IjA0ODQzNzJhLWNmYzUtNDA5MC04MDEwLWUwNjZlNDY0MjMwZiIsInN1YiI6InNzLmNoYXVoYW4yMDIxQGdsYmFqYWpncm91cC5vcmcifSwiY29tcGFueU5hbWUiOiJhZmZvcmRtZWQiLCJjbGllbnRJRCI6IjA0ODQzNzJhLWNmYzUtNDA5MC04MDEwLWUwNjZlNDY0MjMwZiIsImNsaWVudFNlY3JldCI6InJZVEtWWmNJdmN2eHlBaXkiLCJvd25lck5hbWUiOiJzdXJhaiBzaW5naCBjaGF1aGFuIiwib3duZXJFbWFpbCI6InNzLmNoYXVoYW4yMDIxQGdsYmFqYWpncm91cC5vcmciLCJyb2xsTm8iOiIyMTA1MTExNTMwMDQ2In0.jCCSr78fwLSvgWGfETolSlSQiJxYVxwpDHSlxgOG6VU");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
    
        return (List<Integer>) response.getBody().get("numbers");
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