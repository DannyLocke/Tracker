package com.ironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.HashMap;

public class Main {

    //to store users' twitter posts
    static HashMap<String, User> userHashMap = new HashMap<>();


    public static void main(String[] args) {

        Spark.init();

        //get() method to identify user and/or add new user to HashMap
        Spark.get (
                "/",

                ((request,response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = userHashMap.get(name);

                    HashMap n = new HashMap();

                    //if there's no user, home page to login
                    if(user == null) {
                        return new ModelAndView(n, "home.html");
                    }
                    //create new user/password/post
                    else {

                        n.put("loginName", user.name);
                        n.put("loginPassword", user.password);
                        n.put("createPost", user.twitterEntries);
                        return new ModelAndView(n, "post.html");
                    }
                }),
                new MustacheTemplateEngine()
        );//end Spark.get "/"

        Spark.post(
                "/login",
                ((request, response) -> {
                    Session session = request.session();
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("loginPassword");

                    //exception for no name or password
                    if(name == null || password == null){
                        throw new Exception("Please enter name and password.");
                    }

                    //create object with name & password
                    User user = userHashMap.get(name);
                    if(user == null){
                        user = new User(name, password);
                        userHashMap.put(name, user);
                        userHashMap.put(password,user);
                    }
                    else if (!user.password.equals(password)){
                        throw new Exception("Wrong password.");
                    }

                    session.attribute("loginName", name);
                    session.attribute("loginPassword", password);
                    response.redirect("/");
                    return "";
                })
        );//end Spark.post /login

        Spark.post(
                "/createPost",

                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");
                    User user = userHashMap.get(name);

                    if(user == null){
                        throw new Exception("Please log in first");
                    }

                    String text = request.queryParams("createPost");

                    //add post to logged in user
                    Twitter x = new Twitter(text);
                    user.twitterEntries.add(x);

                    response.redirect("/");
                    return "";
                })
        );//end Spark.post /createPost

        //edit post
        Spark.post(
                "/editPost",

                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");

                    User user = userHashMap.get(name);

                    String num = request.queryParams("num");
                    int x = Integer.parseInt(num);

                    //specifies which post to select and edit
                    user.twitterEntries.get(x - 1);
                    user.twitterEntries.remove(x - 1);

                    //repost edited post
                    String editPost = request.queryParams("editPost");
                    Twitter text = new Twitter(editPost);
                    user.twitterEntries.add(x - 1, text);


                    response.redirect("/");
                    return "";
                }
                ));//end Spark.post /editPost

        //delete post
        Spark.post(
                "/deletePost",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("loginName");

                    User user = userHashMap.get(name);

                    String deletePost = request.queryParams("deletePost");

                    int x = Integer.parseInt(deletePost);
                    user.twitterEntries.remove(x - 1);

                    response.redirect("/");
                    return "";
                })
        );//end Spark.post /deletePost

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );//end Spark /logout

    }//end main()

}//end class Main
