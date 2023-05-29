package com.seckill.dis.user.persistence;

import com.seckill.dis.user.domain.SeckillUser;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * seckill_user表交互接口
 *
 * @author hakusai
 */
@Mapper
public interface SeckillUserMapper {

  /**
   * 通过 phone 查询用户信息
   *
   * @param phone
   * @return
   */
  SeckillUser getUserByPhone(@Param("phone") Long phone);

  List<SeckillUser> getAllUserInfo();

  /**
   * 更新用户信息
   *
   * @param updatedUser
   */
  @Update("UPDATE seckill_user SET password=#{password} WHERE id=#{id}")
  void updatePassword(SeckillUser updatedUser);

  /**
   * 更新用户登录的次数
   */
  void updateLoginCount(@Param("phone") Long phone, @Param("date") Date date);


  /**
   * 插入一条用户信息到数据库中
   *
   * @param seckillUser
   * @return
   */
  long insertUser(SeckillUser seckillUser);

  /**
   * 查询电话号码
   *
   * @param phone
   * @return
   */
  long findPhone(long phone);

  @Delete("delete from seckill_user WHERE uuid=#{uuid}")
  void deleteUser(@Param("uuid") long uuid);
}
