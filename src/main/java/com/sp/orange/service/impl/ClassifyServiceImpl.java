package com.sp.orange.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sp.orange.common.CustomException;
import com.sp.orange.model.Classify;
import com.sp.orange.mapper.ClassifyMapper;
import com.sp.orange.service.ClassifyService;
import com.sp.orange.service.CuisineService;
import com.sp.orange.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClassifyServiceImpl extends ServiceImpl<ClassifyMapper, Classify> implements ClassifyService {
    @Autowired
    private CuisineService cuisineService;
    @Autowired
    private ComboService comboService;

    /**
     * 新增，排序不能重复否则前端出错
     *
     * @param classify
     * @return
     */
   @Override
    public void categorySave(Classify classify) {
//        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Category::getSort,category.getSort());
//        int count = this.count(lambdaQueryWrapper);
//        if(count>0){
//            throw new CustomException("排序数字不能重复");
//        }
        this.save(classify);
   }

    /**
     * 分页构造
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page page(int page, int pageSize) {//这里的page，pageSize,为分页请求携带的参数
//        log.info("page ={},pageSize={}", page,pageSize);
        //创建分页构造器
        Page pageInfo = new Page(page, pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Classify> queryWrapper = new LambdaQueryWrapper<>();
        //以sort属性为排序条件
        queryWrapper.orderByDesc(Classify::getSort);
        //执行查询业务
        this.page(pageInfo, queryWrapper);
        return pageInfo;
    }

    //查询菜品分类信息
    @Override
    public List<Classify> queryDishByID(Classify classify) {
        LambdaQueryWrapper<Classify> lambdaQueryWrapper = new LambdaQueryWrapper();
        //添加条件
        lambdaQueryWrapper.eq(classify.getType() != null, Classify::getType, classify.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Classify::getSort).orderByDesc(Classify::getUpdateTime);
        List<Classify> dishList = this.list(lambdaQueryWrapper);
        return dishList;
    }

    @Override
    public String update(Classify classify) {//前端传入的数据
//        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Category::getSort,category.getSort());
//        int count = this.count(lambdaQueryWrapper);
//        if(count>0){
//            throw new CustomException("排序数字不能重复");
//        }
        this.updateById(classify);
        return "修改分类信息成功";
    }

    /**
     * 根据id删除分类,删除之前需要进行判断
     *
     * @param id
     */
    @Override
    public void removeByDS(Long id) {
       // LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
//        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
//        int count1 = dishService.count(dishLambdaQueryWrapper);
//        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
//        int count2 = setmealService.count(setmealLambdaQueryWrapper);
//
//        //查询当前的分类是否关联了菜品，如果已经关联，抛出一个业务异常
//        if (count1 > 0) {//如果根据dish中分类id查询的数据存在
//            throw new CustomException("当前分类下关联了菜品，不能删除");
//        }
//        //查询当前的分类是否关联了套餐，如果已经关联，抛出一个业务异常
//        if (count2 > 0) {
//            throw new CustomException("当前分类下关联了套餐，不能删除");
//        }
        if (id==null){
            throw new CustomException("未知错误,请联系管理员");
        }
        //正常删除分类
        super.removeById(id);
    }
}











