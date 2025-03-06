<%--
  Created by IntelliJ IDEA.
  User: 该用户以成仙
  Date: 2024/11/7
  Time: 18:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人笔记系统-注册</title>
    <link rel="stylesheet" href="./static/style.css">
</head>
<body>
<div class="container">
    <h1>注册</h1>
    <form action="/node03/register" method="post">
        <input type="text" name="username" placeholder="用户名" required>
        <input type="email" name="email" placeholder="邮箱" required>
        <input type="tel" name="phone" placeholder="电话" required>
        <input type="password" name="password" placeholder="密码" required>
        <button type="submit">注册</button>
    </form>
    <a href="login.jsp" class="link">已经有账号了？登录</a>
</div>
</body>
</html>