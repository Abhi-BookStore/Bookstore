package com.bookstore.domain;

import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@ToString
@Entity
public class ReferAFriend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emailId;
    private Date invitationDate;
    private Date joiningDate;
    private Boolean isJoined;
    private int invitationCount;
    private String referralModel;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Date getInvitationDate() {
        return invitationDate;
    }

    public void setInvitationDate(Date invitationDate) {
        this.invitationDate = invitationDate;
    }

    public Date getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(Date joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Boolean getJoined() {
        return isJoined;
    }

    public void setJoined(Boolean joined) {
        isJoined = joined;
    }

    public int getInvitationCount() {
        return invitationCount;
    }

    public void setInvitationCount(int invitationCount) {
        this.invitationCount = invitationCount;
    }

    public String getReferralModel() {
        return referralModel;
    }

    public void setReferralModel(String referralModel) {
        this.referralModel = referralModel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
