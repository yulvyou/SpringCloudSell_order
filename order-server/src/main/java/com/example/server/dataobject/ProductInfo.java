package com.example.server.dataobject;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 */
//@Table(name = "") //如果表名和数据库的名称不一样的话需要添加此注解
@Data
@Entity
public class ProductInfo {

    /**商品ID*/
    @Id
    private String     productId;
    /** 商品名称 */
    private String     productName;
    /** 单价 */
    private BigDecimal productPrice;
    /** 库存 */
    private Integer    productStock;
    /** 描述 */
    private String     productDescription;
    /** 小图*/
    private String     productIcon;
    /** 商品状态 0--正常；1--下架*/
    private Integer    productStatus;
    /** 类目编号 */
    private Integer    categoryType;
    /** 创建时间 */
    private Date       createTime;
    /** 更新时间 */
    private Date     updateTime;

}
