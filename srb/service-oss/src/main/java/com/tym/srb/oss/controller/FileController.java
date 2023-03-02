package com.tym.srb.oss.controller;

import com.tym.common.exception.BusinessException;
import com.tym.common.result.R;
import com.tym.common.result.ResponseEnum;
import com.tym.srb.oss.service.FileService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/oss/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public R upload(
            @ApiParam(value = "文件", required = true)
            @RequestParam("file") MultipartFile file,
            @ApiParam(value = "模块", required = true)
            @RequestParam("module") String module) {
        try {
            // 获取文件流
            InputStream inputStream = file.getInputStream();
            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            String url = fileService.upload(inputStream, module, originalFilename);
            return R.ok().message("文件上传成功").data("url", url);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @DeleteMapping("/remove")
    public R remove(
            @ApiParam(value = "要删除的文件", required = true)
            @RequestParam("url") String url) {
        if(!fileService.removeFile(url)){
            return R.error().message("文件不存在");
        }
        return R.ok().message("文件删除成功");
    }
}
