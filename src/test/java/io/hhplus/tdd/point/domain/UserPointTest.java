package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserPointTest {

    /**
     * 제공해 주신 템플릿에서 UserPoint는 Domain의 entity라고 생각을 하고
     * 포인트 충전/사용 관련 비즈니스로직을 설계했습니다.
     * 그에 따라 제가 설계한 로직들이 설계한대로 정상 동작하는지에 대한 단위테스트를 작성했습니다.
     */

    @DisplayName("유저가 1000 포인트를 가진 상태에서 1000 포인트를 충전하면 2000 포인트가 된다.")
    @Test
    void chargeAmountTest() {
        //given
        long id = 1L;
        long amount = 1000L;
        UserPoint userPoint = createUserPoint(id, amount);

        //when
        UserPoint addUserPoint = userPoint.chargeAmount(1000L);

        //then
        assertEquals(2000L, addUserPoint.point());
    }

    @DisplayName("유저가 충전할 수 있는 최대 포인트 10000000를 가진 상태에서 포인트를 충전하면 예외가 발생한다.")
    @Test
    void chargeAmountLimitTest() {
        //given
        long id = 1L;
        long amount = 10000000L;
        UserPoint userPoint = createUserPoint(id, amount);

        //when, then
        assertThatThrownBy(() -> userPoint.chargeAmount(1000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 충전 가능 포인트인 10000000를 초과하였습니다.");
    }

    @DisplayName("유저가 1000 포인트를 가진 상태에서 500 포인트를 사용하면 500 포인트가 된다.")
    @Test
    void useAmountTest() {
        //given
        long id = 1L;
        long amount = 1000L;
        UserPoint userPoint = createUserPoint(id, amount);

        //when
        UserPoint addUserPoint = userPoint.useAmount(500L);

        //then
        assertEquals(500L, addUserPoint.point());
    }

    @DisplayName("유저가 1000 포인트를 가진 상태에서 가지고 있는 포인트 보다 많은 1500 포인트를 사용하려 하면 예외가 발상핸다.")
    @Test
    void haveAmountMoreThanUseAmountTest() {
        //given
        long id = 1L;
        long amount = 1000L;
        UserPoint userPoint = createUserPoint(id, amount);

        //when, then
        assertThatThrownBy(() -> userPoint.useAmount(1500L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔여 포인트가 부족합니다.");
    }

    @DisplayName("가지고 있는 포인트보다 많은 포인트를 전달 받은 경우 true를 반환한다.")
    @Test
    void isAmountLessThanTest() {
        //given
        long id = 1L;
        long amount = 1000L;
        UserPoint userPoint = createUserPoint(id, amount);

        //when
        boolean isAmountLessThan = userPoint.isAmountLessThan(1500L);

        //then
        assertTrue(isAmountLessThan);
    }

    private UserPoint createUserPoint(long id, long amount) {
        return new UserPoint(id, amount, System.currentTimeMillis());
    }

}