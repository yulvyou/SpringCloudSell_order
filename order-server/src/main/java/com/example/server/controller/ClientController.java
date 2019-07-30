package com.example.server.controller;

import com.example.client.ProductClient;
import com.example.common.DecreaseStockInput;
import com.example.server.dataobject.ProductInfo;
import com.example.server.dto.CartDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端Controller ； 主要是用于调用其他服务的接口
 */
@RestController
@Slf4j
public class ClientController {

//    //RestTemplate第二种方式将用到
//    @Autowired
//    private LoadBalancerClient loadBalancerClient;

//    //RestTemplate第三种方法将用到
//    //因为在配置文件中将RestTemplate注册为Bean了，所以可以Autowired
//    @Autowired
//    private RestTemplate restTemplate;

    //使用 Feign 调用 product 服务的接口
    @Autowired
    ProductClient productClient;


    @GetMapping("getProductMsg")
    public String getProductMsg(){
//        //1.RestTemplate第一种方式 (直接使用RestTemplate，url写死)
//        RestTemplate restTemplate = new RestTemplate();
//        String response = restTemplate.getForObject("http://localhost:8080/msg",
//                String.class);

//        //2.RestTemplate第二种方式（利用LoadBalancerClient通过应用名获取url，然后使用RestTemplate）
//        RestTemplate restTemplate = new RestTemplate();
//        ServiceInstance serviceInstance = loadBalancerClient.choose("PRODUCT");
//        String url = String .format("http://%s:%s",serviceInstance.getHost(),
//                serviceInstance.getPort()+"msg");
//        String response = restTemplate.getForObject(url, String.class);

        //3.RestTemplate第三种(利用@LoadBalanced，可以在restTemplate的url中使用应用的名称，如“PRODUCT”)
//        String response = restTemplate.getForObject("http://PRODUCT/msg",String.class);


        //使用 Feign方法
        String response = productClient.productMsg();
        log.info("response = {}",response);

        return response;
    }


    @GetMapping("getProductList")
    public String getProductList(){
        ArrayList productIdList = new ArrayList();
        productIdList.add("164103465734242707");
        List<ProductInfo> productInfoList = productClient.listForOrder(productIdList);

        log.info("respons={}",productIdList.toString());

        return "ok";
    }


    @GetMapping("/productDecreastStock")
    public String productDescreaseStock(){
//        CartDTO cartDTO = new CartDTO("164103465734242707",2);
//        List<CartDTO> cartDTOList = new ArrayList<CartDTO>();
//        cartDTOList.add(cartDTO);

        List<DecreaseStockInput> decreaseStockInputList = new ArrayList<DecreaseStockInput>();
        DecreaseStockInput decreaseStockInput = new DecreaseStockInput("164103465734242707",2);
        decreaseStockInputList.add(decreaseStockInput);

        productClient.decreaseStock(decreaseStockInputList);
        return "OK";
    }

}
