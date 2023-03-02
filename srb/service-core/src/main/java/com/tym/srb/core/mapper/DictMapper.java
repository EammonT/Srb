package com.tym.srb.core.mapper;

import com.tym.srb.core.pojo.dto.ExcelDictDTO;
import com.tym.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author tym
 * @since 2022-12-18
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertBatch(ArrayList<ExcelDictDTO> list);
}
