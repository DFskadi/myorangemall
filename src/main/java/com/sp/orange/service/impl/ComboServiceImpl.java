package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.orange.common.CustomException;
import com.sp.orange.dto.CuisineDto;
import com.sp.orange.dto.ComboDto;
import com.sp.orange.model.*;
import com.sp.orange.mapper.ComboMapper;
import com.sp.orange.service.ClassifyService;
import com.sp.orange.service.CuisineService;
import com.sp.orange.service.ComboCuisineService;
import com.sp.orange.service.ComboService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ComboServiceImpl extends ServiceImpl<ComboMapper, Combo> implements ComboService {
    @Autowired
    private ComboCuisineService comboCuisineService;
    @Autowired
    private ClassifyService classifyService;

    @Autowired
    private CuisineService cuisineService;

    @Override
    public Page page(String name, int page, int pageSize) {
        //创建分页构造器
        Page<Combo> pageInfo = new Page<>(page, pageSize);
        Page<ComboDto> dtoPage = new Page<>(page, pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Combo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        lambdaQueryWrapper.like(name != null, Combo::getName, name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Combo::getUpdateTime);

        this.page(pageInfo, lambdaQueryWrapper);


        //对象拷贝 先拷贝分页中除了records的数据，再手动拷贝records(先创建一个setmealDto封装其中的分类名属性，再拷贝其它的信息)
        BeanUtils.copyProperties(page, dtoPage, "records");//不需要拷贝records(页面中的每一条数据)，这是由于page和dtoPage的泛型不同
        List<Combo> records = pageInfo.getRecords();//这里需要自己将pageInfo中的records中数据拷贝到dtoPage中
        List<ComboDto> list = null;
        list = records.stream().map((item) -> {
            ComboDto setmealDto = new ComboDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Classify classify = classifyService.getById(categoryId);
            if (classify != null) {
                //分类名称
                String categoryName = classify.getName();
                setmealDto.setCategoryName(categoryName);//最终对复制分类名称，但是其它属性还是null

            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return dtoPage;

    }

    /**
     * 根据条件查询套餐数据
     *
     * @param combo
     * @return
     */
    @Override
    public List<Combo> list(Combo combo) {
        LambdaQueryWrapper<Combo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(combo.getCategoryId() != null, Combo::getCategoryId, combo.getCategoryId());
        queryWrapper.eq(combo.getStatus() != null, Combo::getStatus, combo.getStatus());
        queryWrapper.orderByDesc(Combo::getUpdateTime);
        List<Combo> list = this.list(queryWrapper);

        return list;
    }

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param comboDto
     */
    @Override
    public void saveWithCuisine(ComboDto comboDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(comboDto);//插入成功后，setmealId生成
        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作

        List<ComboCuisine> comboCuisineList = comboDto.getSetmealDishes();


        comboCuisineList.stream().map((item) -> {
            item.setSetmealId(comboDto.getId());
            return item;
        }).collect(Collectors.toList());


        comboCuisineService.saveBatch(comboCuisineList);
    }


    @Override
    public void removeWithDish(List<Long> ids) {

        //select count(*) from setmeal where id in (1,2,3) and sratus =1

        //查询订单状态，确定是否可以删除
        LambdaQueryWrapper<Combo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ids != null, Combo::getId, ids);
        lambdaQueryWrapper.eq(Combo::getStatus, 1);

        int count = this.count(lambdaQueryWrapper);

        //如果不能删除抛出一个业务异常(自定义的异常)
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");//
        }
        //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);
        //删除关系表中的数据

        LambdaQueryWrapper<ComboCuisine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ComboCuisine::getSetmealId, ids);
        comboCuisineService.remove(queryWrapper);
    }

    //数据回显
    @Override
    public ComboDto getByIdWithDish(Long id) {
        Combo combo = this.getById(id);
        ComboDto setmealDto = new ComboDto();
        LambdaQueryWrapper<ComboCuisine> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(id != null, ComboCuisine::getSetmealId, id);
        List<ComboCuisine> sdish = comboCuisineService.list(lambdaQueryWrapper);
        BeanUtils.copyProperties(combo, setmealDto);
        setmealDto.setSetmealDishes(sdish);

        return setmealDto;

    }


    //套餐更新
    @Override
    public void updateWithDish(ComboDto setmealDto) {

        LambdaQueryWrapper<ComboCuisine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ComboCuisine::getSetmealId, setmealDto.getId());//setmealDto类中并没有id属性，这个id是他的父类Dish的属性
        comboCuisineService.remove(queryWrapper);
        //添加当前提交过来的口味数据
        //和新增时情况相同setmealId没有被封装，需要手动封装
        List<ComboCuisine> comboCuisines = setmealDto.getSetmealDishes();
        comboCuisines = comboCuisines.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        this.updateById(setmealDto);
        comboCuisineService.saveBatch(comboCuisines);
    }

    /**
     * 套餐停售&起售
     */
    @Override
    public String status(Integer status,List<Long> ids){
        LambdaUpdateWrapper<Combo> updateWrapper =new LambdaUpdateWrapper<>();
        updateWrapper.in(ids !=null, Combo::getId,ids).set(Combo::getStatus,status);
        this.update(updateWrapper);
        return "批量操作成功";
    }

    /**
     * 点击套餐图片查看详情
     * 移动端点击套餐图片查看套餐具体内容
     *这里返回的是dto 对象，因为前端需要copies这个属性
     *前端主要要展示的信息是:套餐中菜品的基本信息，图片，菜品描述，以及菜品的份数
     *
     * @param setmealId
     * @return
     */
    @Override
    public List<CuisineDto> dish(Long setmealId){
        LambdaQueryWrapper<ComboCuisine> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmealId!=null, ComboCuisine::getSetmealId,setmealId);
        List<ComboCuisine> comboCuisineList = comboCuisineService.list(lambdaQueryWrapper);
        List<CuisineDto> dishDtoList = comboCuisineList.stream().map((setmealDish)->{
            CuisineDto dishDto=new CuisineDto();
            //这个BeanUtils的拷贝是浅拷贝
            BeanUtils.copyProperties(setmealDish,dishDto);
            //这里是为了套餐ba中的菜品的基本信息填充到dto中，比如菜品描述，菜品图片等
            Long dishId = setmealDish.getDishId();
            Cuisine cuisine = cuisineService.getById(dishId);
            BeanUtils.copyProperties(cuisine,dishDto);
            return dishDto;
        }).collect(Collectors.toList());
        return dishDtoList;
    }

}








