package com.example;

// 导入Java SQL包中的类，用于数据库操作
import java.sql.Connection;        // 数据库连接接口，表示与数据库的会话
import java.sql.DriverManager;     // 数据库驱动管理类，用于建立数据库连接
import java.sql.ResultSet;         // 结果集接口，封装了SQL查询返回的数据
import java.sql.SQLException;      // SQL异常类，处理数据库操作中的错误
import java.sql.Statement;         // SQL语句接口，用于执行静态SQL语句
import java.util.ArrayList;        // 动态数组类，用于存储查询到的学生信息
import java.util.List;             // 列表接口，定义了列表的基本操作

/**
 * 学生信息查询类
 * 该类实现了连接MySQL数据库、查询学生信息、格式化输出结果的功能
 */
public class StudentQuery {
    
    // ========== 数据库连接配置常量 ==========
    
    // 数据库驱动类名，MySQL 8.0+使用这个驱动类
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // 数据库连接URL，格式：jdbc:mysql://主机名:端口/数据库名?参数
    // useSSL=false：禁用SSL连接（本地开发环境）
    // serverTimezone=Asia/Shanghai：设置服务器时区为上海
    // allowPublicKeyRetrieval=true：允许公钥检索
    private static final String URL = "jdbc:mysql://localhost:3306/school_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    
    // 数据库用户名
    private static final String USERNAME = "root";
    
    // 数据库密码占位符，请在此处填入您的实际密码
    private static final String PASSWORD = "123456";
    
    // ========== 表格显示配置常量 ==========
    
    // 姓名字段的显示宽度（字符数）
    private static final int NAME_WIDTH = 10;
    // 年龄字段的显示宽度（字符数）
    private static final int AGE_WIDTH = 6;
    // 年级字段的显示宽度（字符数）
    private static final int CLASS_WIDTH = 10;
    
    /**
     * 程序主入口方法
     * @param args 命令行参数（本程序不使用）
     */
    public static void main(String[] args) {
        // 创建StudentQuery类的实例对象
        StudentQuery query = new StudentQuery();
        // 调用查询学生信息的方法
        query.queryStudents();
    }
    
    /**
     * 查询学生信息的主方法
     * 负责建立数据库连接、执行查询、显示结果、关闭资源
     */
    public void queryStudents() {
        // 声明数据库连接对象，用于后续关闭
        Connection connection = null;
        // 声明SQL语句对象，用于后续关闭
        Statement statement = null;
        // 声明结果集对象，用于后续关闭
        ResultSet resultSet = null;
        
        try {
            // ========== 步骤1：加载数据库驱动 ==========
            // 使用反射机制加载MySQL驱动类到内存中
            // 这是JDBC 4.0之前的必需步骤，4.0+可以自动加载，但显式加载更安全
            Class.forName(DRIVER);
            // 打印提示信息，表示驱动加载成功
            System.out.println("数据库驱动加载成功！");
            
            // ========== 步骤2：建立数据库连接 ==========
            // 使用DriverManager获取数据库连接
            // 传入参数：连接URL、用户名、密码
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // 打印提示信息，表示连接成功
            System.out.println("数据库连接成功！");
            
            // ========== 步骤3：创建SQL语句对象 ==========
            // 通过连接对象创建Statement实例，用于执行SQL语句
            // Statement适用于执行静态SQL语句（无参数的SQL）
            statement = connection.createStatement();
            
            // ========== 步骤4：定义并执行SQL查询语句 ==========
            // 定义查询SQL语句，从students表中查询所有学生的姓名、年龄、班级
            String sql = "SELECT name, age, class_name FROM students";
            // 执行查询，返回ResultSet结果集对象
            resultSet = statement.executeQuery(sql);
            
            // ========== 步骤5：处理查询结果 ==========
            // 创建列表用于存储所有查询到的学生信息
            List<Student> students = new ArrayList<>();
            
            // 使用while循环遍历结果集中的每一行数据
            // resultSet.next()方法将光标移动到下一行，如果有数据返回true
            while (resultSet.next()) {
                // 从当前行获取姓名字段的值
                // getString方法根据列名获取字符串类型的数据
                String name = resultSet.getString("name");
                // 从当前行获取年龄字段的值
                // getInt方法根据列名获取整数类型的数据
                int age = resultSet.getInt("age");
                // 从当前行获取班级字段的值
                String className = resultSet.getString("class_name");
                
                // 创建Student对象，封装学生信息
                Student student = new Student(name, age, className);
                // 将学生对象添加到列表中
                students.add(student);
            }
            
            // ========== 步骤6：显示查询结果 ==========
            // 调用方法以表格形式打印学生信息
            printTable(students);
            // 打印统计信息，显示总共查询到的学生数量
            System.out.println("\n共查询到 " + students.size() + " 个学生");
            
        } catch (ClassNotFoundException e) {
            // 捕获驱动类未找到的异常
            // 这通常意味着MySQL驱动jar包没有正确添加到项目中
            System.err.println("错误：数据库驱动类未找到！");
            // 打印详细的异常堆栈信息，便于调试
            e.printStackTrace();
        } catch (SQLException e) {
            // 捕获SQL异常
            // 这通常是由于连接信息错误、SQL语句错误或数据库问题导致的
            System.err.println("错误：数据库操作失败！");
            // 打印详细的异常堆栈信息
            e.printStackTrace();
        } finally {
            // ========== 步骤7：关闭数据库资源 ==========
            // finally块确保无论是否发生异常，资源都会被关闭
            // 按照打开顺序的逆序关闭：结果集 -> 语句 -> 连接
            
            // 关闭结果集对象
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    // 忽略关闭时的异常
                    e.printStackTrace();
                }
            }
            
            // 关闭语句对象
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // 忽略关闭时的异常
                    e.printStackTrace();
                }
            }
            
            // 关闭连接对象
            if (connection != null) {
                try {
                    connection.close();
                    // 打印提示信息，表示连接已关闭
                    System.out.println("数据库连接已关闭。");
                } catch (SQLException e) {
                    // 忽略关闭时的异常
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 以表格形式打印学生信息列表
     * 使用Unicode制表符绘制美观的表格边框
     * @param students 学生信息列表
     */
    private void printTable(List<Student> students) {
        // 打印表格顶部边框线
        // ┌是左上角，┬是中间上连接，┐是右上角
        // ─是水平线
        System.out.println("┌" + repeat("─", NAME_WIDTH) + "┬" + repeat("─", AGE_WIDTH) + "┬" + repeat("─", CLASS_WIDTH) + "┐");
        
        // 打印表头行
        // │是垂直线，用于分隔列
        System.out.println("│" + center("姓名", NAME_WIDTH) + "│" + center("年龄", AGE_WIDTH) + "│" + center("年级", CLASS_WIDTH) + "│");
        
        // 打印表头与数据之间的分隔线
        // ├是左中连接，┼是中间十字连接，┤是右中连接
        System.out.println("├" + repeat("─", NAME_WIDTH) + "┼" + repeat("─", AGE_WIDTH) + "┼" + repeat("─", CLASS_WIDTH) + "┤");
        
        // 遍历学生列表，打印每一行数据
        for (Student student : students) {
            // 打印当前学生的信息行
            System.out.println("│" + center(student.getName(), NAME_WIDTH) + "│" + center(String.valueOf(student.getAge()), AGE_WIDTH) + "│" + center(student.getClassName(), CLASS_WIDTH) + "│");
        }
        
        // 打印表格底部边框线
        // └是左下角，┴是中间下连接，┘是右下角
        System.out.println("└" + repeat("─", NAME_WIDTH) + "┴" + repeat("─", AGE_WIDTH) + "┴" + repeat("─", CLASS_WIDTH) + "┘");
    }
    
    /**
     * 重复字符串指定次数
     * 用于生成表格边框线
     * @param str 要重复的字符串
     * @param count 重复次数
     * @return 重复后的字符串
     */
    private String repeat(String str, int count) {
        // 创建StringBuilder对象，用于高效拼接字符串
        StringBuilder sb = new StringBuilder();
        // 循环count次，每次追加str
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        // 将StringBuilder转换为String并返回
        return sb.toString();
    }
    
    /**
     * 将字符串居中对齐到指定宽度
     * 如果字符串长度超过指定宽度，则截取字符串
     * @param str 原始字符串
     * @param width 目标宽度
     * @return 居中对齐后的字符串
     */
    private String center(String str, int width) {
        // 如果字符串为null，则使用空字符串
        if (str == null) {
            str = "";
        }
        
        // 如果字符串长度超过指定宽度，则截取前width个字符
        if (str.length() > width) {
            return str.substring(0, width);
        }
        
        // 计算需要填充的总空格数
        int totalPadding = width - str.length();
        // 计算左侧空格数（总空格数的一半，向下取整）
        int leftPadding = totalPadding / 2;
        // 计算右侧空格数（剩余的空格数）
        int rightPadding = totalPadding - leftPadding;
        
        // 使用StringBuilder拼接左侧空格、原始字符串、右侧空格
        StringBuilder sb = new StringBuilder();
        // 追加左侧空格
        for (int i = 0; i < leftPadding; i++) {
            sb.append(" ");
        }
        // 追加原始字符串
        sb.append(str);
        // 追加右侧空格
        for (int i = 0; i < rightPadding; i++) {
            sb.append(" ");
        }
        
        // 返回居中对齐后的字符串
        return sb.toString();
    }
    
    /**
     * 学生信息内部类
     * 用于封装单个学生的姓名、年龄、班级信息
     */
    private static class Student {
        // 学生姓名，使用private修饰实现封装
        private String name;
        // 学生年龄
        private int age;
        // 学生班级
        private String className;
        
        /**
         * 构造方法，用于创建Student对象
         * @param name 学生姓名
         * @param age 学生年龄
         * @param className 学生班级
         */
        public Student(String name, int age, String className) {
            // 使用this关键字区分成员变量和参数
            this.name = name;
            this.age = age;
            this.className = className;
        }
        
        /**
         * 获取学生姓名
         * @return 姓名
         */
        public String getName() {
            return name;
        }
        
        /**
         * 获取学生年龄
         * @return 年龄
         */
        public int getAge() {
            return age;
        }
        
        /**
         * 获取学生班级
         * @return 班级
         */
        public String getClassName() {
            return className;
        }
    }
}
