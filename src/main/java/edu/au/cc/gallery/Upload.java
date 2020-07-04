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

    public static String uploadToS3(Request req, Response resp, String username) throws Exception {
	//String username = req.session.attribute("username");
	String s3ImageBucket = "edu.au.cc.arg0055.image-gallery";
	
	String imageDirLocal = "/home/ec2-user/ig/src/main/resources/images/";
	String imagePathLocal = "";
	String imageName = "";

	req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("D:/tmp"));
	Part filePart = req.raw().getPart("image");
	  
          try (InputStream inputStream = filePart.getInputStream()) {
              imageName = filePart.getSubmittedFileName();
	      imagePathLocal = imageDirLocal + imageName;
              OutputStream outputStream = new FileOutputStream(imagePathLocal);
              IOUtils.copy(inputStream, outputStream);
              outputStream.close();
          }

	  catch (Exception e) {
	      System.out.println(e);
	      e.printStackTrace();
     	      return "Failed to upload file to ec2 but made it to upload method!";
	  }

	  String imageBucketPath = "images/" + username + "/" + imageName;
	  
	  try {
	  S3 s3 = new S3();
	  s3.connect();
	  s3.putObjectFilePath(s3ImageBucket, imageBucketPath, imagePathLocal);
	  } catch (Exception e) {
	      e.printStackTrace();
	      return "Error connecting to s3: " + s3ImageBucket + "/" +  imageBucketPath;
	  }
          return s3ImageBucket + imageBucketPath;
              //return "<img src = \"/home/ec2-user/javatest/images/lover.jpg\"/>";
          //return "<a href=\"/\"><img src=\"" + imagePathName + "\"/> </a>";
    }
}
