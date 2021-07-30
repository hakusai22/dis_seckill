package com.seckill.dis.common.api.goods.vo;

import com.seckill.dis.common.api.user.vo.UserVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品详情
 * 用于将数据传递给客户端
 * @author xizizzz
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoodsDetailVo implements Serializable {


    private int seckillStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods;
    private UserVo user;

}
