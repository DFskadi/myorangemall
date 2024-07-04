package com.sp.orange.dto;


import com.sp.orange.model.Combo;
import com.sp.orange.model.ComboCuisine;
import lombok.Data;
import java.util.List;

@Data
public class ComboDto extends Combo {

    private List<ComboCuisine> setmealDishes;

    private String categoryName;
}
