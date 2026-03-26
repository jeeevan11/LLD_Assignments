package com.bookmyshow.service;

import com.bookmyshow.model.Theatre;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TheatreService {
    private final Map<String, Theatre> theatreStore = new ConcurrentHashMap<>();

    public void addTheatre(Theatre theatre) {
        theatreStore.put(theatre.getTheatreId(), theatre);
        System.out.println("[ADMIN] Theatre added: " + theatre);
    }

    public Theatre getTheatre(String theatreId) {
        return theatreStore.get(theatreId);
    }

    public List<Theatre> getTheatresByCity(String city) {
        return theatreStore.values().stream()
                .filter(t -> t.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }
}
