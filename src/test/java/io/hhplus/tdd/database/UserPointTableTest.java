package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPointTableTest {

    private UserPointTable userPointTable;

    @BeforeEach
    public void setUp() {
        userPointTable = new UserPointTable();
    }

    @DisplayName("UserPointTable에 id와 amount를 입력 받아 저장 후 앞서 저장된 UserPoint를 반환한다.")
    @Test
    public void insertOrUpdateTest() {
        long id = 1L;
        long amount = 100L;

        UserPoint userPoint = userPointTable.insertOrUpdate(id, amount);

        assertNotNull(userPoint);
        assertEquals(id, userPoint.id());
        assertEquals(amount, userPoint.point());
    }


    @DisplayName("UserPointTable에 유저 id가 존재하는 경우 해당 id의 UserPoint를 반환한다.")
    @Test
    void userPointTableSelectByIdTest() {
        //given
        long id = 1L;
        long amount = 100L;

        //when-
        userPointTable.insertOrUpdate(id, amount);
        UserPoint userPoint = userPointTable.selectById(id);

        //then
        assertNotNull(userPoint);
        assertEquals(1L, userPoint.id());
        assertEquals(100L, userPoint.point());

    }

    @DisplayName("UserPointTable에 id 값이 존재하지 않는 경우 point는 0이다.")
    @Test
    void selectByIdWhenNotExistsTest() {
        //given
        long id = 99L;

        //when
        UserPoint userPoint = userPointTable.selectById(id);

        //then
        assertNotNull(userPoint);
        assertEquals(id, userPoint.id());
        assertEquals(0L, userPoint.point());

    }

}