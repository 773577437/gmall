package com.test.gmall.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.zookeeper.data.Stat;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor  //全参构造器
@NoArgsConstructor   //无参构造器
@ApiModel
@Data
public class PageInfoVo implements Serializable {

    @ApiModelProperty("总记录数")
    private Long total;

    @ApiModelProperty("总页码")
    private Long totalPage;

    @ApiModelProperty("每页显示的记录数")
    private Long pageSize;

    @ApiModelProperty("每页显示的数据")
    private List<? extends Object> list;

    @ApiModelProperty("当前的页码")
    private Long pageNum;

    public static PageInfoVo pageInfoVo(IPage iPage,  Long pageSize){
        return new PageInfoVo(iPage.getTotal(),iPage.getPages(),pageSize,iPage.getRecords(),iPage.getCurrent());
    }
}
