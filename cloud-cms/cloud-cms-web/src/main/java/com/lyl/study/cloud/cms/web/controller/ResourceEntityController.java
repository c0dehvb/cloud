package com.lyl.study.cloud.cms.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.Random;
import com.lyl.study.cloud.base.dto.PageInfo;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.cms.api.dto.request.ResourceEntityListConditions;
import com.lyl.study.cloud.cms.api.dto.request.ResourceEntitySaveForm;
import com.lyl.study.cloud.cms.api.dto.response.ResourceEntityDTO;
import com.lyl.study.cloud.cms.api.facade.ResourceEntityFacade;
import com.lyl.study.cloud.cms.web.UploadProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.lyl.study.cloud.cms.api.CmsErrorCode.*;

@RestController("/resourceEntity")
public class ResourceEntityController implements InitializingBean {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MediaType MEDIA_TYPE_IMAGE = MediaType.parseMediaType("image/*");
    private final MediaType MEDIA_TYPE_AUDIO = MediaType.parseMediaType("audio/*");
    private final MediaType MEDIA_TYPE_VIDEO = MediaType.parseMediaType("video/*");

    private Long maxImageSize;
    private Long maxAudioSize;
    private Long maxVideoSize;
    private Long maxFileSize;
    private String urlPrefix;
    private Path uploadPath;

    @Autowired
    private UploadProperties properties;
    @Reference
    private ResourceEntityFacade resourceEntityFacade;

    @Override
    public void afterPropertiesSet() {
        BeanUtils.copyProperties(properties, this);
    }

    @PostMapping
    public Result uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        try {
            checkFileSize(multipartFile);
            Path path = resolveFilePath(multipartFile);
            multipartFile.transferTo(path.toAbsolutePath().toFile());
            ResourceEntitySaveForm form = buildSaveForm(multipartFile, path);

            try {
                resourceEntityFacade.save(form);
                return new Result<>(OK, "上传成功", form.getUrl());
            } catch (Exception e) {
                logger.error(e.toString());
                Files.deleteIfExists(Paths.get(form.getFilepath()));
                return new Result<>(FILE_SAVE_INTERNAL_SERVICE_EXCEPTION, "内部服务调用错误", e.getMessage());
            }
        } catch (IOException e) {
            return new Result<>(FILE_SAVE_IO_EXCEPTION, "文件读写异常", e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") Long id) {
        int rows = resourceEntityFacade.deleteById(id);
        if (rows > 0) {
            return new Result<>(OK, "删除成功", null);
        } else {
            return new Result<>(NOT_FOUND, "找不到ID为" + id + "的资源文件", null);
        }
    }

    @GetMapping("/list")
    public Result<PageInfo<ResourceEntityDTO>> list(@ModelAttribute ResourceEntityListConditions conditions) {
        PageInfo<ResourceEntityDTO> page = resourceEntityFacade.list(conditions);
        return new Result<>(OK, "查询成功", page);
    }


    private ResourceEntitySaveForm buildSaveForm(MultipartFile file, Path localPath) {
        ResourceEntitySaveForm form = new ResourceEntitySaveForm();
        form.setMediaType(file.getContentType());
        form.setOriginalFilename(file.getOriginalFilename());
        form.setSize(file.getSize());
        form.setFilepath(localPath.toAbsolutePath().toString());
        form.setUrl(urlPrefix + uploadPath.relativize(localPath).toString());
        return form;
    }

    /**
     * 计算上传文件的存储路径
     *
     * @param multipartFile 文件信息
     * @return 文件存储路径
     */
    private Path resolveFilePath(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = getFileNameSuffix(originalFilename);

        for (int i = 0; i < 10; i++) {
            String filename = System.currentTimeMillis()
                    + String.format("%04d", new Random().nextInt(10000)) + suffix;

            try {
                Path filepath = uploadPath.resolve(Paths.get(filename));
                Files.createFile(filepath);
                return filepath;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("上传文件失败");
    }

    private String getFileNameSuffix(String filename) {
        int index = filename.lastIndexOf(".");
        return index == -1 ? "" : filename.substring(index);
    }

    /**
     * 验证文件大小
     *
     * @param file 文件
     */
    private void checkFileSize(MultipartFile file) {
        MediaType mediaType = MediaType.parseMediaType(file.getContentType());

        if (MEDIA_TYPE_IMAGE.includes(mediaType)) {
            Assert.isTrue(maxImageSize <= file.getSize(), "上传图片文件过大");
        } else if (MEDIA_TYPE_AUDIO.includes(mediaType)) {
            Assert.isTrue(maxAudioSize <= file.getSize(), "上传音频文件过大");
        } else if (MEDIA_TYPE_VIDEO.includes(mediaType)) {
            Assert.isTrue(maxVideoSize <= file.getSize(), "上传视频文件过大");
        } else {
            Assert.isTrue(maxFileSize <= file.getSize(), "上传文件过大");
        }
    }
}