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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static io.hhplus.tdd.point.domain.type.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.type.TransactionType.USE;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointManageServiceTest {


    /**
     * 개별 단위테스트들이 정상 동작 하면 굳이 통합테스트는 작성하지 않아도 되지 않나? 할 수 있지만
     * 검증된 단위 테스트들이 시스템 내에서 여러 의존성들이 있는 상황에서도 정상 동작하는지 확인하기 위해
     * 통합테스트를 작성했습니다.
     *
     */

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
    void chargePointTest() throws InterruptedException{
        //given
        long id = 99L;
        long amount = 100L;
        int threadCount = 10;
        ExecutorService executorService = newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //when
        for(int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointManageService.chargePoint(id, amount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        UserPoint userPoint = pointManageService.getUserPoint(id);

        //then
        assertEquals(1000L, userPoint.point());
        assertThat(pointManageService.getPointHistoriesByUserId(id)).hasSize(10)
                .extracting("userId", "amount", "type")
                .containsExactlyInAnyOrder(
                        tuple(99L, 100L, CHARGE), tuple(99L, 100L, CHARGE), tuple(99L, 100L, CHARGE),
                        tuple(99L, 100L, CHARGE), tuple(99L, 100L, CHARGE), tuple(99L, 100L, CHARGE),
                        tuple(99L, 100L, CHARGE), tuple(99L, 100L, CHARGE), tuple(99L, 100L, CHARGE),
                        tuple(99L, 100L, CHARGE)
                );


    }


    @DisplayName("유저가 포인트를 사용하면 기존 금액에서 사용한 금액만큼 차감되고, 유저의 포인트 충전/이용 내역에 저장된다.")
    @Test
    void usePointTest() throws InterruptedException{
        //given
        long id = 3L;
        long amount = 1500L;
        long useAmount = 100L;
        int threadCount = 10;
        userPointTable.insertOrUpdate(id, amount);

        ExecutorService executorService = newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        //when
        for(int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    pointManageService.usePoint(id, useAmount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        UserPoint userPoint = pointManageService.getUserPoint(id);

        //then
        assertEquals(500L, userPoint.point());
        assertThat(pointManageService.getPointHistoriesByUserId(id)).hasSize(10)
                .extracting("userId", "amount", "type")
                .containsExactlyInAnyOrder(
                        tuple(3L, 100L, USE), tuple(3L, 100L, USE), tuple(3L, 100L, USE),
                        tuple(3L, 100L, USE), tuple(3L, 100L, USE), tuple(3L, 100L, USE),
                        tuple(3L, 100L, USE), tuple(3L, 100L, USE), tuple(3L, 100L, USE),
                        tuple(3L, 100L, USE)
                );


    }
}