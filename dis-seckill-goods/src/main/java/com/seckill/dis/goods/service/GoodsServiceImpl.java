package com.seckill.dis.goods.service;

import com.seckill.dis.common.api.goods.GoodsServiceApi;
import com.seckill.dis.common.api.goods.vo.GoodsVo;
import com.seckill.dis.common.api.goods.vo.InsertGoodsDTO;
import com.seckill.dis.goods.persistence.GoodsMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 商品服务接口实现
 * @author xizizzz
 */
@Service(interfaceClass = GoodsServiceApi.class)
public class GoodsServiceImpl implements GoodsServiceApi {

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> listGoodsVo() {
        List<GoodsVo> goodsVos = new ArrayList<>();
        goodsVos = goodsMapper.listGoodsVo();
            for (GoodsVo goodsVo : goodsVos) {
                Date startDate = goodsVo.getStartDate();
                Date endDate = goodsVo.getEndDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                goodsVo.setStartDateString(sdf.format(startDate));
                goodsVo.setEndDateString(sdf.format(endDate));
            }
        System.out.println("goodsVos1"+goodsVos);
        return goodsVos;
    }

    /**
     * 通过商品的id查出商品的所有信息（包含该商品的秒杀信息）
     * @param goodsId
     */
    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 减库存
     * @param goodsId
     */
    @Override
    public void reduceStock(long goodsId) {
        goodsMapper.reduceStack(goodsId);
        
    }

    @Override
    public void deleteGoods(long goodsId) {
        goodsMapper.deleteGoods(goodsId);
    }

    @Override
    public void updateGoods(long goodsId) {
        Date endDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(endDate);
        calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动
        endDate=calendar.getTime(); //这个时间就是日期往后推一天的结果
        goodsMapper.updateGoods(goodsId,endDate);
    }

    @Override
    public void insertGoods(InsertGoodsDTO insertGoodsDTO) {
        insertGoodsDTO.setStartDate(new Date());
        Date endDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(endDate);
        calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动
        endDate=calendar.getTime(); //这个时间就是日期往后推一天的结果
        insertGoodsDTO.setEndDate(endDate);
        goodsMapper.insert(insertGoodsDTO);
    }
}
