package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPointTableTest {

    /**
     * 처음에는 실제 회사 과제전형이라고 생각해 봤을 때 검증된 로직이니 이걸 활용하라고 템플릿을 제공하지 않았을까? 라고
     * 생각해서 테스트 코드를 작성하지 않으려고 했습니다.
     * 하지만 멘토링때 코치님께서 외부 라이브러리 같은 경우라면 굳이 테스트를 작성하지 않고 어느정도 믿고
     * 사용하는 부분도 있지만 이와 같은 경우는 실제로 우리가 제어할 수 있는 영역에 있는 로직들이다 보니 테스트를 작성한다고 하셨습니다.
     * 그래서 그말씀에 매우 공감하여 제공된 로직들에 대하여 단위테스트를 작성했습니다.
     */

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

        //when
        userPointTable.insertOrUpdate(id, amount);
        UserPoint userPoint = userPointTable.selectById(id);

        //then
        assertNotNull(userPoint);
        assertEquals(1L, userPoint.id());
        assertEquals(100L, userPoint.point());

    }

    @DisplayName("UserPointTable에 id 값이 존재하지 않는 경우 point는 기본 값인 0이다.")
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