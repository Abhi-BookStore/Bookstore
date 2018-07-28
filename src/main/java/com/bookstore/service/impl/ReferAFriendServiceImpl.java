package com.bookstore.service.impl;

import com.bookstore.domain.ReferAFriend;
import com.bookstore.repository.ReferAFriendRepository;
import com.bookstore.service.ReferAFriendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferAFriendServiceImpl implements ReferAFriendService {

    private static final Logger logger = LoggerFactory.getLogger(ReferAFriendServiceImpl.class);

    @Autowired
    private ReferAFriendRepository referAFriendRepository;

    @Override
    public void save(ReferAFriend referAFriend) {
        referAFriendRepository.save(referAFriend);
    }

    @Override
    public ReferAFriend findById(Long id) {
        return null;
    }

    @Override
    public List<ReferAFriend> findByJoinedStatus() {
        return null;
    }

    @Override
    public List<ReferAFriend> findByInvitedStatus() {
        return null;
    }

    @Override
    public void revokeReferAFriend(ReferAFriend referAFriend) {

    }

    @Override
    public int fetchTheInvitationCount(String email) {
        logger.info(":::: fetchTheInvitationCount for email : "+ email);

        ReferAFriend referAFriend = referAFriendRepository.findInvitationCountByEmail(email);
        logger.info("::::::::::: referAFriend invitation count: "+ referAFriend.getInvitationCount());
        if(null != referAFriend){
            return referAFriend.getInvitationCount();
        }
        return 0;
    }

    @Override
    public List<ReferAFriend> findAllReferredFriendsByUserId(Long userId) throws Exception {
        if(userId == null){
            throw new Exception("User Id is null.");
        }

        List<ReferAFriend> referAFriendList = referAFriendRepository.findByUserId(userId);
        logger.info(":::: referAFriendList size = "+ referAFriendList.size());
        return referAFriendList;
    }


}
