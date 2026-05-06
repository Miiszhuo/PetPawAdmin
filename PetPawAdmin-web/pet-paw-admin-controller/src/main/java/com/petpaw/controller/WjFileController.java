package com.petpaw.controller;

import com.petpaw.common.result.Result;
import com.petpaw.entity.WjFile;
import com.petpaw.service.WjFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/file")
public class WjFileController {

    @Autowired
    private WjFileService wjFileService;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "businessId", required = false) Long businessId,
                                 @RequestParam(value = "businessType", required = false) String businessType) {
        String fileUrl = wjFileService.uploadFile(file, businessId, businessType);
        return Result.success(fileUrl);
    }

    @GetMapping("/list")
    public Result<List<WjFile>> list(@RequestParam("businessId") Long businessId,
                                     @RequestParam("businessType") String businessType) {
        List<WjFile> files = wjFileService.getFiles(businessId, businessType);
        return Result.success(files);
    }
}
