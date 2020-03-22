package com.test.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.test.gmall.pms.entity.Brand;
import com.test.gmall.pms.service.BrandService;
import com.test.gmall.pms.vo.PmsBrandParam;
import com.test.gmall.to.CommonResult;
import com.test.gmall.vo.PageInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌功能Controller
 */
@CrossOrigin
@RestController
@Api(tags = "PmsBrandController",description = "商品品牌管理")
@RequestMapping("/brand")
public class PmsBrandController {
    @Reference
    private BrandService brandService;

    @ApiOperation(value = "获取全部品牌列表")
    @GetMapping(value = "/listAll")
    public Object getList() {
        //TODO 获取全部品牌列表  brandService.listAll()
        List<Brand> brands = brandService.list();
        return new CommonResult().success(brands);
    }

    @ApiOperation(value = "添加品牌")
    @PostMapping(value = "/create")
    public Object create(@Validated @RequestBody PmsBrandParam pmsBrand, BindingResult result) {
        CommonResult commonResult = new CommonResult();
        //TODO 添加品牌
        Brand brand = new Brand();
        BeanUtils.copyProperties(pmsBrand, brand);
        boolean save = brandService.save(brand);
        Integer saveLabel = null;
        if(save){
            saveLabel = 1;
        }
        return commonResult.success(saveLabel);
    }

    @ApiOperation(value = "更新品牌")
    @PostMapping(value = "/update/{id}")
    public Object update(@PathVariable("id") Long id,
                              @Validated @RequestBody PmsBrandParam pmsBrandParam,
                              BindingResult result) {
        CommonResult commonResult = new CommonResult();
        //TODO 更新品牌
        Brand brand = new Brand();
        BeanUtils.copyProperties(pmsBrandParam, brand);
        brand.setId(id);
        boolean update = brandService.updateById(brand);
        Integer updateLabel = null;
        if(update){
            updateLabel = 1;
        }
        return commonResult.success(updateLabel);
    }

    @ApiOperation(value = "删除品牌")
    @GetMapping(value = "/delete/{id}")
    public Object delete(@PathVariable("id") Long id) {
        CommonResult commonResult = new CommonResult();
        //TODO 删除品牌
        boolean remove = brandService.removeById(id);
        Integer removeLabel = null;
        if(remove){
            removeLabel = 1;
        }
        return commonResult.success(removeLabel);
    }

    @ApiOperation(value = "根据品牌名称分页获取品牌列表")
    @GetMapping(value = "/list")
    public Object getList(@RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "pageNum", defaultValue = "1") Long pageNum,
                            @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize) {
        CommonResult commonResult = new CommonResult();

        //TODO 根据品牌名称分页获取品牌列表
        PageInfoVo pageInfoVo = brandService.getBrandInfo(keyword, pageNum, pageSize);

        return commonResult.success(pageInfoVo);
    }

    @ApiOperation(value = "根据编号查询品牌信息")
    @GetMapping(value = "/{id}")
    public Object getItem(@PathVariable("id") Long id) {
        CommonResult commonResult = new CommonResult();
        //TODO 根据编号查询品牌信息
        Brand brand = brandService.getById(id);
        return commonResult.success(brand);
    }

    @ApiOperation(value = "批量删除品牌")
    @PostMapping(value = "/delete/batch")
    public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
        CommonResult commonResult = new CommonResult();
        //TODO 批量删除品牌


        return commonResult;
    }

    @ApiOperation(value = "批量更新显示状态")
    @PostMapping(value = "/update/showStatus")
    public Object updateShowStatus(@RequestParam("ids") List<Long> ids,
                                   @RequestParam("showStatus") Integer showStatus) {
        CommonResult commonResult = new CommonResult();
        //TODO 批量更新显示状态


        return commonResult;
    }

    @ApiOperation(value = "批量更新厂家制造商状态")
    @PostMapping(value = "/update/factoryStatus")
    public Object updateFactoryStatus(@RequestParam("ids") List<Long> ids,
                                      @RequestParam("factoryStatus") Integer factoryStatus) {
        CommonResult commonResult = new CommonResult();
        //TODO 批量更新厂家制造商状态


        return commonResult;
    }
}
