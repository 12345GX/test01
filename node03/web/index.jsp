<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.gx.model.DBConnection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人笔记系统</title>
    <link rel="stylesheet" href="./static/index.css">
</head>
<body>
<!-- 顶部导航条 -->
<div class="navbar">
    <div class="brand">个人笔记系统</div>
    <form action="<%= request.getContextPath() %>/search" method="get" class="search-box">
        <input type="text" name="query" placeholder="搜索笔记类型或标题..." required>
        <button type="submit" style="">搜索</button>
    </form>
    <div class="buttons">
        <a href="login.jsp">登录</a>
        <a href="register.jsp">注册</a>
        <a href="profile.jsp">个人</a>
        <a href="note.jsp?action=add" class="add-note-btn">新增文章</a>
    </div>
</div>

<!-- 主体内容 -->
<div class="content">
    <%
        boolean hasNotes = false; // 标志变量，表示是否有笔记
        try (Connection conn = DBConnection.getConnection()) {
            // 查询最新的10篇笔记
            String sql = "SELECT note_id, title, created_at, view_count ,author_id FROM notes ORDER BY created_at DESC LIMIT 10";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                // 遍历查询结果，生成文章列表
                while (rs.next()) {
                    hasNotes = true; // 如果有笔记，将标志设置为 true
                    int noteId = rs.getInt("note_id");
                    String title = rs.getString("title");
                    String createdAt = rs.getString("created_at");
                    int viewCount = rs.getInt("view_count");
                    String authorId = rs.getString("author_id");
    %>
    <!-- 单个文章显示 -->
    <div class="note" onclick="window.location.href='NoteServlet?action=view&note_id=<%= noteId %>'" style="cursor: pointer;">
        <h3><%= title %></h3>
        <p>发布时间: <%= createdAt %> | 浏览量: <%= viewCount %> | 作者ID: <%= authorId %></p>


        <form method="post" action="NoteServlet?action=delete" style="margin-top: 10px;">
            <input type="hidden" name="note_id" value="<%= noteId %>">
            <button type="submit"
                    style="
                background-color: #dc3545; /* 深红色 */
                color: white;
                border: none;
                border-radius: 5px;
                padding: 8px 15px;
                font-size: 14px;
                font-weight: bold;
                cursor: pointer;
                transition: all 0.3s ease;">
                删除
            </button>
        </form>

    </div>
    <%
            }
        }
    } catch (Exception e) {
        // 捕获并显示数据库加载失败的错误信息
    %>
    <div class="error">
        <p>无法加载笔记，请稍后再试。</p>
    </div>
    <%
            e.printStackTrace(); // 打印异常信息便于调试
        }

        // 如果没有笔记，显示提示信息
        if (!hasNotes) {
    %>
    <div class="note">
        <h3>暂无笔记</h3>
        <p>请点击页面顶部的“新增文章”按钮添加您的第一篇笔记。</p>
    </div>
    <%
        }
    %>
</div>


<!-- 页面底部 -->
<div class="footer">
    <p>© 2024 高昕个人笔记系统 | <a href="http://blog.csdn.net">联系我们</a></p>
</div>
</body>
</html>
