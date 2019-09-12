package com.lyh.route.collapser.pool;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


import com.lyh.common.exception.MyPushException;
import com.lyh.route.collapser.BufferBus;

import cn.hutool.core.collection.CollectionUtil;
/**
 * bus池管理
 * @author liyanhui
 *
 */
public class BufferBusPoolManager {

	private List<BufferBus> busList = new CopyOnWriteArrayList<BufferBus>();
	private BufferBusPoolConfig config;

	public BufferBusPoolManager(BufferBusPoolConfig config){
		this.setConfig(config);
		for(int i=0;i<config.getDefaultTotal();i++){
			add(new BufferBus(config.getBufferOperation(), config.getSize(), config.getPreiod(), config.getDestIp(), config.getDestPort()));
		}
	}
	
	public void add(BufferBus bus) {
		busList.add(bus);
	}

	public BufferBus get(int target) throws MyPushException {
		if(CollectionUtil.isEmpty(busList)){
			throw new MyPushException("BufferBusPool is empty");
		}
		for (int i = 0; i < busList.size(); i++) {
			BufferBus item = busList.get(i);
			if(target<=item.getLeft()){
				return item;
			}
		}
		if(busList.size()<config.getMaxTotal()){
			//新增
			BufferBus newBus = new BufferBus(config.getBufferOperation(), config.getSize(), config.getPreiod(), config.getDestIp(), config.getDestPort());
			add(newBus);
			return newBus;
		}
		return busList.get(new Random().nextInt(busList.size()));
	}
	
	public void remove(BufferBus bus) throws MyPushException {
		if(CollectionUtil.isEmpty(busList)){
			throw new MyPushException("BufferBusPool is empty");
		}
		for (int i = 0; i < busList.size(); i++) {
			BufferBus item = busList.get(i);
			if(item==bus){
				busList.remove(item);
			}
		}
	}

	public BufferBusPoolConfig getConfig() {
		return config;
	}

	public void setConfig(BufferBusPoolConfig config) {
		this.config = config;
	}
}
