<%@ page import="java.sql.Timestamp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // 检查登录状态
    String username = (String) session.getAttribute("username");
    String email = (String) session.getAttribute("email");
    String phone = (String) session.getAttribute("phone");
    String avatarUrl = (String) session.getAttribute("avatarUrl");
    Timestamp created_at = (Timestamp) session.getAttribute("created_at");
    if (username == null) {
        response.sendRedirect("login.jsp"); // 如果未登录，跳转到登录页面
        return;
    }

    // 如果信息获取失败，使用默认值
    if (email == null || phone == null) {
        email = "未知";
        phone = "未知";
        avatarUrl = "default-avatar.jpg";
    }
%>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人信息页面</title>
    <link rel="stylesheet" href="./static/profile.css">
</head>
<body>
<!-- 顶部导航条 -->
<div class="navbar">
    <div class="brand">个人笔记系统</div>
    <div class="buttons">
        <a href="index.jsp">首页</a>
        <a href="login.jsp">退出登录</a>
    </div>
</div>

<!-- 主体内容 -->
<div class="content">
    <!-- 用户头像及上传功能 -->
    <div class="profile-header">
        <form id="avatar-form" action="upload-avatar" method="post" enctype="multipart/form-data">
            <label for="avatar-upload">
                <img src="<%= request.getContextPath() + "/" + avatarUrl %>" alt="用户头像" title="点击更换头像">
                <span class="upload-overlay">点击上传头像</span>
            </label>
            <input type="file" id="avatar-upload" name="avatar" accept="image/*" onchange="document.getElementById('avatar-form').submit();">
        </form>
        <div>
            <h2>用户名: <%= username %></h2>
            <p>注册日期: <%= created_at %></p>
        </div>
    </div>

    <div class="profile-info">
        <h3>基本信息</h3>
        <!-- 显示用户信息按钮，点击后刷新当前用户信息 -->
        <div class="info-item">
            <span>用户名:</span> <span id="username-display"><%= username %></span>
        </div>
        <div class="info-item">
            <span>邮箱:</span> <span id="email-display"><%= email %></span>
        </div>
        <div class="info-item">
            <span>电话:</span> <span id="phone-display"><%= phone %></span>
        </div>
    </div>

    <!-- 修改信息表单 -->
    <div class="actions">
        <form action="/node03/profile" method="post">
            <label for="email">修改邮箱:</label>
            <input type="email" id="email" name="email" value="<%= email %>" required>

            <label for="phone">修改电话:</label>
            <input type="tel" id="phone" name="phone" value="<%= phone %>" required>

            <button type="submit">保存修改</button>
        </form>
    </div>
</div>


<!-- 页脚 -->
<div class="footer">
    <p>© 2024 高昕个人笔记系统 | <a href="http://blog.csdn.net">联系我们</a></p>
</div>
</body>
</html>
