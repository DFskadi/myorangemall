package com.sp.reggit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.reggit.common.R;
import com.sp.reggit.dto.SetmealDto;
import com.sp.reggit.entity.Category;
import com.sp.reggit.entity.Setmeal;
import com.sp.reggit.service.CategoryService;
import com.sp.reggit.service.DishService;
import com.sp.reggit.service.SetmealDishService;
import com.sp.reggit.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
       // log.info(setmealDto.toString());
        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R<Page> page(String name,int page,int pageSize){
        //创建分页构造器
        Page<Setmeal> pageInfo =new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage =new Page<>(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件
        lambdaQueryWrapper.eq(name!=null,Setmeal::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,lambdaQueryWrapper);


        //对象拷贝 先拷贝分页中除了records的数据，再手动拷贝records(先创建一个setmealDto封装其中的分类名属性，再拷贝其它的信息)
        BeanUtils.copyProperties(page,dtoPage,"records");//不需要拷贝records(页面中的每一条数据)，这是由于page和dtoPage的泛型不同
        List<Setmeal> records = pageInfo.getRecords();//这里需要自己将pageInfo中的records中数据拷贝到dtoPage中
        List<SetmealDto> list=null;
      list =  records.stream().map((item)->{
            SetmealDto setmealDto =new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);//最终对复制分类名称，但是其它属性还是null

            }
              return setmealDto;
        }).collect(Collectors.toList());

      dtoPage.setRecords(list);
        return R.success(dtoPage);

    }


    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam  List<Long> ids){
//        log.info("ids",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
        public R<List<Setmeal>> list( Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
        }


}









