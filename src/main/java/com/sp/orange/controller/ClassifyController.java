package com.sp.orange.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sp.orange.common.R;
import com.sp.orange.model.Classify;
import com.sp.orange.service.ClassifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classify")
@Slf4j
public class ClassifyController {
    @Autowired
    private ClassifyService classifyService;


    /**
     * 新增分类
     *
     * @param classify
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Classify classify) {
        classifyService.categorySave(classify);
        return R.success("新增分类成功");
    }

    /**
     * 分页构造
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {//这里的page，pageSize,为分页请求携带的参数
        Page pageInfo = classifyService.page(page, pageSize);
        return R.success(pageInfo);
    }

    //删除分类业务
    @DeleteMapping()
    public R<String> delete(Long id) {
        classifyService.removeByDS(id);
        return R.success("删除分类成功");
    }

    //修改分类业务
    @PutMapping()
    public R<String> update(@RequestBody Classify classify) {//前端传入的数据
        String str = classifyService.update(classify);
        return R.success(str);
    }

    //查询菜品分类信息
    @GetMapping("/list")
    public R<List<Classify>> queryDishByID(Classify classify) {
        List<Classify> dishList = classifyService.queryDishByID(classify);
        return R.success(dishList);
    }


}

