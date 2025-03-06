<%--
  Created by IntelliJ IDEA.
  User: 该用户以成仙
  Date: 2024/11/20
  Time: 21:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>搜索结果</title>
    <link rel="stylesheet" href="./static/search.css">
</head>
<body>
<div class="navbar">
    <div class="brand">个人笔记系统</div>
    <a href="index.jsp">返回主页</a>
</div>

<div class="content">
    <h2>搜索结果：<%= request.getAttribute("query") %></h2>
    <%
        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) request.getAttribute("searchResults");
        if (searchResults == null || searchResults.isEmpty()) {
    %>
    <p>未找到符合条件的笔记。</p>
    <% } else { %>
    <ul>
        <% for (Map<String, Object> note : searchResults) { %>
        <li>
            <h3><a href="NoteServlet?action=view&note_id=<%= note.get("note_id") %>">
                <%= note.get("title") %></a></h3>
            <p><%= note.get("content") %></p>
        </li>
        <% } %>
    </ul>
    <% } %>
</div>

<div class="footer">
    <p>© 2024 高昕个人笔记系统 | <a href="http://blog.csdn.net">联系我们</a></p>
</div>
</body>
</html>
