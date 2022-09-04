package prj.coreyduffy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MultithreadingChallenge {
    static void problem1() throws InterruptedException {
        final int threads = 10;
        final int incrementsPerThread = 1_000_000;
        final int expected = threads * incrementsPerThread;
        final CountDownLatch cdl = new CountDownLatch(threads);
        final AtomicInteger x = new AtomicInteger();
        for (int i = 0; i < threads; i++) {
            (new Thread(
                    () -> {
                        synchronized (x) {
                            try {
                                for (int i1 = 0; i1 < incrementsPerThread; i1++) {
                                    x.getAndIncrement();
                                }
                            } finally {
                                cdl.countDown();
                            }
                        }
                    }))
                    .start();
        }
        cdl.await();
        assert expected == x.get();
    }

    static void problem2() throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(2);
        final LockOrdering lo = new LockOrdering();
        (new Thread(
                () -> {
                    try {
                        lo.opA();
                    } catch (final InterruptedException e) {
                    } finally {
                        cdl.countDown();
                    }
                }))
                .start();
        (new Thread(
                () -> {
                    try {
                        lo.opB();
                    } catch (final InterruptedException e) {
                    } finally {
                        cdl.countDown();
                    }
                }))
                .start();
        cdl.await();
    }

    public static void main(final String[] args) throws InterruptedException {
        problem1();
        problem2();
    }

    static final class LockOrdering {
        private final ReentrantLock a;
        private final ReentrantLock b;

        LockOrdering() {
            a = new ReentrantLock();
            b = new ReentrantLock();
        }

        void opA() throws InterruptedException {
            try {
                a.lock();
                Thread.sleep(5_000);
                b.lock();
                assert a.isHeldByCurrentThread() && b.isHeldByCurrentThread();
            } finally {
                a.unlock();
                b.unlock();
            }
        }

        void opB() throws InterruptedException {
            try {
                a.lock();
                Thread.sleep(5_000);
                b.lock();
                assert a.isHeldByCurrentThread() && b.isHeldByCurrentThread();
            } finally {
                a.unlock();
                b.unlock();
            }
        }
    }
}
