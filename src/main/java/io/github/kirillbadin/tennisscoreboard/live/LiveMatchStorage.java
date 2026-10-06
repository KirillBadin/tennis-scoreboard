package io.github.kirillbadin.tennisscoreboard.live;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LiveMatchStorage {
    private final Map<UUID, LiveMatch> storage = new ConcurrentHashMap<>();

    public void save(LiveMatch liveMatch) {
        storage.put(liveMatch.getUuid(), liveMatch);
        System.out.println("Сохранили матч" + liveMatch.getUuid());
    }
}
