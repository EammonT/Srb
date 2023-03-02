package com.tym.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.tym.srb.core.mapper.DictMapper;
import com.tym.srb.core.pojo.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {

    private DictMapper dictMapper;
    private ArrayList<ExcelDictDTO> list = new ArrayList<>();
    private static final int BATCH_COUNT = 5;

    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(ExcelDictDTO excelDictDTO, AnalysisContext analysisContext) {
        log.info("解析到一条记录 {}",excelDictDTO);
        list.add(excelDictDTO);
        if (list.size() >= BATCH_COUNT){
            saveData();
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("所以数据解析完成！");
    }

    private void saveData(){
        log.info("{}条数据被存储到数据库....",list.size());
        dictMapper.insertBatch(list);
        log.info("{}条数据成功存储到数据库...",list.size());

    }
}
