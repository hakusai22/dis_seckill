package com.seckill.dis.gateway.init;

import com.seckill.dis.gateway.utils.RedisUtil;
import com.seckill.dis.gateway.utils.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * spring 容器加载完毕后调用
 */
@Component
@Slf4j
public class ApplicationInit implements ApplicationContextAware {

  @Autowired
  private RedisUtil redisUtil;

  @Value("${spring.application.name}")
  private String name;

  /**
   * 1.初始化SnowflakeIdGenerator
   */
  @Override
  public void setApplicationContext(
      ApplicationContext applicationContext) throws BeansException {
    String snowflakeId = "SnowflakeId";
    try {
      redisUtil.lock(snowflakeId + "-key", 90000L, () -> {
        Map<String, Object> map = redisUtil.hmget(snowflakeId);
        long workId = 0;
        if (map == null || map.size() == 0) {
          map = new HashMap<>(16);
          map.put(name + "-" + getIpAddress(), workId);
        } else {
          Object o = map.get(name + "-" + getIpAddress());
          if (o != null) {
            workId = Long.parseLong(o.toString());
          } else {
            workId = getNextWorkerId(map);
            map.put(name + "-" + getIpAddress(), workId);
          }
        }
        SnowflakeIdGenerator.init(workId);
        redisUtil.hmset(snowflakeId, map);
        return null;
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 查找workerId是否有空洞，有则填上；没有则取当前最大数+1
   *
   * @param map
   * @return
   */
  private static long getNextWorkerId(Map<String, Object> map) {
    Long nextWorkerId = -1L;
    // 记录已经存在的workerId
    Set<Long> currentWorkerIdSet = new HashSet<>(map.size());
    for (Iterator ite = map.entrySet().iterator(); ite.hasNext(); ) {
      Map.Entry entry = (Map.Entry) ite.next();
      Long value = Long.parseLong(entry.getValue().toString());
      // 记录最大的workerId
      nextWorkerId = nextWorkerId.compareTo(value) > 0 ? nextWorkerId : value;
      currentWorkerIdSet.add(value);
    }
    long temp = nextWorkerId.longValue();
    // 从最大workerId向前找，若找到空位则补上
    while (--temp >= 0) {
      if (!currentWorkerIdSet.contains(temp)) {
        return temp;
      }
    }
    // 没有空位，workerId递增
    return ++nextWorkerId;
  }

  /**
   * 本机获取ip地址
   */
  public static String getIpAddress() {
    try {
      Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
      InetAddress ip = null;
      if (Objects.isNull(allNetInterfaces)) {
        return "";
      }
      while (allNetInterfaces.hasMoreElements()) {
        NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
        if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
          continue;
        } else {
          Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
          while (addresses.hasMoreElements()) {
            ip = addresses.nextElement();
            if (ip != null && ip instanceof Inet4Address) {
              return ip.getHostAddress();
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }
}
