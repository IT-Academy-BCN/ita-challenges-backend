package com.itachallenge.gamification.repository;

import java.util.UUID;

public interface UserScoreAggregation {
    UUID get_id();
    String getUsername();
    int getTotalPoints();
}
