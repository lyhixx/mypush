package com.lyh.route.collapser;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.lyh.route.BufferSendHttpRequest;
/**
 * 每个对象都包含一个看门狗的线程，资源消耗比较大
 * 看门狗刚开始的时候拿不到锁，因为countLatch初始是1，所以看门狗会阻塞等待buf里写新数据，这时看门狗被唤醒，唤醒之后看门狗获取lock
 * 然后调度自己阻塞100ms，如果没有被中断，则执行flush，如果被interrupt中断了，说明在线程阻塞的100ms中，buf已经满足了fire的条件，且已经flush了
 * 所以如果被中断，则continue，不再执行flush
 * @author liyanhui
 *
 */
public class BufferRequest {

	protected byte buf[];
	protected int count;
	
	private BufferSendHttpRequest binarySend;
	
	protected int preiod;//ms
	protected Thread thread;
	
	private final SyncWatch sync = new SyncWatch();
    private final AtomicInteger countLatch = new AtomicInteger(1);
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
	
	public BufferRequest(BufferSendHttpRequest binarySend){
		this(binarySend, 8192, 20);
	}

	public BufferRequest(BufferSendHttpRequest binarySend, int size, int preiod){
		if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
		this.buf = new byte[size];
		this.binarySend = binarySend;
		this.preiod = preiod;
		thread = new Thread(new watchdog());
		thread.start();
	}
	
	class SyncWatch extends AbstractQueuedSynchronizer{
		 private static final long serialVersionUID = 1L;
		@Override
		protected int tryAcquireShared(int arg) {
			long newCount = countLatch.incrementAndGet();
            if (newCount > 1) {
                // Limit exceeded
            	countLatch.decrementAndGet();
                return -1;
            } else {
                return 1;
            }
		}

		@Override
		protected boolean tryReleaseShared(int arg) {
			countLatch.decrementAndGet();
			return true;
		}

	}
	
	private void countDown(){
		sync.releaseShared(0);
	}
	
	private void countUpOrWait() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}
	
	class watchdog implements Runnable{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		public void run() {
			while(true){
				try {
					System.out.println("watchdog 开始执行");
					countUpOrWait();//唤醒之后过等100ms在执行flushBuffer()
					System.out.println(sdf.format(new Date())+" 唤醒之后过等100ms在执行flushBuffer()");
					try {
						lock.lock();
						cond.await(preiod, TimeUnit.MILLISECONDS);
						//自然醒执行flushBuffer()，被叫醒执行continue
						System.out.println(sdf.format(new Date())+" lock 自然醒");
						flushBuffer(false);
					} catch (IOException e) {
						e.printStackTrace();
					} catch(InterruptedException e) {
//						e.printStackTrace();
						System.out.println("lock 被叫醒中断");
						continue;
					} finally {
						lock.unlock();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.out.println("countlatch 被叫醒中断");
				}
			}
		}
		
	}
	
	public void write(byte b[], int off, int len) throws IOException {
    	synchronized (buf) {
    		if (len >= buf.length) {
                flushBuffer(true);
                binarySend.send(b, off, len);
                return;
            }
            if (len > buf.length - count) {
                flushBuffer(true);
            }
            System.arraycopy(b, off, buf, count, len);
            if(count==0 && len>0){
            	System.out.println("写入新数据之前count==0，watchdog被唤醒");
            	countDown();
    		}
            count += len;
		}
    }
	
	private void flushBuffer(boolean isInterrupt) throws IOException {
		synchronized (buf){
			System.out.println(Thread.currentThread().getName()+", flushBuffer, count="+count+",buf="+buf.toString());
			if (count > 0) {
	        	binarySend.send(buf, 0, count);
	            count = 0;
	        }
			if(isInterrupt)thread.interrupt();
		}
    }
	
}
