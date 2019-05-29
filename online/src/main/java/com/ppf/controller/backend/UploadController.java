package com.ppf.controller.backend;

import com.ppf.common.ResponseCode;
import com.ppf.common.ServerResponse;

import com.ppf.vo.ImageVO;
import org.apache.ibatis.javassist.bytecode.analysis.MultiArrayType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/manage/product/")
public class UploadController {
    @Value("${online.imageHost}")
    private String imageHost;
   @GetMapping("richtext_img_upload.do")
    public String upload(){
       return "upload";
   }
   @PostMapping("richtext_img_upload.do")
   @ResponseBody
    public ServerResponse upload(MultipartFile uploadfile) {
       if (uploadfile == null || uploadfile.getOriginalFilename().equals("")) {
           return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"必须上传图片");
       }
       //获取上传图片的名称
       String oldFileName = uploadfile.getOriginalFilename();
       //获取文件的扩展名
       String extendName = oldFileName.substring(oldFileName.lastIndexOf('.'));
       //生成新的文件名
       String newFilename = UUID.randomUUID().toString() + extendName;
       //保存图片
       File mkdir = new File("d:/upload");
       //判断路径存不存在
       if (!mkdir.exists()) {
           mkdir.mkdirs();
       }
        File newFile=new File(mkdir,newFilename);
       //执行transferTo，文件上传
       try{
        uploadfile.transferTo(newFile);
        //返回图片的地址和名字
        ImageVO imageVO=new ImageVO(newFilename,imageHost+newFilename);
        return ServerResponse.createServerResponseBySucess(null,imageVO);
       }catch(IOException e){
            e.printStackTrace();
       }

       return ServerResponse.createServerResponseByFail(ResponseCode.ERROR);
   }
}
