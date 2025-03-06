<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>笔记详情</title>
    <link rel="stylesheet" href="./static/note.css">
</head>
<body>
<%
    String action = request.getParameter("action");
    if (action == null || action.isEmpty()) action = "view";
    Map<String, Object> note = (Map<String, Object>) request.getAttribute("note");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>

<div class="navbar">
    <div class="brand">个人笔记系统</div>
    <div class="buttons">
        <a href="index.jsp" style="text-decoration: none; color: white; padding: 8px 15px; background: #495057; border-radius: 5px;">返回主页</a>
        <% if (note != null && "view".equals(action)) { %>
        <a href="NoteServlet?action=edit&note_id=<%= note.get("note_id") %>"
           style="text-decoration: none; color: white; padding: 8px 15px; background: #007bff; border-radius: 5px;">
            修改笔记
        </a>
        <% } %>
    </div>
</div>

<div class="content">
    <% if (errorMessage != null) { %>
    <div class="error">
        <p style="color: red;"><%= errorMessage %></p>
    </div>
    <% } %>

    <% if ("view".equals(action)) { %>
    <% if (note != null) { %>
    <div class="note-detail">
        <h2><%= note.get("title") %></h2>
        <p>发布时间: <%= note.get("created_at") %> | 浏览量: <%= note.get("view_count") %></p>
        <div class="note-content">
            <p><%= note.get("content") %></p>
        </div>
    </div>
    <% } else { %>
    <div class="error">
        <p>未找到笔记内容，请返回主页。</p>
    </div>
    <% } %>
    <% } else if ("add".equals(action) || "edit".equals(action)) { %>
    <div class="note-editor">
        <h3><%= "add".equals(action) ? "新增文章" : "编辑文章" %></h3>
        <form method="post" action="NoteServlet?action=<%= action %>">
            <% if ("edit".equals(action)) { %>
            <input type="hidden" name="note_id" value="<%= note != null ? note.get("note_id") : "" %>">
            <% } %>
            <div>
                <label for="title">标题：</label>
                <input type="text" id="title" name="title" value="<%= note != null ? note.get("title") : "" %>" required>
            </div>
            <div>
                <label for="content">内容：</label>
                <textarea id="content" name="content" rows="10" required><%= note != null ? note.get("content") : "" %></textarea>
            </div>
            <div style="margin-top: 10px;">
                <button type="submit" style="padding: 10px 20px; background-color: #28a745; color: white; border: none; border-radius: 5px; cursor: pointer;">
                    <%= "add".equals(action) ? "新增" : "保存" %>
                </button>
            </div>
        </form>
    </div>
    <% } %>
</div>

<div class="footer">
    <p>© 2024 高昕个人笔记系统 | <a href="http://blog.csdn.net">联系我们</a></p>
</div>
</body>
</html>
