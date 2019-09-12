package com.lyh.route.collapser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.lyh.route.collapser.oper.BufferOperation;
/**
 * 所有对象只有一只看门狗
 * @author liyanhui
 *
 */
public class BufferRequest2 {

	protected byte buf[];
	protected int count;
	
	private BufferOperation oper;
	
	protected int preiod;//ms
	
    private final static Timer watchdog = new Timer(true);
    private TimerTask t;
	
	public BufferRequest2(BufferOperation oper){
		this(oper, 8192, 20);
	}

	public BufferRequest2(BufferOperation oper, int size, int preiod){
		if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
		this.buf = new byte[size];
		this.oper = oper;
		this.preiod = preiod;
	}
	
	public void write(byte b[], int off, int len) {
    	synchronized (buf) {
    		if (len >= buf.length) {
                flushBuffer(true);
                oper.asyncOper(b, off, len);
                return;
            }
            if (len > buf.length - count) {
                flushBuffer(true);
            }
            System.arraycopy(b, off, buf, count, len);
            if(count==0 && len>0){
//            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            	System.out.println(sdf.format(new Date())+"写入新数据之前count==0，给watchdog分配任务");
            	t = new TimerTask() {
					
					@Override
					public void run() {
						flushBuffer(false);
					}
				};
				watchdog.purge();
            	watchdog.schedule(t, preiod);
    		}
            count += len;
		}
    }
	
	private void flushBuffer(boolean isCancel) {
		synchronized (buf){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println(sdf.format(new Date())+" "+Thread.currentThread().getName()+", flushBuffer, count="+count+",buf="+buf.toString());
			if (count > 0) {
				oper.asyncOper(buf, 0, count);
	            count = 0;
	        }
			if(isCancel){
				System.out.println("watchdog 任务取消，t"+t.toString());
				t.cancel();
			}
		}
    }
	
	public int getLeft(){
		return buf.length - count;
	}
	
}
