package com.seckill.dis.user.persistence;

import com.seckill.dis.user.domain.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * seckill_user表交互接口
 *
 * @author hakusai
 */
@Mapper
public interface AdminUserMapper {

  /**
   * 通过 phone 查询用户信息
   *
   * @param phone
   * @return
   */
  AdminUser getUserByPhone(@Param("phone") Long phone);

  /**
   * 更新用户信息
   *
   * @param updatedUser
   */
  @Update("UPDATE seckill_user SET password=#{password} WHERE id=#{id}")
  void updatePassword(AdminUser updatedUser);


  /**
   * 插入一条用户信息到数据库中
   *
   * @param seckillUser
   * @return
   */
  long insertUser(AdminUser seckillUser);

  /**
   * 查询电话号码
   *
   * @param phone
   * @return
   */
  long findPhone(long phone);
}
