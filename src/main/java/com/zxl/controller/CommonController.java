package com.zxl.controller;

import com.zxl.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author ：xxx
 * @description：TODO 文件的上传和下载
 * @date ：2024/02/03 15:38
 */
@RestController
@RequestMapping("common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file 不能随便起名字，要和前端一致
     * @return
     */
    @PostMapping("upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置
            //获得原始文件名
        String originalFilename = file.getOriginalFilename();
        //使用uuid重新生成文件名，防止文件名称重复文件覆盖
        //后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString()+suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        if(!dir.exists()){
            //目录不存在，创建
            dir.mkdir();
        }
        try {
            //将文件转存在这里
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            //从输入流中读到bytes数组中
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
