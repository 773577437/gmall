package com.test.shop.controller;

        import com.alibaba.dubbo.config.annotation.Reference;
        import com.test.gmall.oms.service.OrderService;
        import com.test.gmall.oms.vo.OrderSubmitVo;
        import com.test.gmall.oms.vo.OrderVo;
        import com.test.gmall.to.CommonResult;
        import io.swagger.annotations.Api;
        import io.swagger.annotations.ApiOperation;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.*;

        import javax.servlet.http.HttpServletRequest;
        import java.io.UnsupportedEncodingException;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Map;

@Slf4j
@Api(tags = "订单服务")
@CrossOrigin
@RequestMapping("/order")
@RestController
public class orderController {

    @Reference
    private OrderService orderService;

    /**
     * 当信息确认后，防止用户不断提交，我们必须做防重验证【接口幂等性设计】
     * 1、业务层面使用防重令牌 + 分布式锁
     *
     * 2、数据库层面使用数据库的锁机制保证多次请求幂等
     *      insert(); [如果id不自增，传入id, 保证传入一个唯一字段]
     *      delete(); [带id删除，幂等操作]
     *      update(); [乐观锁] update set stock-1 version+1 where skuId = 1 version = 2 通过版本控制
     *
     * @return
     */
    @ApiOperation("确认订单")
    @RequestMapping("/confirm")
    public Object confirmOrder(@RequestParam(value = "accessToken", required = false) String accessToken,
                               @RequestParam(value = "skuId",required = false) Long skuId){

        OrderVo orderVo = orderService.getToOrderVo(accessToken, skuId);
        return  new CommonResult().success(orderVo);
    }

    @ApiOperation("提交订单")
    @PostMapping("/submit")
    public Object submitOrder(@RequestParam(value = "accessToken") String accessToken,
                              @RequestParam(value = "addressId") Long addressId,
                              @RequestParam(value = "orderToken") String orderToken){

        OrderSubmitVo orderSubmitVo = orderService.getToOrderSubmitVo(orderToken, accessToken, addressId);
        return new CommonResult().success(orderSubmitVo);
    }

    @ApiOperation("结算订单")
    @GetMapping(value = "/tally", produces = "text/html")
    public Object tallyOrder(@RequestParam(value = "orderSn") String orderSn,
                             @RequestParam(value = "accessToken") String accessToken){

        String par = orderService.payOrder(orderSn, accessToken);
        return par;
    }

    /**
     * 异步通知的处理
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @ResponseBody
    @RequestMapping("/payAsync")
    public String payAsync(HttpServletRequest request) throws UnsupportedEncodingException {
        log.debug("支付宝支付异步通知完成....");
        // 修改订单的状态
        // 支付宝收到了success说明处理完成，不会再通知

        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        // 商户订单号
        String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
        // 支付宝流水号
        String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
        // 交易状态
        String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

        params.put("out_trade_no", out_trade_no);
        params.put("trade_no", trade_no);
        params.put("trade_status", trade_status);

        orderService.payToAsync(params);

        return "success";
    }

}
