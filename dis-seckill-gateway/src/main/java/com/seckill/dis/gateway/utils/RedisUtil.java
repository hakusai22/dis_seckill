package com.seckill.dis.gateway.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.util.JedisClusterCRC16;

/**
 * Copyright (c) 2023
 * All rights reserved
 * Author: hakusai22@qq.com
 */

@Slf4j
@Component("redis")
public class RedisUtil extends AbstractDistributedLock {

  private final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

  @Autowired
  private RedisTemplate redisTemplate;

  //=============================common============================

  /**
   * 指定缓存失效时间
   *
   * @param key  键
   * @param time 时间(秒)
   * @return
   */
  public boolean expire(String key, long time) {
    try {
      if (time > 0) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
      }
      return true;
    } catch (Exception e) {
      log.error("指定缓存失效时间:{}", e);
      return false;
    }
  }

  /**
   * 根据key 获取过期时间
   *
   * @param key 键 不能为null
   * @return 时间(秒) 返回0代表为永久有效
   */
  public long getExpire(String key) {
    return redisTemplate.getExpire(key, TimeUnit.SECONDS);
  }

  /**
   * 判断key是否存在
   *
   * @param key 键
   * @return true 存在 false不存在
   */
  public boolean exists(String key) {
    try {
      return redisTemplate.hasKey(key);
    } catch (Exception e) {
      log.error("判断key是否存在:{}", e);
      return false;
    }
  }

  /**
   * 删除缓存
   *
   * @param key 可以传一个值 或多个
   */
  @SuppressWarnings("unchecked")
  public void del(String... key) {
    if (key != null && key.length > 0) {
      if (key.length == 1) {
        redisTemplate.delete(key[0]);
      } else {
        redisTemplate.delete(CollectionUtils.arrayToList(key));
      }
    }
  }

  //============================String=============================

  /**
   * 读取缓存
   *
   * @param key
   * @return
   */
  public Object get(final String key) {
    Object result = null;
    ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
    result = operations.get(key);
    return result;
  }

  /**
   * 写入缓存
   *
   * @param key
   * @param value
   * @return
   */
  public boolean set(final String key, Object value) {
    boolean result = false;
    try {
      ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
      operations.set(key, value);
      result = true;
    } catch (Exception e) {
      log.error("写入缓存:{}", e);
    }
    return result;
  }

  /**
   * 写入缓存
   *
   * @param key
   * @param value
   * @return
   */
  public boolean set(final String key, Object value, Long expireTime) {
    boolean result = false;
    try {
      ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
      operations.set(key, value);
      if (expireTime > 0) {
        expire(key, expireTime);
      }
      result = true;
    } catch (Exception e) {
      log.error("写入缓存:{}", e);
    }
    return result;
  }

  public double incr(String key, long by) {
    return redisTemplate.opsForValue().increment(key, by);
  }

  //============================Hash=============================

  /**
   * HashGet
   *
   * @param key  键 不能为null
   * @param item 项 不能为null
   * @return 值
   */
  public Object hget(String key, String item) {
    return redisTemplate.opsForHash().get(key, item);
  }

  /**
   * 获取hashKey对应的所有键值
   *
   * @param key 键
   * @return 对应的多个键值
   */
  public Map<String, Object> hmget(String key) {
    return redisTemplate.opsForHash().entries(key);
  }

  /**
   * 获取hash中的 ertry set
   *
   * @param key
   * @return
   */
  public List<Object> hvalues(String key) {
    return redisTemplate.opsForHash().values(key);
  }

  /**
   * HashSet
   *
   * @param key 键
   * @param map 对应多个键值
   * @return true 成功 false 失败
   */
  public boolean hmset(String key, Map<String, Object> map) {
    try {
      redisTemplate.opsForHash().putAll(key, map);
      return true;
    } catch (Exception e) {
      log.error("HashSet:{}", e);
      return false;
    }
  }

  /**
   * HashSet 并设置时间
   *
   * @param key  键
   * @param map  对应多个键值
   * @param time 时间(秒)
   * @return true成功 false失败
   */
  public boolean hmset(String key, Map<String, Object> map, long time) {
    try {
      redisTemplate.opsForHash().putAll(key, map);
      if (time > 0) {
        expire(key, time);
      }
      return true;
    } catch (Exception e) {
      log.error("HashSet 并设置时间:{}", e);
      return false;
    }
  }

  /**
   * 向一张hash表中放入数据,如果不存在将创建
   *
   * @param key   键
   * @param item  项
   * @param value 值
   * @return true 成功 false失败
   */
  public boolean hset(String key, String item, Object value) {
    try {
      redisTemplate.opsForHash().put(key, item, value);
      return true;
    } catch (Exception e) {
      log.error("向一张hash表中放入数据,如果不存在将创建:{}", e);
      return false;
    }
  }

  /**
   * 向一张hash表中放入数据,如果不存在将创建
   *
   * @param key   键
   * @param item  项
   * @param value 值
   * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
   * @return true 成功 false失败
   */
  public boolean hset(String key, String item, Object value, long time) {
    try {
      redisTemplate.opsForHash().put(key, item, value);
      if (time > 0) {
        expire(key, time);
      }
      return true;
    } catch (Exception e) {
      log.error("向一张hash表中放入数据,如果不存在将创建:{}", e);
      return false;
    }
  }

  /**
   * 删除hash表中的值
   *
   * @param key  键 不能为null
   * @param item 项 可以使多个 不能为null
   */
  public void hdel(String key, Object... item) {
    redisTemplate.opsForHash().delete(key, item);
  }

  /**
   * 判断hash表中是否有该项的值
   *
   * @param key  键 不能为null
   * @param item 项 不能为null
   * @return true 存在 false不存在
   */
  public boolean hHasKey(String key, String item) {
    return redisTemplate.opsForHash().hasKey(key, item);
  }

  /**
   * hash递增 如果不存在,就会创建一个 并把新增后的值返回
   *
   * @param key  键
   * @param item 项
   * @param by   要增加几(大于0)
   * @return
   */
  public double hincr(String key, String item, double by) {
    return redisTemplate.opsForHash().increment(key, item, by);
  }

  /**
   * hash递减
   *
   * @param key  键
   * @param item 项
   * @param by   要减少记(小于0)
   * @return
   */
  public double hdecr(String key, String item, double by) {
    return redisTemplate.opsForHash().increment(key, item, -by);
  }

  //============================Set==============================

  /**
   * 根据key获取Set中的所有值
   *
   * @param key 键
   * @return
   */
  public Set<Object> sget(String key) {
    try {
      return redisTemplate.opsForSet().members(key);
    } catch (Exception e) {
      log.error("根据key获取Set中的所有值:{}", e);
      return null;
    }
  }

  /**
   * 根据value从一个set中查询,是否存在
   *
   * @param key   键
   * @param value 值
   * @return true 存在 false不存在
   */
  public boolean sHasKey(String key, Object value) {
    try {
      return redisTemplate.opsForSet().isMember(key, value);
    } catch (Exception e) {
      log.error("根据value从一个set中查询,是否存在:{}", e);
      return false;
    }
  }

  /**
   * 将数据放入set缓存
   *
   * @param key    键
   * @param values 值 可以是多个
   * @return 成功个数
   */
  public long sset(String key, Object... values) {
    try {
      return redisTemplate.opsForSet().add(key, values);
    } catch (Exception e) {
      log.error("将数据放入set缓存:{}", e);
      return 0;
    }
  }

  /**
   * 将set数据放入缓存
   *
   * @param key    键
   * @param time   时间(秒)
   * @param values 值 可以是多个
   * @return 成功个数
   */
  public long sset(String key, long time, Object... values) {
    try {
      Long count = redisTemplate.opsForSet().add(key, values);
      if (time > 0) expire(key, time);
      return count;
    } catch (Exception e) {
      log.error("将数据放入set缓存:{}", e);
      return 0;
    }
  }

  /**
   * 获取set缓存的长度
   *
   * @param key 键
   * @return
   */
  public long sgetSetSize(String key) {
    try {
      return redisTemplate.opsForSet().size(key);
    } catch (Exception e) {
      log.error("获取set缓存的长度:{}", e);
      return 0;
    }
  }

  /**
   * 移除值为value的
   *
   * @param key    键
   * @param values 值 可以是多个
   * @return 移除的个数
   */
  public long sdel(String key, Object... values) {
    try {
      Long count = redisTemplate.opsForSet().remove(key, values);
      return count;
    } catch (Exception e) {
      log.error("移除值为value的:{}", e);
      return 0;
    }
  }

  //===============================list=================================

  /**
   * 获取list缓存的内容
   *
   * @param key   键
   * @param start 开始
   * @param end   结束  0 到 -1代表所有值
   * @return
   */
  public List<Object> lget(String key, long start, long end) {
    try {
      return redisTemplate.opsForList().range(key, start, end);
    } catch (Exception e) {
      log.error("获取list缓存的内容:{}", e);
      return null;
    }
  }

  /**
   * 获取list缓存的长度
   *
   * @param key 键
   * @return
   */
  public Long lgetListSize(String key) {
    try {
      return redisTemplate.opsForList().size(key);
    } catch (Exception e) {
      log.error("获取list缓存的长度:{}", e);
      return null;
    }
  }

  /**
   * 通过索引 获取list中的值
   *
   * @param key   键
   * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
   * @return
   */
  public Object lgetIndex(String key, long index) {
    try {
      return redisTemplate.opsForList().index(key, index);
    } catch (Exception e) {
      log.error("通过索引 获取list中的值:{}", e);
      return null;
    }
  }

  /**
   * 将list放入缓存
   *
   * @param key   键
   * @param value 值
   * @return
   */
  public boolean lset(String key, Object value) {
    try {
      redisTemplate.opsForList().rightPush(key, value);
      return true;
    } catch (Exception e) {
      log.error("将list放入缓存:{}", e);
      return false;
    }
  }

  /**
   * 将list放入缓存
   *
   * @param key   键
   * @param value 值
   * @param time  时间(秒)
   * @return
   */
  public boolean lset(String key, Object value, long time) {
    try {
      redisTemplate.opsForList().rightPush(key, value);
      if (time > 0) expire(key, time);
      return true;
    } catch (Exception e) {
      log.error("将list放入缓存:{}", e);
      return false;
    }
  }

  /**
   * 将list放入缓存
   *
   * @param key   键
   * @param value 值
   * @return
   */
  public boolean lset(String key, List<Object> value) {
    try {
      redisTemplate.opsForList().rightPushAll(key, value);
      return true;
    } catch (Exception e) {
      log.error("将list放入缓存:{}", e);
      return false;
    }
  }

  /**
   * 将list放入缓存
   *
   * @param key   键
   * @param value 值
   * @param time  时间(秒)
   * @return
   */
  public boolean lset(String key, List<Object> value, long time) {
    try {
      redisTemplate.opsForList().rightPushAll(key, value);
      if (time > 0) expire(key, time);
      return true;
    } catch (Exception e) {
      log.error("将list放入缓存:{}", e);
      return false;
    }
  }

  /**
   * 根据索引修改list中的某条数据
   *
   * @param key   键
   * @param index 索引
   * @param value 值
   * @return
   */
  public boolean lUpdateIndex(String key, long index, Object value) {
    try {
      redisTemplate.opsForList().set(key, index, value);
      return true;
    } catch (Exception e) {
      log.error("根据索引修改list中的某条数据:{}", e);
      return false;
    }
  }

  /**
   * 移除N个值为value
   *
   * @param key   键
   * @param count 移除多少个
   * @param value 值 count > 0：删除等于从左到右移动的值的第一个元素；
   *              count < 0：删除等于从右到左移动的值的第一个元素；
   *              count = 0：删除等于value的所有元素
   * @return 移除的个数
   */
  public long ldel(String key, long count, Object value) {
    try {
      Long remove = redisTemplate.opsForList().remove(key, count, value);
      return remove;
    } catch (Exception e) {
      log.error("移除N个值为value:{}", e);
      return 0;
    }
  }

  /**
   * 模糊查询拼接
   * 消息补偿特殊处理使用
   *
   * @param pattern
   */
  public ArrayList matchAndSplice(String pattern) {
    ArrayList<Long> list = new ArrayList<>();
    Set keys = redisTemplate.keys(pattern + "*");
    if (keys != null && keys.size() > 0) {
      keys.forEach(k -> {
        Object o = redisTemplate.opsForValue().get(k);
        if (o != null) {
          ArrayList<Long> ids = (ArrayList) o;
          ids.forEach(i -> list.add(i));
        }
      });
    }
    return list;
  }

  /**
   * 模糊匹配删除 key* 出现线程阻塞
   *
   * @param pattern
   */
  public void matchDel(String pattern) {
    Set keys = redisTemplate.keys(pattern + "*");
    if (keys != null && keys.size() > 0) {
      keys.forEach(k -> redisTemplate.delete(k));
    }
  }

  /**
   * 模糊匹配删除
   *
   * @param matchKey 模糊key
   */
  public void matchDelByScan(String matchKey) {
    Set<String> keys = scanKeys(matchKey);
    if (keys != null && keys.size() > 0) {
      keys.forEach(k -> redisTemplate.delete(k));
    }
  }

  /**
   * 通过scan扫描redis集群所有节点,模糊匹配key
   *
   * @param matchKey 模糊key
   * @return
   */
  public Set<String> scanKeys(String matchKey) {
    Set<String> set = new HashSet<>();
    RedisClusterConnection redisClusterConnection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getClusterConnection();
    // 获取jedisPool
    Map<String, JedisPool> clusterNodes = ((JedisCluster) redisClusterConnection.getNativeConnection()).getClusterNodes();
    for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
      // 获取单个的jedis对象
      Jedis jedis = null;
      try {
        jedis = entry.getValue().getResource();
        // 判断非从节点(因为若主从复制，从节点会跟随主节点的变化而变化)，此处要使用主节点从主节点获取数据
        if (!jedis.info("replication").contains("role:slave")) {
          List<String> keys = getScan(jedis, matchKey + "*");
          if (keys.size() > 0) {
            Map<Integer, List<String>> map = new HashMap<>(8);
            //接下来的循环不是多余的，需要注意
            for (String key : keys) {
              // cluster模式执行多key操作的时候，这些key必须在同一个slot上，不然会报:JedisDataException:
              int slot = JedisClusterCRC16.getSlot(key);
              // 按slot将key分组，相同slot的key一起提交
              if (map.containsKey(slot)) {
                map.get(slot).add(key);
              } else {
                List<String> list = new ArrayList<>();
                list.add(key);
                map.put(slot, list);
              }
            }
            for (Map.Entry<Integer, List<String>> integerListEntry : map.entrySet()) {
              set.addAll(integerListEntry.getValue());
            }
          }
        }
      } catch (Exception e) {
        logger.error("模糊扫描redis失败", e);
      } finally {
        if (null != jedis) {
          jedis.close();
        }
      }
    }
    return set;
  }

  /**
   * scan获取单redis节点key*
   *
   * @param jedis    Jedis
   * @param matchKey 模糊匹配key
   * @return key结果集
   */
  private static List<String> getScan(Jedis jedis, String matchKey) {
    List<String> list = new ArrayList<>();
    //扫描的参数对象创建与封装
    ScanParams params = new ScanParams();
    params.match(matchKey);
    //扫描返回10000行
    params.count(10000);
    String scanCursorIndex = "0";
    //scan.getStringCursor() 存在 且不是 0 的时候，一直移动游标获取
    do {
      ScanResult<String> scanResult = jedis.scan(scanCursorIndex, params);
      scanCursorIndex = scanResult.getStringCursor();
      list.addAll(scanResult.getResult());
    } while (null != scanCursorIndex && !"0".equals(scanCursorIndex));
    return list;
  }


  //============================Lock=============================

  private ConcurrentHashMap lockMap = new ConcurrentHashMap();

  private static final String UNLOCK_LUA;

  static {
    String string = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
        "then " +
        "    return redis.call(\"del\",KEYS[1]) " +
        "else " +
        "    return 0 " +
        "end ";
    UNLOCK_LUA = string;
  }

  @Override
  public <T> T lock(String key, long expire, int retryTimes, long sleepMillis,
      AcquiredLockWorker<T> worker) throws Exception {
    boolean result = setRedis(key, expire);
    // 如果获取锁失败，按照传入的重试次数进行重试
    while ((!result) && retryTimes-- > 0) {
      try {
        logger.debug("lock failed, retrying..." + retryTimes);
        Thread.sleep(sleepMillis);
      } catch (InterruptedException e) {
        throw new Exception("InterruptedException :", e);
      }
      result = setRedis(key, expire);
    }
    try {
      return worker.invokeAfterLockAcquire();
    } finally {
      unLock(key);
    }
  }

  private boolean setRedis(String key, long expire) {
    try {
      String result = (String) redisTemplate.execute((RedisCallback<String>) connection -> {
        JedisCommands commands = (JedisCommands) connection.getNativeConnection();
        String uuid = UUID.randomUUID().toString();
        String end = commands.set(key, uuid, "NX", "PX", expire);
        if (!Strings.isNullOrEmpty(end)) {
          lockMap.put(key, uuid);
        }
        return end;
      });
      return !Strings.isNullOrEmpty(result);
    } catch (Exception e) {
      logger.error("set redis occur an exception", e);
    }
    return false;
  }

  private boolean unLock(String key) {
    // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
    try {
      List<String> keys = new ArrayList<>();
      keys.add(key);
      List<String> args = new ArrayList<>();
      args.add(lockMap.get(key).toString());
      lockMap.remove(key);
      // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
      // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
      Long result = (Long) redisTemplate.execute((RedisCallback<Long>) connection -> {
        Object nativeConnection = connection.getNativeConnection();
        // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
        if (nativeConnection instanceof JedisCluster) {
          return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
        } else if (nativeConnection instanceof Jedis) {
          return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
        }
        return 0L;
      });
      return result != null && result > 0;
    } catch (Exception e) {
      logger.error("release lock occur an exception", e);
    }
    return false;
  }
}