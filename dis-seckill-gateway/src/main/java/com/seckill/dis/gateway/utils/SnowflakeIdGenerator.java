package com.seckill.dis.gateway.utils;

public class SnowflakeIdGenerator {
  private static final long startTime = 1532016000000L;
  private static final long workerIdBits = 10L;
  private static final long maxWorkerId = 1023L;
  private static final long sequenceBits = 12L;
  private static final long workerIdMoveBits = 12L;
  private static final long timestampMoveBits = 22L;
  private static final long sequenceMask = 4095L;
  private long workerId;
  private static long sequence = 0L;
  private static long lastTimestamp = -1L;
  private static SnowflakeIdGenerator idWorker;

  public SnowflakeIdGenerator(long workerId) {
    if (workerId <= 1023L && workerId >= 0L) {
      this.workerId = workerId;
    } else {
      throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", 1023L));
    }
  }

  public synchronized long nextId() {
    return reverse(this.nextNormalId());
  }

  public synchronized long nextNormalId() {
    long timestamp = this.currentTime();
    if (timestamp < lastTimestamp) {
      throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
    } else {
      if (lastTimestamp == timestamp) {
        sequence = sequence + 1L & 4095L;
        if (sequence == 0L) {
          timestamp = this.blockTillNextMillis(lastTimestamp);
        }
      } else {
        sequence = 0L;
      }

      lastTimestamp = timestamp;
      return timestamp - 1532016000000L << 22 | this.workerId << 12 | sequence;
    }
  }

  private static long reverse(long input) {
    long result = 0L;

    for (int i = 0; i < 63; ++i) {
      if ((input >>> i & 1L) == 1L) {
        int j = 62 - i;
        result |= 1L << j;
      }
    }

    return result;
  }

  private long blockTillNextMillis(long lastTimestamp) {
    long timestamp;
    for (timestamp = this.currentTime(); timestamp <= lastTimestamp; timestamp = this.currentTime()) {
    }

    return timestamp;
  }

  private long currentTime() {
    return System.currentTimeMillis();
  }

  public static synchronized void init(long workerId) {
    if (idWorker == null) {
      idWorker = new SnowflakeIdGenerator(workerId);
    }

  }

  public static SnowflakeIdGenerator getInstance() {
    return idWorker;
  }
}
