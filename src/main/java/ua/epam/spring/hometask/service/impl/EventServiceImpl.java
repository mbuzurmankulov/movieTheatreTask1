package ua.epam.spring.hometask.service.impl;

import ua.epam.spring.hometask.domain.Event;
import ua.epam.spring.hometask.service.EventService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class EventServiceImpl implements EventService {

    private Long currentId;
    private final Map<Long, Event> eventMap;

    public EventServiceImpl(Long currentId, Map<Long, Event> eventMap){
        this.currentId = currentId;
        this.eventMap = eventMap;
    }

    @Nullable
    @Override
    public Event getByName(@Nonnull String name) {
        return (Event) eventMap.entrySet().stream()
                .filter(e -> e.getValue().getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Nonnull
    @Override
    public Set<Event> getForDateRange(@Nonnull LocalDate from, @Nonnull LocalDate to) {
        return eventMap.entrySet().stream()
                .map(e -> e.getValue())
                .filter(e -> e.airsOnDates(from, to))
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Set<Event> getAfterDateTime(@Nonnull LocalDateTime from) {
        return eventMap.entrySet().stream()
                .map(e -> e.getValue())
                .filter(e -> e.airsAfterDate(from))
                .collect(Collectors.toSet());
    }

    @Nonnull
    @Override
    public Set<Event> getNextEvents(@Nonnull LocalDateTime to) {
        LocalDateTime now = LocalDateTime.now();
        return eventMap.entrySet().stream()
                .map(e -> e.getValue())
                .filter(e -> e.airsOnDateTimes(now, to))
                .collect(Collectors.toSet());
    }

    @Override
    public Event save(@Nonnull Event object) {
        if(object.getId() == null) {
            object.setId(++currentId);
        }
        eventMap.put(object.getId(), object);
        return object;
    }

    @Override
    public void remove(@Nonnull Event object) {
        eventMap.remove(object.getId());
    }

    @Override
    public Event getById(@Nonnull Long id) {
        return eventMap.get(id);
    }

    @Nonnull
    @Override
    public Collection<Event> getAll() {
        return eventMap.entrySet().stream()
                .map(e -> e.getValue())
                .collect(Collectors.toList());
    }
}
