package com.seckill.dis.gateway.order;

import com.seckill.dis.common.api.goods.GoodsServiceApi;
import com.seckill.dis.common.api.goods.vo.GoodsVo;
import com.seckill.dis.common.api.order.OrderServiceApi;
import com.seckill.dis.common.api.order.vo.OrderDetailVo;
import com.seckill.dis.common.api.user.vo.UserVo;
import com.seckill.dis.common.domain.OrderInfo;
import com.seckill.dis.common.domain.SeckillUser;
import com.seckill.dis.common.result.CodeMsg;
import com.seckill.dis.common.result.Result;
import com.seckill.dis.gateway.aop_log.LogAnnotation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 订单服务接口
 *
 * @author hakusai
 */


@Controller
@RequestMapping("/order/")
public class OrderController {

  @Reference(interfaceClass = OrderServiceApi.class)
  OrderServiceApi orderService;

  @Reference(interfaceClass = GoodsServiceApi.class)
  GoodsServiceApi goodsService;

  /**
   * 因为在redis缓存中不存页面缓存时需要手动渲染，所以注入一个视图解析器，自定义渲染
   */
  @Autowired
  ThymeleafViewResolver thymeleafViewResolver;

  /**
   * 获取订单详情
   */
  @LogAnnotation(module = "获取订单详情", operation = "获取订单详情")
  @RequestMapping("detail")
  @ResponseBody
  public Result<OrderDetailVo> orderInfo(Model model, UserVo user,
      @RequestParam("orderId") long orderId) {
    if (user == null) {
      return Result.error(CodeMsg.SESSION_ERROR);
    }

    // 获取订单信息
    OrderInfo order = orderService.getOrderById(orderId);
    if (Objects.isNull(order)) {
      return Result.error(CodeMsg.ORDER_NOT_EXIST);
    }
    // 如果订单存在，则根据订单信息获取商品信息
    long goodsId = order.getGoodsId();
    GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    OrderDetailVo vo = new OrderDetailVo();
    vo.setUser(user);// 设置用户信息
    vo.setOrder(order); // 设置订单信息
    vo.setGoods(goods); // 设置商品信息
    return Result.success(vo);
  }

  @LogAnnotation(module = "查询全部的订单", operation = "查询全部的订单")
  @RequestMapping(value = "getAllOrderInfo", method = RequestMethod.GET)
  @ResponseBody
  public String getAllOrderInfo(
      HttpServletRequest request, HttpServletResponse response, Model model) {
    List<OrderInfo> orderList = orderService.getAllOrder();
    System.out.println("orderList" + orderList.toString());
    model.addAttribute("orderList", orderList);
    // 3. 渲染html
    WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
    // (第一个参数为渲染的html文件名，第二个为web上下文：里面封装了web应用的上下文)
    String html = thymeleafViewResolver.getTemplateEngine().process("orderInfo_list", webContext);
    return html;
  }

  @GetMapping(value = "deleteOrder")
  @ResponseBody
  public String deleteOrder(@RequestParam("id") long id, Model model,
      HttpServletResponse response, HttpServletRequest request,
      UserVo user) throws ParseException {
    orderService.deleteOrder(id);
    List<OrderInfo> orderList = orderService.getAllOrder();
    System.out.println("orderList" + orderList.toString());
    model.addAttribute("orderList", orderList);
    // 3. 渲染html
    WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
    // (第一个参数为渲染的html文件名，第二个为web上下文：里面封装了web应用的上下文)
    String html = thymeleafViewResolver.getTemplateEngine().process("orderInfo_list", webContext);
    return html;
  }
}
