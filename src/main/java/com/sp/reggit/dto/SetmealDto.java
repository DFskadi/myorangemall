package com.sp.reggit.dto;


import com.sp.reggit.entity.Setmeal;
import com.sp.reggit.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
