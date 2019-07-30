package com.example.server.service.impl;

import com.example.client.ProductClient;
import com.example.common.DecreaseStockInput;
import com.example.common.ProductInfoOutput;
import com.example.server.dataobject.OrderDetail;
import com.example.server.dataobject.OrderMaster;
import com.example.server.dto.OrderDTO;
import com.example.server.enums.OrderStatusEnum;
import com.example.server.enums.PayStatusEnum;
import com.example.server.repository.OrderDetailRepository;
import com.example.server.repository.OrderMasterRepository;
import com.example.server.service.OrderService;
import com.example.server.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {


    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    //商品服务
    @Autowired
    ProductClient productClient;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        String orderId = KeyUtil.genUniqueKey();

        // 1. 查询商品信息（数量, 价格  调用商品服务）
        //获取订单中的商品ID
        List<String> productIdList = orderDTO.getOrderDetailList().stream()
                .map(OrderDetail::getProductId)
                .collect(Collectors.toList());
        //调用 “商品服务” 得到商品信息
        List<ProductInfoOutput> productInfoList = productClient.listForOrder(productIdList);

        // 2. 计算订单总价
        BigDecimal orderAmout = new BigDecimal(BigInteger.ZERO);
        //遍历购物车中的商品
        for (OrderDetail orderDetail: orderDTO.getOrderDetailList()) {
            for (ProductInfoOutput productInfo: productInfoList) {
                if (productInfo.getProductId().equals(orderDetail.getProductId())) {
                    //单价*数量
                    orderAmout = productInfo.getProductPrice()
                            .multiply(new BigDecimal(orderDetail.getProductQuantity()))
                            .add(orderAmout);
                    BeanUtils.copyProperties(productInfo, orderDetail);
                    orderDetail.setOrderId(orderId);
                    orderDetail.setDetailId(KeyUtil.genUniqueKey());
                    //订单详情入库
                    orderDetailRepository.save(orderDetail);
                }
            }
        }


        // 3. 扣库存（调用商品服务）
        List<DecreaseStockInput> decreaseStockInputList = orderDTO.getOrderDetailList().stream()
                .map(e -> new DecreaseStockInput(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        //调用 "商品服务" 扣库存
        productClient.decreaseStock(decreaseStockInputList);

        // 4.订单入库  （orderMaster和orderDetail）
        OrderMaster orderMaster = new OrderMaster();

        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderMaster.setOrderAmount(orderAmout);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);

        return orderDTO;
    }

}
