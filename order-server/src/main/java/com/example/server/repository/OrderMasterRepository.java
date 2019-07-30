package com.example.server.repository;

import com.example.server.dataobject.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订单DAO
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster, String> {

    /**
     * 通过用户openid 查询订单
     * @param buyerOpenid
     * @param pageable
     * @return
     */
    Page<OrderMaster> findByBuyerOpenid(String buyerOpenid, Pageable pageable);
}
