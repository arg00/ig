package edu.au.cc.gallery;

import edu.au.cc.gallery.tools.UserAdmin;

import static spark.Spark.*;
import spark.Request;
import spark.Response;

import spark.utils.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import spark.ModelAndView;

import spark.template.handlebars.HandlebarsTemplateEngine;

public class Upload {

    public static String uploadToS3(Request req, Response resp){
        String imagesPath = "~/ig/src/main/resources/images/";
      
          req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("D:/tmp"));
          Part filePart = req.raw().getPart("myfile")
          String imagePathName = ""
          try (InputStream inputStream = filePart.getInputStream()) {
              imagePathName = imagesPath + filePart.getSubmittedFileName();
              OutputStream outputStream = new FileOutputStream(imagePathName);
              IOUtils.copy(inputStream, outputStream);
              outputStream.close();
          
          return imagePathName;
              //return "<img src = \"/home/ec2-user/javatest/images/lover.jpg\"/>";
          //return "<a href=\"/\"><img src=\"" + imagePathName + "\"/> </a>";
    }
}
