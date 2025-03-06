<%--
  Created by IntelliJ IDEA.
  User: 该用户以成仙
  Date: 2024/11/10
  Time: 13:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人笔记系统-登录</title>
    <link rel="stylesheet" href="./static/style.css">
</head>
<body>
<div class="container">
    <h1>登录</h1>
    <form action="/node03/login" method="post">
        <input type="text" name="username" placeholder="用户名" required>
        <input type="password" name="password" placeholder="密码" required>
        <button type="submit">登录</button>
    </form>
    <a href="register.jsp" class="link">还没有账号？注册</a>
</div>
</body>
</html>