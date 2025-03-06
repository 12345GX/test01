package com.gx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.gx.model.UserDAO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Timestamp;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = (String) req.getSession().getAttribute("username");
        if (username == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            ResultSet userInfo = userDAO.getUserByUsername(username);
            if (userInfo != null && userInfo.next()) {
                String email = userInfo.getString("email");
                String phone = userInfo.getString("phone");
                String avatarUrl = userInfo.getString("avatar_url");
                Timestamp createdAt = userInfo.getTimestamp("created_at");

                System.out.println("Fetched user info: email=" + email + ", phone=" + phone +
                        ", avatar_url=" + avatarUrl + ", created_at=" + createdAt);

                req.setAttribute("email", email != null ? email : "未知");
                req.setAttribute("phone", phone != null ? phone : "未知");
                req.setAttribute("avatarUrl", avatarUrl != null ? avatarUrl : "default-avatar.jpg");
                req.setAttribute("created_at", createdAt);
            } else {
                System.out.println("No user information found for username=" + username);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "服务器内部错误，请稍后重试！");
        }

        req.getRequestDispatcher("profile.jsp").forward(req, resp);
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取用户输入的数据
        String username = (String) req.getSession().getAttribute("username");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        if (username == null) {
            resp.sendRedirect("login.jsp"); // 如果未登录，跳转到登录页面
            return;
        }

        try {
            // 调用 DAO 更新用户信息
            boolean updated = userDAO.updateUserProfile(username, email, phone);
            if (updated) {
                resp.sendRedirect("profile"); // 更新成功，跳转回个人信息页面
            } else {
                req.setAttribute("error", "更新失败，请稍后重试！");
                req.getRequestDispatcher("error.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "服务器错误，请稍后重试！");
            req.getRequestDispatcher("error.jsp").forward(req, resp);
        }
    }
}
