package com.itachallenge.challenge.service;

import org.springframework.stereotype.Service;

@Service
public class ChallengeServiceImpl implements ChallengeService{
    @Override
    public boolean deleteByResourceId(Long resourceId) {
        return true;
    }
}
