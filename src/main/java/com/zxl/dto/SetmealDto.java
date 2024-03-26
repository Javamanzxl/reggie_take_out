package com.zxl.dto;

import com.zxl.pojo.Setmeal;
import com.zxl.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
