package com.bookstore.repository;

import com.bookstore.domain.ReferAFriend;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferAFriendRepository extends CrudRepository<ReferAFriend, Long> {

    public static final String FIND_INVITATION_COUNT_BY_EMAIL =
            "SELECT r from ReferAFriend r " +
            "WHERE r.emailId= ?1";

    @Query(FIND_INVITATION_COUNT_BY_EMAIL)
    ReferAFriend findInvitationCountByEmail(String email);

    List<ReferAFriend> findByUserId(Long userId);
}
