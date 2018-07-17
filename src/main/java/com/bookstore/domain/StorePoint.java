package com.bookstore.domain;

import lombok.ToString;

import java.util.List;

import javax.persistence.*;


/**
 * @author : Abhinav Singh
 *
 * This class has been written to have wallet for the user.
 */

@Entity
@ToString
public class StorePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long points;

	@OneToOne
	private User user;

	@OneToOne(fetch = FetchType.EAGER)
	private Order order;

	private Double convertedAmount;
	private boolean referralBonus;
	private Long referralBonusPoint;

    public StorePoint() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(Double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public boolean isReferralBonus() {
        return referralBonus;
    }

    public void setReferralBonus(boolean referralBonus) {
        this.referralBonus = referralBonus;
    }

    public Long getReferralBonusPoint() {
        return referralBonusPoint;
    }

    public void setReferralBonusPoint(Long referralBonusPoint) {
        this.referralBonusPoint = referralBonusPoint;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */

}
