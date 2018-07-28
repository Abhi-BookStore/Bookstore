package com.bookstore.service;

import com.bookstore.domain.ReferAFriend;
import com.bookstore.domain.User;

import java.util.List;

public interface ReferAFriendService {

    void save(ReferAFriend referAFriend);
    ReferAFriend findById(Long id);
    List<ReferAFriend> findByJoinedStatus();
    List<ReferAFriend> findByInvitedStatus();
    void revokeReferAFriend(ReferAFriend referAFriend);
    int fetchTheInvitationCount(String email);
    List<ReferAFriend> findAllReferredFriendsByUserId(Long id) throws Exception;
}
