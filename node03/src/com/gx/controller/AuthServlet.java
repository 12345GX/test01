package com.gx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.gx.model.UserDAO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet({"/register", "/login"})
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置请求和响应的字符编码
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        String path = req.getServletPath();

        // 路由到对应的处理逻辑
        if ("/register".equals(path)) {
            handleRegister(req, resp);
        } else if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else {
            resp.getWriter().write("<script>alert('无效的请求！'); window.history.back();</script>");
        }
    }

    // 处理注册逻辑
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");

        // 检查空值
        if (isInvalidInput(username, password, email, phone)) {
            resp.getWriter().write("<script>alert('所有字段均为必填项，请填写完整！'); window.history.back();</script>");
            return;
        }

        try {
            // 调用 DAO 方法进行注册
            if (userDAO.registerUser(username, password, email, phone)) {
                resp.getWriter().write(
                        "<script>" +
                                "alert('注册成功！即将跳转到登录页面');" +
                                "window.location.href = 'login.jsp';" +
                                "</script>"
                );
            } else {
                resp.getWriter().write(
                        "<script>" +
                                "alert('注册失败，可能是用户名已存在或数据错误！');" +
                                "window.history.back();" +
                                "</script>"
                );
            }
        } catch (Exception e) {
            logError(e, "注册失败");
            resp.getWriter().write("<script>alert('服务器内部错误，请稍后重试！'); window.history.back();</script>");
        }
    }

    // 处理登录逻辑
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 检查空值
        if (isInvalidInput(username, password)) {
            resp.getWriter().write("<script>alert('用户名和密码不能为空！'); window.history.back();</script>");
            return;
        }

        try {
            // 验证用户名和密码，返回用户数据
            Map<String, Object> user = userDAO.validateAndGetUser(username, password);

            if (user != null) {
                // 登录成功，将用户数据存储到 Session 中
                HttpSession session = req.getSession();
                session.setAttribute("user_id", user.get("user_id"));
                session.setAttribute("username", user.get("username"));
                session.setAttribute("email", user.get("email"));
                session.setAttribute("phone", user.get("phone"));

                System.out.println("用户登录成功，Session ID: " + session.getId());
                System.out.println("用户信息: " + user);

                // 跳转到个人主页
                resp.sendRedirect("profile.jsp");
            } else {
                // 登录失败逻辑
                resp.getWriter().write(
                        "<script>" +
                                "alert('用户名或密码错误，请重新输入！');" +
                                "window.history.back();" +
                                "</script>"
                );
            }
        } catch (Exception e) {
            logError(e, "登录失败");
            resp.getWriter().write("<script>alert('服务器内部错误，请稍后重试！'); window.history.back();</script>");
        }
    }

    // 检查输入是否为空
    private boolean isInvalidInput(String... inputs) {
        for (String input : inputs) {
            if (input == null || input.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // 记录错误日志
    private void logError(Exception e, String action) {
        System.err.println(action + "时发生错误：" + e.getMessage());
        e.printStackTrace();
    }
}
