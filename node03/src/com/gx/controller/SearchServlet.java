package com.gx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.gx.model.UserDAO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO(); // 数据库访问类

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取搜索关键字
        String query = req.getParameter("query");

        // 检查搜索关键字是否为空
        if (query == null || query.trim().isEmpty()) {
            req.setAttribute("errorMessage", "搜索关键字不能为空！");
            req.getRequestDispatcher("error.jsp").forward(req, resp);
            return;
        }

        try {
            // 调用 DAO 执行查询
            List<Map<String, Object>> searchResults = userDAO.searchNotes(query);

            // 将结果传递到 JSP 页面
            req.setAttribute("searchResults", searchResults);
            req.setAttribute("query", query);

            // 转发到搜索结果页面
            req.getRequestDispatcher("search-results.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace(); // 输出详细错误到控制台
            req.setAttribute("errorMessage", "服务器内部错误：" + e.getMessage());
            req.getRequestDispatcher("error.jsp").forward(req, resp);
        }
    }
}



