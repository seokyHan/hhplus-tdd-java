package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.hhplus.tdd.point.domain.type.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.type.TransactionType.USE;

@Service
@RequiredArgsConstructor
public class PointManageService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> getPointHistoriesByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint chargePoint(long userId, long amount) {
        UserPoint userPoint = userPointTable.selectById(userId);
        UserPoint chargeUserPoint = userPoint.chargeAmount(amount);
        pointHistoryTable.insert(chargeUserPoint.id(), amount, CHARGE, chargeUserPoint.updateMillis());

        return userPointTable.insertOrUpdate(chargeUserPoint.id(), chargeUserPoint.point());
    }

    public UserPoint usePoint(long userId, long amount) {
        UserPoint userPoint = userPointTable.selectById(userId);
        UserPoint useUserPoint = userPoint.useAmount(amount);
        pointHistoryTable.insert(useUserPoint.id(), amount, USE, useUserPoint.updateMillis());

        return userPointTable.insertOrUpdate(useUserPoint.id(), useUserPoint.point());
    }
}
