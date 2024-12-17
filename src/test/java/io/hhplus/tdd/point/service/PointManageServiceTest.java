package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.hhplus.tdd.point.domain.type.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.type.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointManageServiceTest {

    @Autowired
    private PointManageService pointManageService;
    @Autowired
    private UserPointTable userPointTable;
    @Autowired
    private PointHistoryTable pointHistoryTable;


    @DisplayName("유저의 id를 통해 해당 유저 포인트를 조회한다.")
    @Test
    void getUserPointTest() {
        //given
        long id = 1L;
        long amount = 1000L;
        UserPoint userPoint = userPointTable.insertOrUpdate(id, amount);

        //when
        UserPoint getUserPoint = pointManageService.getUserPoint(userPoint.id());

        //then
        assertNotNull(getUserPoint);
        assertEquals(id, getUserPoint.id());
        assertEquals(amount, getUserPoint.point());

    }

    @DisplayName("유저 id를 통해 해당 유저의 포인트 충전/이용 내역을 조회한다.")
    @Test
    void getPointHistoriesByUserIdTest() {
        //given
        long id = 1L;
        pointHistoryTable.insert(id, 2000L, CHARGE, System.currentTimeMillis());
        pointHistoryTable.insert(id, 1000L, USE, System.currentTimeMillis());

        //when
        List<PointHistory> pointHistories = pointManageService.getPointHistoriesByUserId(id);

        //then
        assertThat(pointHistories).hasSize(2)
                .extracting("userId", "amount", "type")
                .containsExactlyInAnyOrder(
                        tuple(1L, 2000L, CHARGE),
                        tuple(1L, 1000L, USE)
                );

    }

    @DisplayName("유저가 포인트를 충전하면 기존 금액에 충전 금액이 더해지고, 유저의 포인트 충전/이용 내역에 저장된다.")
    @Test
    void chargePointTest() {
        //given
        long id = 2L;
        long amount = 1000L;
        long chargeAmount = 500L;
        userPointTable.insertOrUpdate(id, amount);

        //when
        UserPoint userPoint = pointManageService.chargePoint(id, chargeAmount);

        //then
        assertEquals(id, userPoint.id());
        assertEquals((amount + chargeAmount), userPoint.point());

        assertThat(pointManageService.getPointHistoriesByUserId(id)).hasSize(1)
                .extracting("userId", "amount", "type")
                .containsExactlyInAnyOrder(
                        tuple(2L, 500L, CHARGE)
                );


    }


    @DisplayName("유저가 포인트를 사용하면 기존 금액에서 사용한 금액만큼 차감되고, 유저의 포인트 충전/이용 내역에 저장된다.")
    @Test
    void usePointTest() {
        //given
        long id = 3L;
        long amount = 2000L;
        long useAmount = 1500L;
        userPointTable.insertOrUpdate(id, amount);

        //when
        UserPoint userPoint = pointManageService.usePoint(id, useAmount);

        //then
        assertEquals(id, userPoint.id());
        assertEquals((amount - useAmount), userPoint.point());

        assertThat(pointManageService.getPointHistoriesByUserId(id)).hasSize(1)
                .extracting("userId", "amount", "type")
                .containsExactlyInAnyOrder(
                        tuple(3L, 1500L, USE)
                );


    }
}