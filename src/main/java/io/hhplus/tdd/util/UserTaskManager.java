package io.hhplus.tdd.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class UserTaskManager {
    private final Map<Long, UserTaskQueue> userTaskQueues = new ConcurrentHashMap<>();

    public Future<?> addTask(long userId, Runnable task) {
        return userTaskQueues.computeIfAbsent(userId, k -> new UserTaskQueue()).submit(task);
    }

    class UserTaskQueue {
        private final ThreadPoolExecutor threadPoolExecutor;

        public UserTaskQueue() {
            BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
            this.threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, taskQueue);
            /**
             * ThreadPoolExecutor parameter
             *
             * corePoolSize - 기본으로 풀에 생성되는 스레드 수
             * maximumPoolSize - 풀에 허용되는 최대 스레드 수
             * keepAliveTime - corePoolSize 를 초과하여 생성된 쓰레드가 작업을 대기할 시간
             * unit - keepAliveTime의 시간단위
             * workQueue - 모든 쓰레드가 작업 중일때 task 를 보관할 큐
             */
        }

        public Future<?> submit(Runnable task) {
            // 작업 처리 결과 반환
            return threadPoolExecutor.submit(task);
        }
    }

}
