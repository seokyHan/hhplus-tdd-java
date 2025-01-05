package io.hhplus.tdd.point.domain;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    private static final long MAX_CHARGE_POINT = 10000000L;

    /**
     * 포인트 충전
     * @Param amount -> 충전할 포인트
     */
    public UserPoint chargeAmount(long amount) {
        long addPoint = this.point + amount;
        if(addPoint > MAX_CHARGE_POINT) {
            throw new IllegalArgumentException("최대 충전 가능 포인트인 " + MAX_CHARGE_POINT + "를 초과하였습니다.");
        }
        return new UserPoint(id, addPoint, updateMillis);
    }

    /**
     * 포인트 사용
     * @Param amount -> 사용할 포인트
     */
    public UserPoint useAmount(long amount) {
        if(isAmountLessThan(amount)) {
            throw new IllegalArgumentException("잔여 포인트가 부족합니다.");
        }
        long minusPoint = this.point - amount;
        return new UserPoint(id, minusPoint, updateMillis);
    }

    /**
     * 사용할 포인트가 잔여 포인트보다 적은지 확인
     * @Param amount -> 사용할 포인트
     */
    public boolean isAmountLessThan(long amount) {
        return this.point < amount;
    }

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

}
