package com.theironyard.controllers;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utilities.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by michaelmernin on 1/3/17.
 */

@RestController //when using with Json, returns objects as json
public class IronGramController {
    @Autowired
    UserRepository users;

    @Autowired
    PhotoRepository photos;

    Server dbui = null; //database user interface, (can be named anything)

    @PostConstruct
    public void init() throws SQLException {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String username, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findFirstByName(username);
        if (user == null) {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        } else if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong password");
        }

        session.setAttribute("username", username);
        response.sendRedirect("/");
        return user;
    }

    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        session.invalidate();
        response.sendRedirect("/");
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return users.findFirstByName(username);
    }

    @RequestMapping("/upload")
    public Photo upload(HttpSession session, HttpServletResponse response, String receiver, MultipartFile photo, Photo phTime) throws Exception {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not Logged in.");
        }

        User senderUser = users.findFirstByName(username);
        User receiverUser = users.findFirstByName(receiver);

        if (receiverUser == null) {
            throw new Exception("Receiver name doesn't exist.");
        }

        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo p = new Photo();
        p.setSender(senderUser);
        p.setRecipient(receiverUser);
        p.setFilename(photoFile.getName());
        photos.save(p);

        response.sendRedirect("/");

        return p;
    }

    @RequestMapping("/photos")
    public List<Photo> showPhotos(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("not logged in");
        }

        List<Photo> allPhotos = (List<Photo>) photos.findAll();
        for (Photo photo : allPhotos) {
            if (photo.getPhTime() == null) {
                photo.setExists((long) 10);
            }
        }

        for (Photo photo : allPhotos) {
            if (photo.getPhTime() == null) {
                photo.setExists(LocalDateTime.now());
                photos.save(photo);
            }

            if (LocalDateTime.now().isAfter(photo.getPhTime().plusSeconds(photo.getExists()))) {
                File deletedPhoto = new File("public/" + photo.getFilename());
                deletedPhoto.delete();
                photos.delete(photo);
            }


            User user = users.findFirstByName(username);
            return photos.findByRecipient(user);
        }

        @RequestMapping("/publicPhotos")
        public List<Photo> ablePhoto (HttpSession session, User username) throws Exception {
            String user = (String) session.getAttribute("username");
            if (username == null) {
                throw new Exception("not logged in");
            }


        }


    }
}
