package com.gx.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gx.model.DBConnection.getConnection;

public class UserDAO {

    // 检查用户名是否已存在
    public boolean isUsernameExists(String username) {
        String sql = "SELECT 1 FROM users2 WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // 如果有结果，说明用户名已存在
        } catch (Exception e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 注册用户
    public boolean registerUser(String username, String password, String email, String phone) {
        // 检查用户名是否已存在
        if (isUsernameExists(username)) {
            System.out.println("Username already exists: " + username);
            return false; // 用户名已存在，直接返回 false
        }

        String sql = "INSERT INTO users2 (username, password, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, phone);

            int rowsInserted = stmt.executeUpdate();
            System.out.println("Rows Inserted: " + rowsInserted); // 调试信息
            return rowsInserted > 0;

        } catch (Exception e) {
            System.err.println("Error during user registration: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 验证用户登录
    public boolean validateUser(String username, String password) {
        String sql = "SELECT * FROM users2 WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // 密码匹配时应使用加密后的密码

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.err.println("Error validating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Object> validateAndGetUser(String username, String password) {
        String sql = "SELECT * FROM users2 WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // 密码匹配时应使用加密后的密码

        } catch (Exception e) {
            System.err.println("Error validating user: " + e.getMessage());
            e.printStackTrace();
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // 将用户信息存储到 Map 中返回
                    Map<String, Object> user = new HashMap<>();
                    user.put("user_id", rs.getInt("user_id"));
                    user.put("username", rs.getString("username"));
                    user.put("email", rs.getString("email"));
                    user.put("phone", rs.getString("phone"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 如果验证失败或查询不到用户
    }

    // 根据用户名查询用户信息
    public ResultSet getUserByUsername(String username) {
        String sql = "SELECT email, phone, avatar_url, created_at FROM users2 WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            System.out.println("Executing query: " + sql + " with username=" + username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Query result: email=" + rs.getString("email") +
                        ", phone=" + rs.getString("phone") +
                        ", avatar_url=" + rs.getString("avatar_url") +
                        ", created_at=" + rs.getTimestamp("created_at"));
            } else {
                System.out.println("No user found for username=" + username);
            }
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAvatar(String username, String avatarUrl) {
        String sql = "UPDATE users2 SET avatar_url = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avatarUrl); // 设置头像 URL
            stmt.setString(2, username); // 设置用户名
            return stmt.executeUpdate() > 0; // 返回是否更新成功
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常信息
        }
        return false; // 更新失败时返回 false
    }

    public boolean updateUserProfile(String username, String email, String phone) {
        String sql = "UPDATE users2 SET email = ?, phone = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, username);
            return stmt.executeUpdate() > 0; // 返回是否更新成功
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 获取最新的笔记列表
    public List<Map<String, Object>> getLatestNotes() {
        String sql = "SELECT note_id, title, created_at, view_count FROM notes ORDER BY created_at DESC LIMIT 10";
        List<Map<String, Object>> notesList = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> note = new HashMap<>();
                note.put("note_id", rs.getInt("note_id"));
                note.put("title", rs.getString("title"));
                note.put("created_at", rs.getString("created_at"));
                note.put("view_count", rs.getInt("view_count"));
                notesList.add(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notesList;
    }

    // 获取单个笔记详情
    public Map<String, Object> getNoteDetail(int noteId) {
        String sql = "SELECT * FROM notes WHERE note_id = ?";


        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noteId);
            System.out.println("Executing query: " + sql + " with note_id=" + noteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> note = new HashMap<>();
                    note.put("note_id", rs.getInt("note_id"));
                    note.put("title", rs.getString("title"));
                    note.put("content", rs.getString("content"));
                    note.put("author_id", rs.getInt("author_id"));
                    note.put("created_at", rs.getString("created_at"));
                    note.put("view_count", rs.getInt("view_count"));
                    System.out.println("Note found: " + note);
                    return note;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("No note found for note_id=" + noteId);
        return null;
    }



    // 增加笔记浏览量
    public boolean incrementViewCount(int noteId) {
        String sql = "UPDATE notes SET view_count = view_count + 1 WHERE note_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noteId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 创建新笔记

    public boolean createNote(String title, String content, int authorId, Integer categoryId) {
        // 修改 SQL 语句，添加 author_id 和 category_id 字段
        String sql = "INSERT INTO notes (title, content, author_id, category_id, created_at, view_count) VALUES (?, ?, ?, ?, NOW(), 0)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, authorId); // 插入作者 ID
            if (categoryId != null) {
                stmt.setInt(4, categoryId); // 插入分类 ID（允许为空）
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // 返回插入是否成功
        } catch (SQLException e) {
            System.out.println("插入失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 更新笔记
    public boolean updateNote(int noteId, String title, String content) {
        String sql = "UPDATE notes SET title = ?, content = ? WHERE note_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setInt(3, noteId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //删除笔记
    public boolean deleteNote(int noteId, int authorId) {
        String sql = "DELETE FROM notes WHERE note_id = ? AND author_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noteId);
            stmt.setInt(2, authorId);

            System.out.println("Executing SQL: " + sql);
            System.out.println("Parameters: note_id=" + noteId + ", author_id=" + authorId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Map<String, Object>> searchNotes(String keyword) throws SQLException {
        String sql = "SELECT note_id, title, content, view_count, created_at FROM notes WHERE title LIKE ? OR content LIKE ?";
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword + "%";
            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> note = new HashMap<>();
                    note.put("note_id", rs.getInt("note_id"));
                    note.put("title", rs.getString("title"));
                    note.put("content", rs.getString("content"));
                    note.put("view_count", rs.getInt("view_count"));
                    note.put("created_at", rs.getTimestamp("created_at"));
                    results.add(note);
                }
            }
        }
        return results;
    }

}
