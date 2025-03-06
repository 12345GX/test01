package com.gx.controller;

import com.gx.model.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/NoteServlet")
public class NoteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDAO dao = new UserDAO();
        HttpSession session = request.getSession();

        // 从 Session 中获取 user_id
        Object userIdObj = session.getAttribute("user_id");
        if (userIdObj == null) {
            response.sendRedirect("login.jsp"); // 如果用户未登录，重定向到登录页面
            return;
        }
        int userId = Integer.parseInt(userIdObj.toString()); // 获取 user_id

        // 获取 action 参数，默认是 "view"
        String action = request.getParameter("action");
        if (action == null) action = "view";

        // 获取 note_id 参数
        String noteIdParam = request.getParameter("note_id");
        Map<String, Object> note = null;


        try {
            if ("view".equals(action)) {
                // 查看笔记详情
                if (noteIdParam != null && !noteIdParam.isEmpty()) {
                    int noteId = Integer.parseInt(noteIdParam);
                    note = dao.getNoteDetail(noteId);

                    if (note == null) {
                        sendErrorMessage(request, response, "未找到指定的笔记", "error.jsp");
                        return;
                    }

                    // 增加浏览量
                    if (!dao.incrementViewCount(noteId)) {
                        System.out.println("增加浏览量失败，笔记ID: " + noteId);
                    }

                    // 将笔记数据传递到 JSP 页面
                    request.setAttribute("note", note);
                    request.setAttribute("action", "view");
                    request.setAttribute("author_id", note.get("author_id"));

                } else {
                    sendErrorMessage(request, response, "缺少笔记ID", "error.jsp");
                    return;
                }

            } else if ("edit".equals(action)) {
                // 编辑笔记页面
                if (noteIdParam != null && !noteIdParam.isEmpty()) {
                    int noteId = Integer.parseInt(noteIdParam);
                    note = dao.getNoteDetail(noteId);

                    if (note == null) {
                        sendErrorMessage(request, response, "未找到指定的笔记", "error.jsp");
                        return;
                    }

                    // 将笔记数据传递到 JSP 页面
                    request.setAttribute("note", note);
                    request.setAttribute("action", "edit");
                    request.setAttribute("author_id", note.get("author_id"));

                } else {
                    sendErrorMessage(request, response, "缺少笔记ID", "error.jsp");
                    return;
                }

            } else if ("delete".equals(action)) {
                // 删除笔记操作
                handleDeleteNoteByGet(request, response, dao);
                return;

            } else if ("incrementView".equals(action)) {
                // 处理增加浏览量的独立操作
                handleIncrementViewCount(request, response, dao);
                return;

            } else {
                sendErrorMessage(request, response, "未知的操作", "error.jsp");
                return;
            }

            // 转发到 JSP 页面
            request.getRequestDispatcher("note.jsp").forward(request, response);
            request.getRequestDispatcher("index.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            sendErrorMessage(request, response, "笔记ID格式错误", "error.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorMessage(request, response, "服务器内部错误", "error.jsp");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDAO dao = new UserDAO();
        HttpSession session = request.getSession();
        Object userIdObj = session.getAttribute("user_id");

        if (userIdObj == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int authorId = Integer.parseInt(userIdObj.toString());
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            handleDeleteNoteByGet(request, response, dao);
        } else if ("add".equals(action)) {
            handleAddNote(request, response, dao, authorId);
        } else if ("edit".equals(action)) {
            handleEditNote(request, response, dao, authorId);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的操作: " + action);
        }
    }

    private void handleAddNote(HttpServletRequest request, HttpServletResponse response, UserDAO dao, int authorId)
            throws ServletException, IOException {
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String categoryIdParam = request.getParameter("category_id");

        if (title == null || title.isEmpty() || content == null || content.isEmpty()) {
            sendErrorMessage(request, response, "标题和内容不能为空", "note.jsp?action=add");
            return;
        }

        Integer categoryId = null;
        if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdParam);
            } catch (NumberFormatException e) {
                sendErrorMessage(request, response, "分类ID格式错误", "note.jsp?action=add");
                return;
            }
        }

        if (dao.createNote(title, content, authorId, categoryId)) {
            response.sendRedirect("index.jsp");
        } else {
            sendErrorMessage(request, response, "新增笔记失败", "note.jsp?action=add");
        }
    }

    private void handleEditNote(HttpServletRequest request, HttpServletResponse response, UserDAO dao, int authorId)
            throws ServletException, IOException {
        String noteIdParam = request.getParameter("note_id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        // 调试日志
        System.out.println("note_id: " + noteIdParam);
        System.out.println("title: " + title);
        System.out.println("content: " + content);

        if (noteIdParam == null || noteIdParam.isEmpty() || title == null || title.isEmpty() || content == null || content.isEmpty()) {
            sendErrorMessage(request, response, "笔记ID、标题和内容不能为空", "note.jsp?action=edit&note_id=" + noteIdParam);
            return;
        }

        try {
            int noteId = Integer.parseInt(noteIdParam);
            if (dao.updateNote(noteId, title, content)) {
                response.sendRedirect("index.jsp");
            } else {
                sendErrorMessage(request, response, "编辑笔记失败", "note.jsp?action=edit&note_id=" + noteId);
            }
        } catch (NumberFormatException e) {
            sendErrorMessage(request, response, "笔记ID格式错误", "note.jsp?action=edit&note_id=" + noteIdParam);
        }
    }

    private void handleIncrementViewCount(HttpServletRequest request, HttpServletResponse response, UserDAO dao)
            throws IOException {
        String noteIdParam = request.getParameter("note_id");

        if (noteIdParam == null || noteIdParam.isEmpty()) {
            response.getWriter().write("缺少笔记ID");
            return;
        }

        try {
            int noteId = Integer.parseInt(noteIdParam);
            if (dao.incrementViewCount(noteId)) {
                response.getWriter().write("浏览量增加成功");
            } else {
                response.getWriter().write("浏览量增加失败");
            }
        } catch (NumberFormatException e) {
            response.getWriter().write("笔记ID格式错误");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("服务器内部错误");
        }
    }

    //删除笔记
    private void handleDeleteNoteByGet(HttpServletRequest request, HttpServletResponse response, UserDAO dao)
            throws ServletException, IOException {
        String noteIdParam = request.getParameter("note_id");
        HttpSession session = request.getSession();
        Object userIdObj = session.getAttribute("user_id");

        // 检查用户是否登录
        if (userIdObj == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 获取 authorId 和 noteId
        int authorId = Integer.parseInt(userIdObj.toString());
        System.out.println("GET 删除请求 - note_id: " + noteIdParam + ", author_id: " + authorId);

        if (noteIdParam == null || noteIdParam.isEmpty()) {
            sendErrorMessage(request, response, "笔记ID不能为空", "error.jsp");
            return;
        }

        try {
            int noteId = Integer.parseInt(noteIdParam);

            // 调用 DAO 方法进行删除操作
            boolean isDeleted = dao.deleteNote(noteId, authorId);
            System.out.println("删除结果: " + isDeleted);

            if (isDeleted) {
                System.out.println("笔记删除成功 - note_id: " + noteId);
                response.sendRedirect("index.jsp");
            } else {
                System.out.println("笔记删除失败 - note_id: " + noteId);
                sendErrorMessage(request, response, "删除笔记失败", "error.jsp");
            }
        } catch (NumberFormatException e) {
            sendErrorMessage(request, response, "笔记ID格式错误", "error.jsp");
        }
    }




    private void sendErrorMessage(HttpServletRequest request, HttpServletResponse response, String message, String redirectPath)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher(redirectPath).forward(request, response);
    }
}
