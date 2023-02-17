package com.learndemo.botrunningsystem.service.impl.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BotRunningSystem是一个消费者模型，该线程等待需要运行的Bot代码的到来
 * 如果bots队列为空，就要阻塞该线程，当有需要运行的Bot代码消息时，应该发
 * 一个信号量唤醒该线程。
 */

public class BotPool extends Thread {

    private final static ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();     // 手动实现一个消息队列

    public void addBot(Integer userId, String botCode, String input) {
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot) {
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot);   // 最多执行两秒
    }

    @Override
    public void run() {
        while(true) {
            lock.lock();
            if(bots.isEmpty()) {        // 如果bots是空的
                try {
                    condition.await();      // 阻塞该线程，默认包含一个lock.unlock的操作
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot);
            }
        }
    }
}
