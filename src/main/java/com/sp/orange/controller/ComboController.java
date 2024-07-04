package com.sp.orange.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.orange.common.R;
import com.sp.orange.dto.CuisineDto;
import com.sp.orange.dto.ComboDto;
import com.sp.orange.model.Combo;
import com.sp.orange.model.ComboCuisine;
import com.sp.orange.service.ClassifyService;
import com.sp.orange.service.CuisineService;
import com.sp.orange.service.ComboCuisineService;
import com.sp.orange.service.ComboService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/combo")
@Slf4j
@Api(tags = "套餐相关接口")
public class ComboController {
    @Autowired
    private ComboService comboService;
    @Autowired
    private ComboCuisineService comboCuisineService;
    @Autowired
    private CuisineService cuisineService;
    @Autowired
    private ClassifyService classifyService;


    /**
     * 新增套餐
     *
     * @param comboDto
     * @return
     */
    @PostMapping()
    @ApiOperation("新增套餐接口")
    @CacheEvict(value = "comboCache", allEntries = true)
    public R<String> save(@RequestBody ComboDto comboDto) {
        comboService.saveWithCuisine(comboDto);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "name", value = "套餐名称", required = false)
    })
    public R<Page> page(String name, int page, int pageSize) {
        Page dtoPage = comboService.page(name, page, pageSize);
        return R.success(dtoPage);
    }


    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "comboCache", allEntries = true)//allEntries = true ，清理setmealCache分类下的所有缓存数据
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info(String.valueOf(ids.size()));
        if (ids.size()==0){
           // throw new CustomException("批量操作，请先勾选操作套餐！");
            return R.error("批量操作，请先勾选操作套餐！");
        }
        comboService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件查询套餐数据
     *
     * @param combo
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "comboCache", key = "#combo.categoryId+'_'+#combo.status")
    public R<List<Combo>> list(Combo combo) {
        List<Combo> list = comboService.list(combo);
        return R.success(list);
    }


    /**
     * 套餐数据回显
     */
    @GetMapping("/{id}")
    public R<ComboDto> getById(@PathVariable Long id) {
        ComboDto combodto = comboService.getByIdWithDish(id);
        return R.success(combodto);
    }



    @PutMapping
    @CacheEvict(value = "comboCache", allEntries = true)
    public R<String> update(@RequestBody ComboDto comboDto) {
        if (comboDto == null) {
            return R.error("请求异常");
        }
        if (comboDto.getSetmealDishes() == null) {
            return R.error("套餐没有菜品,请添加套餐");
        }
        comboService.updateWithDish(comboDto);
        return R.success("修改套餐信息成功");
    }

    /**
     * 更新套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("更新套餐状态接口")
    @CacheEvict(value = "comboCache", allEntries = true)
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        String str = comboService.status(status, ids);
        return R.success(str);
    }



    @GetMapping("/cuisine/{id}")
    public R<List<CuisineDto>> cuisine(@PathVariable("id") Long comboId){
        List<CuisineDto> cuisineDtoList = comboService.dish(comboId);
        return R.success(cuisineDtoList);
    }




}









