package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.util.UserTaskManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.hhplus.tdd.point.domain.type.TransactionType.CHARGE;
import static io.hhplus.tdd.point.domain.type.TransactionType.USE;

@Service
@RequiredArgsConstructor
public class PointManageService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final Map<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();


    public UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> getPointHistoriesByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint chargePoint(long userId, long amount) {
        Lock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock(true));
        lock.lock();

        try{
            UserPoint chargeUserPoint = getUserPoint(userId).chargeAmount(amount);
            pointHistoryTable.insert(chargeUserPoint.id(), amount, CHARGE, chargeUserPoint.updateMillis());
            userPointTable.insertOrUpdate(chargeUserPoint.id(), chargeUserPoint.point());

            return getUserPoint(userId);
        } finally {
            lock.unlock();
        }
    }

    public UserPoint usePoint(long userId, long amount) {
        Lock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock(true));
        lock.lock();

        try{
            UserPoint useUserPoint = getUserPoint(userId).useAmount(amount);
            pointHistoryTable.insert(useUserPoint.id(), amount, USE, useUserPoint.updateMillis());
            userPointTable.insertOrUpdate(useUserPoint.id(), useUserPoint.point());

            return getUserPoint(userId);
        } finally {
            lock.unlock();
        }
    }
}
