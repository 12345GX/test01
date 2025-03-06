package com.gx.controller;

import com.gx.model.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;

@WebServlet("/upload-avatar")
@MultipartConfig
public class UploadAvatarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = (String) req.getSession().getAttribute("username");
        if (username == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        // 获取上传文件
        Part filePart = req.getPart("avatar");
        if (filePart != null && filePart.getSize() > 0) {
            // 设置上传路径
            String uploadPath = getServletContext().getRealPath("/") + "uploads/avatars";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 保存文件
            String fileName = username + "_" + System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
            filePart.write(uploadPath + File.separator + fileName);

            // 保存头像路径到数据库
            String avatarUrl = "uploads/avatars/" + fileName;
            UserDAO userDAO = new UserDAO();
            if (userDAO.updateAvatar(username, avatarUrl)) {
                req.getSession().setAttribute("avatarUrl", avatarUrl); // 更新 session
                resp.sendRedirect("profile");
            } else {
                resp.getWriter().write("<script>alert('头像更新失败，请重试！'); window.history.back();</script>");
            }
        } else {
            resp.getWriter().write("<script>alert('未选择文件，请重新上传！'); window.history.back();</script>");
        }
    }
}
