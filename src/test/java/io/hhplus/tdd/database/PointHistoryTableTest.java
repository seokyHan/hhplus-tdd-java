package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.PointHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.hhplus.tdd.point.domain.type.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.type.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PointHistoryTableTest {

    private PointHistoryTable pointHistoryTable;

    @BeforeEach
    public void setUp() {
        pointHistoryTable = new PointHistoryTable();
    }

    @DisplayName("유저의 포인트 충전/이용 내역에 유저 id, amount, transactionType, updateMillis를 저장한다.")
    @Test
    void insertTest() {
        //given
        //when
        PointHistory pointHistory = pointHistoryTable.insert(1L, 2000L, CHARGE, System.currentTimeMillis());

        //then
        assertNotNull(pointHistory);
        assertEquals(1L, pointHistory.id());
        assertEquals(2000L, pointHistory.amount());
        assertEquals(CHARGE, pointHistory.type());

    }

    @DisplayName("유저의 포인트 충전/이용 내역에서 유저의 id를 통해 목록들을 조회한다.")
    @Test
    void selectAllByUserIdTest() {
        //given
        pointHistoryTable.insert(1L, 2000L, CHARGE, System.currentTimeMillis());
        pointHistoryTable.insert(1L, 1000L, USE, System.currentTimeMillis());

        //when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(1L);

        //then
        assertThat(pointHistories).hasSize(2)
                .extracting("userId", "amount", "type")
                .containsExactlyInAnyOrder(
                        tuple(1L, 2000L, CHARGE),
                        tuple(1L, 1000L, USE)
                );

    }

}