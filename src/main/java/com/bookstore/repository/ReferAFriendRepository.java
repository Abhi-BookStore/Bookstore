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
            "WHERE r.emailId= ?2 and r.user.id=?1";

    public static final String FIND_INVITATION_USER_BY_EMAIL =
            "SELECT r from ReferAFriend r " +
                    "WHERE r.emailId= ?2 and r.userId=?1";


    @Query(FIND_INVITATION_COUNT_BY_EMAIL)
    ReferAFriend findInvitationCountByEmail(Long userId, String email);

    List<ReferAFriend> findByUserId(Long userId);

    ReferAFriend findByEmailId(String emailId);

//    @Query(FIND_INVITATION_USER_BY_EMAIL)
//    ReferAFriend searchInvitedPersonByUser(Long userId, String email);
}
