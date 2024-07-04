package com.sp.orange.dto;

import com.sp.orange.model.Cuisine;
import com.sp.orange.model.CuisineFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CuisineDto extends Cuisine {
    private List<CuisineFlavor> flavors = new ArrayList<>();
    private String categoryName;
    private Integer copies;
}
