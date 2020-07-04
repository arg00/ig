/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.au.cc.gallery;

import edu.au.cc.gallery.tools.UserAdmin;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import org.apache.commons.lang3.StringUtils;

import static spark.Spark.*;
import spark.Request;
import spark.Response;

import spark.ModelAndView;

import spark.template.handlebars.HandlebarsTemplateEngine;

public class App {
    public String getGreeting() {
	return "Hi!";
    }
    
    public static ArrayList<HashMap<String, String>> getUserData(UserAdmin ua) throws Exception {
		ArrayList<String> users = ua.getUsers();
		HashMap<String, Object> usersData = new HashMap<String, Object>();
		ArrayList<HashMap<String,String>> allUserVals = new ArrayList<HashMap<String, String>>();
		for (String username : users) {
			HashMap<String, String> userInfo = new HashMap<String, String>();
			ArrayList<String> userVals = ua.getUserInfo(username);
			userInfo.put("username", username);
			userInfo.put("password", userVals.get(1));
			userInfo.put("full_name", userVals.get(2));
			usersData.put(username, userInfo);
			allUserVals.add(userInfo);
		}
		return allUserVals;
    }

    public static void main(String[] args) throws Exception {
	/*
	String portString = System.getenv("JETTY_PORT");
	if (portString == null || portString.equals(""))
	    port(5000);
	else
	    port(Integer.parseInt(portString));
	*/
	port(8888);
	
		
	UserAdmin ua = new UserAdmin();
	//DB db = new DB();
	//db.initDB();

	// init users map
	/*
	ArrayList<String> users = ua.getUsers();
	HashMap<String, Object> usersData = new HashMap<String, Object>();
	ArrayList<HashMap<String,String>> allUserVals = new ArrayList<HashMap<String, String>>();
	for (String username : users) {
		HashMap<String, String> userInfo = new HashMap<String, String>();
		ArrayList<String> userVals = ua.getUserInfo(username);
		userInfo.put("username", username);
		userInfo.put("password", userVals.get(1));
		userInfo.put("full_name", userVals.get(2));
		usersData.put(username, userInfo);
		allUserVals.add(userInfo);
	}
	*/

	get("/admin", (req, res) -> {
		if ((!checkAuthenticated(req, res)) || (!checkAdmin(req, res))) {
		    return "";
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("users", getUserData(ua));
		return new HandlebarsTemplateEngine()
			.render(new ModelAndView(model, "admin.hbs"));
	});

	get("/admin/createUser", (req, res) -> {
	        if ((!checkAuthenticated(req, res)) || (!checkAdmin(req, res))) {
		    return "";
		}
		Map<String, Object> model = new HashMap<String, Object>();
		
		return new HandlebarsTemplateEngine()
			.render(new ModelAndView(model, "createUser.hbs"));
	});

	get("admin/addUser", (req, res) -> {
		if ((!checkAuthenticated(req, res)) || (!checkAdmin(req, res))) {
		    return "";
		}
	       String uname = req.queryParams("uname");
       		String pwd = req.queryParams("pwd");
 		String fname = req.queryParams("fname");
		if (!StringUtils.isAlphanumeric(uname)) {
		    return "Username can only contain letters and numbers. <a href=\"../admin\">Return to admin page.</a>"; 
		}
	     	else if (ua.addUser(uname, pwd, fname)) {
		        res.redirect("/admin");
		        return "";
		        //return "User " + uname + " created. <a href=\"../admin\">Return to admin page.</a>";
		}
		else
			return "Couldn't create " + uname + ". User already exists. <a href=\"../admin\">Return to admin page.</a>";
	});				

	get("/admin/deleteUser", (req, res) -> {
		if ((!checkAuthenticated(req, res)) || (!checkAdmin(req, res))) {
		    return "";
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("uname", req.queryParams("uname"));
		return new HandlebarsTemplateEngine()
			.render(new ModelAndView(model, "deleteUser.hbs"));
	});

	get("/admin/delete", (req, res) -> {
		if ((!checkAuthenticated(req, res)) || (!checkAdmin(req, res))) {
		    return "";
		}
		String uname = req.queryParams("uname");
		ua.deleteUser(uname);
		res.redirect("/admin");
		//return "User " + uname + " deleted. <a href=\"../admin\">Return to admin page.</a>";
		return "";
	});

	get("/admin/editUser", (req, res) -> {
		if ((!checkAuthenticated(req, res)) || (!checkAdmin(req, res))) {
		    return "";
		}
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("uname", req.queryParams("uname"));
		return new HandlebarsTemplateEngine()
			.render(new ModelAndView(model, "editUser.hbs"));
	});

	get("/admin/edit", (req, res) -> {
		if ((!checkAuthenticated(req, res)) || (!checkAdmin(req, res))) {
		    return "";
		}
		String uname = req.queryParams("uname");
		String pwd = req.queryParams("pwd");
		String fname = req.queryParams("fname");
		ua.editUser(uname, pwd, fname);	
		res.redirect("/admin");
		return "";
		//return "User " + uname + " updated. <a href=\"../admin\">Return to admin page.</a>";
	});

	get("/addSessionAttributes", (req, res) -> {
		String key = req.queryParams("key");
		String value = req.queryParams("value");
		req.session().attribute(key, value);
		return "Added key: " + key + ", value: " + value;    
	});

	get("/debugSession", (req, res) -> {
		StringBuffer sb = new StringBuffer();
		for (String key: req.session().attributes()) {
		    sb.append(key+"->"+req.session().attribute(key)+"<br/>");
		}
		return sb.toString();    
	});

       
        get("/login", (req, resp) -> {
		if (req.session().attribute("authenticated") == null ||
		    !req.session().attribute("authenticated").equals("true")) {
		    Map<String, Object> model = new HashMap<String, Object>();
		    return new HandlebarsTemplateEngine()
			.render(new ModelAndView(model, "login.hbs"));
		}
		resp.redirect("/");
		    return "";
	});


	get("/accessDenied", (req, resp) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		return new HandlebarsTemplateEngine()
			.render(new ModelAndView(model, "accessDenied.hbs"));
	});


	/*
	post("/uploadToS3", (req, resp) -> {
		String imagesPath = "/home/ec2-user/ig/src/main/resources/images";
		String imagePathName = "";

		req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("imagesPath"));
		Part filePart = req.raw().getPart("myfile");

		try (InputStream inputStream = filePart.getInputStream()) {
              imagePathName = imagesPath + filePart.getSubmittedFileName();
              OutputStream outputStream = new FileOutputStream(imagePathName);
              IOUtils.copy(inputStream, outputStream);
              outputStream.close();
		}

		catch (Exception e) {
              System.out.println(e);
              e.printStackTrace();
              return "Failed to upload file but made it to upload method!";
          }

          return imagePathName;


	});
	*/

	addUploadRoutes();
	addViewRoutes();
	addMainRoutes();
    }

    private static String home(Request req, Response resp) {
	checkAuthenticated(req, resp);
	Map<String, Object> model = new HashMap<String, Object>();
		return new HandlebarsTemplateEngine()
			.render(new ModelAndView(model, "home.hbs"));
    }

    private static String upload(Request req, Response resp) {
	checkAuthenticated(req, resp);
	Map<String, Object> model = new HashMap<String, Object>();
		return new HandlebarsTemplateEngine()
			.render(new ModelAndView(model, "upload.hbs"));
    }

    private static String view(Request req, Response resp) {
	checkAuthenticated(req, resp);
	Map<String, Object> model = new HashMap<String, Object>();
	String username = req.session().attribute("username");
	String fullName = req.session().attribute("fullName");

	try {
	    UserAdmin ua = new UserAdmin();
	    ArrayList<String> imageNames = ua.getUsersImages(username);
	    HashMap<String, String> imageURLs = getUsersImageURLs(username, imageNames);
	    model.put("imageURLs", imageURLs);
	    model.put("imageNames", imageNames);
	} catch (Exception e) {
	    return "Error fetching user's images.";
	}

	model.put("full_name", fullName);
	model.put("username", username);
		return new HandlebarsTemplateEngine()
			.render(new ModelAndView(model, "view.hbs"));
    }

    public static HashMap<String, String> getUsersImageURLs(String username, ArrayList<String> imgNames) throws Exception {
	HashMap<String, String> usersImageURLs = new HashMap<String, String>();
	String s3RsrcPrefix = "https://s3-us-west-1.amazonaws.com/edu.au.cc.arg0055.image-gallery/images/";
       
	    for (String imgName : imgNames) {
		usersImageURLs.put(imgName, (s3RsrcPrefix + username + "/" + imgName));
	    }
      
	return usersImageURLs;
    }

    public static String attemptLogin(Request req, Response resp) {
	String uname = req.queryParams("uname");
	String enteredPwd = req.queryParams("pwd");
	try {
	    UserAdmin ua = new UserAdmin();
	    ArrayList<String> userInfo = ua.getUserInfo(uname);
	    String usersPwd = userInfo.get(1);
	    if (enteredPwd.equals(usersPwd)) {
		req.session().attribute("username", uname);
		req.session().attribute("fullName", userInfo.get(2));
		req.session().attribute("admin", "false");
		req.session().attribute("authenticated", "true");
		resp.redirect("/");
	    } else {
		return "Password for " + uname + " incorrect. <button><a href=\"/login\">Try again</a></button>";
	    }
	    return "";
	} catch (Exception e) {
	    return "No account with username: " + uname + ". <button><a href=\"/login\">Try again</a></button>";
	}
    }

    public static String deleteImage(Request req, Response resp) {
	String imgName = req.queryParams("imgName");
	String username = req.queryParams("username");
	S3 s3client = new S3();
	s3client.connect();
	if (s3client.deleteObject(username, imgName)) {
	    System.out.println("deleted on s3");
	} else {}

	try {
	    UserAdmin ua = new UserAdmin();
	    if (ua.deleteImage(username, imgName)) {
		return "Image deleted!";
	    } else return "Failed to delete image.";
	    
	} catch (Exception e) {
	    return "Failed to delete image. <a href=\"/view\">Back</a>";
	}
     }

    public static String uploadTest(Request req, Response resp) {
	return "";
    }

    
    public static boolean checkAuthenticated(Request req, Response resp) {
	if (req.session().attribute("authenticated") == null ||
	    !req.session().attribute("authenticated").equals("true")) {
	        resp.redirect("/login");
		return false;
	}
	return true;
     }

    public static boolean checkAdmin(Request req, Response resp) {
	if (req.session().attribute("admin") == null ||
	    !req.session().attribute("admin").equals("true")) {
	    resp.redirect("/accessDenied");
	    return false;
	}
	return true;
     }

    public static void addUploadRoutes() {
	get("/upload", (req, res) -> upload(req, res));
	post("/uploadImage", (req, res) -> Upload.uploadToS3(req, res));
    }

    public static void addViewRoutes() {
	get("/view", (req, resp) -> view(req, resp));
	post("/deleteImage", (req, resp) -> deleteImage(req, resp));
    }

    public static void addMainRoutes() {
	get("/", (req, resp) -> home(req, resp));
	post("/login", (req, resp) -> attemptLogin(req, resp));
    }
}
