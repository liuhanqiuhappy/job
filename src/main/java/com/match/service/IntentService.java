package com.match.service;

import com.match.entity.Intent;

import java.util.List;

public interface IntentService {

    void sendIntent(Intent intent);

    void acceptIntent(Long intentId);

    void rejectIntent(Long intentId);

    void expireIntent(Long intentId);

    List<Intent> getSentIntents(Long userId);

    List<Intent> getReceivedIntents(Long userId);

    Intent getById(Long id);

    boolean hasActiveIntent(Long fromUserId, Long toUserId, Long resumeId, Long jobId);
}