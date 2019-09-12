package com.lyh.gate;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.axdoctor.tools.redis.RedisUtil;
import com.lyh.common.cache.ServerNode;
import com.lyh.common.cache.redis.ServerNodeCache;
import com.lyh.common.cache.redis.UserTokenCache;
import com.lyh.common.cache.redis.UserTokenCache.LoginInfo;
import com.lyh.common.exception.MyPushException;
import com.lyh.common.packet.ChatSinglePacket;
import com.lyh.common.utils.SnowflakeIdWorker;
import com.lyh.gate.dao.MpUserDao;
import com.lyh.gate.hystrix.RegisterCommand;
import com.lyh.gate.hystrix.SendChatMsgCommand;
import com.lyh.gate.model.MpUser;

import cn.hutool.json.JSONObject;

@RestController
@RequestMapping("api/user")
public class BizController {

	@Autowired
	MpUserDao mpUserDao;
	@Autowired
	private ServerNodeCache serverNodeCache;
	@Autowired
	private UserTokenCache userTokenCache;
	@Autowired
	private GatewayService gatewayService;
	
	@Autowired
	RedisUtil redisUtil;
	@Autowired
	private RedissonClient redissonClient;
	
	@RequestMapping("register")
	public @ResponseBody String register(HttpServletRequest request) {
		String id = ServletRequestUtils.getStringParameter(request, "id", "");
		JSONObject res = new JSONObject();
		res.put("resCode", 200);
		res.put("resDesc", "成功");
		RegisterCommand command = new RegisterCommand("RegisterGroup", Integer.valueOf(id));
		command.setService(gatewayService);
		if(command.execute()){
			return res.toString();
		}
		res.put("resCode", 400);
		res.put("resDesc", "注册失败");
		return res.toString();
	}

	@RequestMapping("login")
	public @ResponseBody String login(HttpServletRequest request) throws Exception {
		int mid = ServletRequestUtils.getIntParameter(request, "mid", -1);
		String protocol = ServletRequestUtils.getStringParameter(request, "protocol", "");
		long begin = System.currentTimeMillis();
		JSONObject res = new JSONObject();
		res.put("resCode", 200);
		res.put("resDesc", "成功");
		if (mid == -1) {
			res.put("resCode", 403);
			res.put("resDesc", "缺少参数mid");
			return res.toString();
		}
		if (StringUtils.isEmpty(protocol)) {
			res.put("resCode", 403);
			res.put("resDesc", "缺少参数protocol");
			return res.toString();
		}
		// 获取可用imserver服务器节点
		ServerNode sn = serverNodeCache.getRandomNode(protocol);
		if (sn == null) {
			// 无可以长连的imserver服务器节点
			res.put("resCode", 401);
			res.put("resDesc", "无可用服务器节点");
			System.out.println(System.currentTimeMillis()-begin);
			return res.toString();
		}
		LoginInfo loginInfo = new UserTokenCache.LoginInfo(mid, protocol);
		if (userTokenCache.isLogined(loginInfo)) {
			// 登录中,查找之前的token
			res.put("token", userTokenCache.getToken(loginInfo.getMid(), loginInfo.getProtocol()));
			ServerNode serverNode = userTokenCache.getConnectServerInfo(loginInfo);
			res.put("server", sn);
			if (serverNode != null) {
				// 判断serverNode是否真的存在
				if (serverNodeCache.isServerNodeExsit(serverNode.hashCode())) {
					// 存在
					res.put("server", serverNode);
				}
			}
			return res.toString();
		}
		// TODO 数据库查询，避免大量登录并发，活跃用户可以提前预热初始化
		MpUser mpUser = mpUserDao.getBeanById(mid);
		if (mpUser == null) {
			// 登录失败
			res.put("resCode", 400);
			res.put("resDesc", "登录失败");
			return res.toString();
		}
		// 保存登录状态
		res.put("token", userTokenCache.saveOrUpdateToken(loginInfo));
		res.put("server", sn);
		return res.toString();
	}

	/**
	 * 限流-熔断 超时设置的流量最大值时，进行熔断处理，降级mock返回值
	 * 当整体系统内部的资源开始消耗比较大，如redis内存吃紧，mq积压达上限，说明系统已经处理不过来了，通知网关限流
	 * 
	 * @param request
	 * @return
	 * @author liyanhui
	 * @date 2019年7月13日
	 */
	@RequestMapping("sendChatMsg")
	public @ResponseBody String sendChatMsg(HttpServletRequest request) {
		String fromMid = ServletRequestUtils.getStringParameter(request, "fromMid", "");
		String toMid = ServletRequestUtils.getStringParameter(request, "toMid", "");
		String token = ServletRequestUtils.getStringParameter(request, "token", "");
		String content = ServletRequestUtils.getStringParameter(request, "content", "");
		String time = ServletRequestUtils.getStringParameter(request, "time", "");
		String protocol = ServletRequestUtils.getStringParameter(request, "protocol", "");
		ChatSinglePacket chat = new ChatSinglePacket();
		chat.setContent(content);
		chat.setFromMid(fromMid);
		chat.setProtocol(protocol);
		chat.setTime(Long.valueOf(time));
		chat.setToken(token);
		chat.setToMid(toMid);
		chat.setChatId(SnowflakeIdWorker.getInstance().nextId());
		JSONObject res = new JSONObject();
		try {
			//校验是否登录
	        LoginInfo info = new LoginInfo();
	        info.setMid(Integer.valueOf(chat.getFromMid()));
	        info.setProtocol(chat.getProtocol());
	        info.setToken(chat.getToken());
	        gatewayService.isLogin(info);
	        //发消息
			SendChatMsgCommand command = new SendChatMsgCommand("ChatGroup", chat);
			command.setService(gatewayService);
			command.execute();
			res.put("resCode", 200);
			res.put("resDesc", "成功");
			return res.toString();
		} catch (MyPushException e) {
			e.printStackTrace();
			res.put("resCode", e.getCode());
			res.put("resDesc", e.getMessage());
			return res.toString();
		} catch(Exception e) {
			e.printStackTrace();
			res.put("resCode", MyPushException.ErrorCode.SYS_ERROR.getCode());
			res.put("resDesc", MyPushException.ErrorCode.SYS_ERROR.getDes());
			return res.toString();
		}

	}
	//test
	@RequestMapping("getUserInfo")
	public @ResponseBody String getUserInfo(HttpServletRequest request) throws Exception {
		String id = ServletRequestUtils.getStringParameter(request, "id", "");
		String res = null;
		try {
			res = redisUtil.get("im:mypush:"+id);
		} catch (Exception e) {
			System.out.println("################cache = "+res);
			return "mock";
		}
		if(StringUtils.isNotBlank(res)){
//			System.out.println("cache = "+res);
			return res;
		}
		RCountDownLatch latch = redissonClient.getCountDownLatch("im;mypush;countdownlatch");
		if(latch.getCount()==0)latch.trySetCount(1);
		System.out.println(Thread.currentThread().getName()+" 开始睡100ms");
		latch.await(1000, TimeUnit.MILLISECONDS);
		
		res = redisUtil.get("im:mypush:"+id);
		if(StringUtils.isNotBlank(res)){
			return res;
		}
		System.out.println(Thread.currentThread().getName()+" 醒了查数据库");
		MpUser mpUser = mpUserDao.getBeanById(Integer.valueOf(id));
		System.out.println(Thread.currentThread().getName()+" 查到结果："+mpUser.getBizUid());
		redisUtil.set("im:mypush:"+id, mpUser.getBizUid());
		return mpUser.getBizUid();
	}
	
	@RequestMapping("updateUserInfo")
	public @ResponseBody String updateUserInfo(HttpServletRequest request) throws Exception {
		RCountDownLatch latch = redissonClient.getCountDownLatch("im;mypush;countdownlatch");
		String id = ServletRequestUtils.getStringParameter(request, "id", "");
		try{
			if(redisUtil.del("im:mypush:"+id)){
				//删除成功
				System.out.println("删除成功，id="+id);
			}
			mpUserDao.update(Integer.valueOf(id), "1234");
			redisUtil.set("im:mypush:"+id, "1234");
			System.out.println("修改成功，改为1234");
		}finally {
			latch.countDown();
		}
		return "success";
	}
}
