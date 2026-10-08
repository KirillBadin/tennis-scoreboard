package io.github.kirillbadin.tennisscoreboard.live;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LiveMatchStorage {
    private final Map<UUID, LiveMatch> storage = new ConcurrentHashMap<>();

    public void save(LiveMatch liveMatch) {
        storage.put(liveMatch.getUuid(), liveMatch);
    }

    public Optional<LiveMatch> getMatch(UUID uuid){
        return Optional.ofNullable(storage.get(uuid));
    }

    public void remove(UUID uuid) {
        storage.remove(uuid);
    }
}
