package com.test.gmall.to.es;

import com.test.gmall.pms.entity.SkuStock;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EsProductSkuInfo extends SkuStock implements Serializable {

    /*sku的特定标题*/
    private String skuTitle;
    /*每个sku不同的属性和规格*/
    private List<EsProductAttributeValue> attrValueList;
}
