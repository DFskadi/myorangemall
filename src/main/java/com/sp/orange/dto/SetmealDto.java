package com.sp.orange.dto;


import com.sp.orange.entity.Setmeal;
import com.sp.orange.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
