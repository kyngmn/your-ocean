package com.myocean.domain.ug.enums;

/**
 * UG 게임에서 사용하는 금액 크기 구분
 * - 소액: 1만원 ~ 10만원
 * - 고액: 1억원 ~ 10억원
 */
public enum MoneySize {
    /**
     * 소액: 10,000원 ~ 100,000원
     */
    SMALL(10_000, 100_000),

    /**
     * 고액: 100,000,000원 ~ 1,000,000,000원
     */
    LARGE(100_000_000, 1_000_000_000);

    private final int minAmount;
    private final int maxAmount;

    MoneySize(int minAmount, int maxAmount) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }
}