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
 * 学生信息高级查询类
 * 该类实现了多种不同条件的学生信息查询功能
 */
public class StudentQueryAdvanced {
    
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
    
    // 数据库密码
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
        // 创建StudentQueryAdvanced类的实例对象
        StudentQueryAdvanced query = new StudentQueryAdvanced();
        
        // 调用各种查询方法
        System.out.println("=== 1. 只查询'高一'的学生 ===");
        query.querySeniorOneStudents();
        
        System.out.println("\n=== 2. 查询年龄大于17岁的学生 ===");
        query.queryStudentsOlderThan17();
        
        System.out.println("\n=== 3. 按年龄从小到大排序显示所有学生 ===");
        query.queryStudentsOrderByAge();
        
        System.out.println("\n=== 4. 只显示学生的姓名和年级（不显示年龄） ===");
        query.queryStudentNameAndClass();
    }
    
    /**
     * 只查询"高一"的学生
     * 使用LIKE关键字匹配班级名称中包含"高一"的记录
     */
    public void querySeniorOneStudents() {
        // 声明数据库连接对象，用于后续关闭
        Connection connection = null;
        // 声明SQL语句对象，用于后续关闭
        Statement statement = null;
        // 声明结果集对象，用于后续关闭
        ResultSet resultSet = null;
        
        try {
            // 加载数据库驱动
            Class.forName(DRIVER);
            // 建立数据库连接
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // 创建SQL语句对象
            statement = connection.createStatement();
            
            // 定义查询SQL语句，只查询班级名称包含"高一"的学生
            // LIKE '%高一%' 表示匹配包含"高一"的任意位置
            String sql = "SELECT name, age, class_name FROM students WHERE class_name LIKE '%高一%'";
            // 执行查询，返回ResultSet结果集对象
            resultSet = statement.executeQuery(sql);
            
            // 处理查询结果
            List<Student> students = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String className = resultSet.getString("class_name");
                
                Student student = new Student(name, age, className);
                students.add(student);
            }
            
            // 显示查询结果
            printTable(students);
            System.out.println("\n共查询到 " + students.size() + " 个高一学生");
            
        } catch (ClassNotFoundException e) {
            System.err.println("错误：数据库驱动类未找到！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("错误：数据库操作失败！");
            e.printStackTrace();
        } finally {
            // 关闭数据库资源
            closeResources(resultSet, statement, connection);
        }
    }
    
    /**
     * 查询年龄大于17岁的学生
     * 使用WHERE子句的大于(>)运算符筛选年龄
     */
    public void queryStudentsOlderThan17() {
        // 声明数据库连接对象，用于后续关闭
        Connection connection = null;
        // 声明SQL语句对象，用于后续关闭
        Statement statement = null;
        // 声明结果集对象，用于后续关闭
        ResultSet resultSet = null;
        
        try {
            // 加载数据库驱动
            Class.forName(DRIVER);
            // 建立数据库连接
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // 创建SQL语句对象
            statement = connection.createStatement();
            
            // 定义查询SQL语句，只查询年龄大于17的学生
            // WHERE age > 17 表示筛选年龄大于17的记录
            String sql = "SELECT name, age, class_name FROM students WHERE age > 17";
            // 执行查询，返回ResultSet结果集对象
            resultSet = statement.executeQuery(sql);
            
            // 处理查询结果
            List<Student> students = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String className = resultSet.getString("class_name");
                
                Student student = new Student(name, age, className);
                students.add(student);
            }
            
            // 显示查询结果
            printTable(students);
            System.out.println("\n共查询到 " + students.size() + " 个年龄大于17岁的学生");
            
        } catch (ClassNotFoundException e) {
            System.err.println("错误：数据库驱动类未找到！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("错误：数据库操作失败！");
            e.printStackTrace();
        } finally {
            // 关闭数据库资源
            closeResources(resultSet, statement, connection);
        }
    }
    
    /**
     * 按年龄从小到大排序显示所有学生
     * 使用ORDER BY子句对年龄字段进行升序排序
     */
    public void queryStudentsOrderByAge() {
        // 声明数据库连接对象，用于后续关闭
        Connection connection = null;
        // 声明SQL语句对象，用于后续关闭
        Statement statement = null;
        // 声明结果集对象，用于后续关闭
        ResultSet resultSet = null;
        
        try {
            // 加载数据库驱动
            Class.forName(DRIVER);
            // 建立数据库连接
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // 创建SQL语句对象
            statement = connection.createStatement();
            
            // 定义查询SQL语句，按年龄从小到大排序
            // ORDER BY age ASC 表示按age字段升序排列（ASC可以省略，默认就是升序）
            String sql = "SELECT name, age, class_name FROM students ORDER BY age ASC";
            // 执行查询，返回ResultSet结果集对象
            resultSet = statement.executeQuery(sql);
            
            // 处理查询结果
            List<Student> students = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String className = resultSet.getString("class_name");
                
                Student student = new Student(name, age, className);
                students.add(student);
            }
            
            // 显示查询结果
            printTable(students);
            System.out.println("\n共查询到 " + students.size() + " 个学生（按年龄从小到大排序）");
            
        } catch (ClassNotFoundException e) {
            System.err.println("错误：数据库驱动类未找到！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("错误：数据库操作失败！");
            e.printStackTrace();
        } finally {
            // 关闭数据库资源
            closeResources(resultSet, statement, connection);
        }
    }
    
    /**
     * 只显示学生的姓名和年级（不显示年龄）
     * 在SELECT语句中只指定需要的字段
     */
    public void queryStudentNameAndClass() {
        // 声明数据库连接对象，用于后续关闭
        Connection connection = null;
        // 声明SQL语句对象，用于后续关闭
        Statement statement = null;
        // 声明结果集对象，用于后续关闭
        ResultSet resultSet = null;
        
        try {
            // 加载数据库驱动
            Class.forName(DRIVER);
            // 建立数据库连接
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // 创建SQL语句对象
            statement = connection.createStatement();
            
            // 定义查询SQL语句，只查询姓名和班级字段
            // SELECT name, class_name 表示只获取这两个字段的数据
            String sql = "SELECT name, class_name FROM students";
            // 执行查询，返回ResultSet结果集对象
            resultSet = statement.executeQuery(sql);
            
            // 处理查询结果
            List<StudentNameClass> students = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String className = resultSet.getString("class_name");
                
                StudentNameClass student = new StudentNameClass(name, className);
                students.add(student);
            }
            
            // 显示查询结果（只显示姓名和年级）
            printNameClassTable(students);
            System.out.println("\n共查询到 " + students.size() + " 个学生（只显示姓名和年级）");
            
        } catch (ClassNotFoundException e) {
            System.err.println("错误：数据库驱动类未找到！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("错误：数据库操作失败！");
            e.printStackTrace();
        } finally {
            // 关闭数据库资源
            closeResources(resultSet, statement, connection);
        }
    }
    
    /**
     * 关闭数据库资源的工具方法
     * 按照打开顺序的逆序关闭：结果集 -> 语句 -> 连接
     * @param resultSet 结果集对象
     * @param statement 语句对象
     * @param connection 连接对象
     */
    private void closeResources(ResultSet resultSet, Statement statement, Connection connection) {
        // 关闭结果集对象
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // 关闭语句对象
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // 关闭连接对象
        if (connection != null) {
            try {
                connection.close();
                System.out.println("数据库连接已关闭。");
            } catch (SQLException e) {
                e.printStackTrace();
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
        System.out.println("┌" + repeat("─", NAME_WIDTH) + "┬" + repeat("─", AGE_WIDTH) + "┬" + repeat("─", CLASS_WIDTH) + "┐");
        
        // 打印表头行
        System.out.println("│" + center("姓名", NAME_WIDTH) + "│" + center("年龄", AGE_WIDTH) + "│" + center("年级", CLASS_WIDTH) + "│");
        
        // 打印表头与数据之间的分隔线
        System.out.println("├" + repeat("─", NAME_WIDTH) + "┼" + repeat("─", AGE_WIDTH) + "┼" + repeat("─", CLASS_WIDTH) + "┤");
        
        // 遍历学生列表，打印每一行数据
        for (Student student : students) {
            // 打印当前学生的信息行
            System.out.println("│" + center(student.getName(), NAME_WIDTH) + "│" + center(String.valueOf(student.getAge()), AGE_WIDTH) + "│" + center(student.getClassName(), CLASS_WIDTH) + "│");
        }
        
        // 打印表格底部边框线
        System.out.println("└" + repeat("─", NAME_WIDTH) + "┴" + repeat("─", AGE_WIDTH) + "┴" + repeat("─", CLASS_WIDTH) + "┘");
    }
    
    /**
     * 以表格形式打印学生姓名和年级列表（不显示年龄）
     * @param students 学生姓名和年级信息列表
     */
    private void printNameClassTable(List<StudentNameClass> students) {
        // 打印表格顶部边框线
        System.out.println("┌" + repeat("─", NAME_WIDTH) + "┬" + repeat("─", CLASS_WIDTH) + "┐");
        
        // 打印表头行
        System.out.println("│" + center("姓名", NAME_WIDTH) + "│" + center("年级", CLASS_WIDTH) + "│");
        
        // 打印表头与数据之间的分隔线
        System.out.println("├" + repeat("─", NAME_WIDTH) + "┼" + repeat("─", CLASS_WIDTH) + "┤");
        
        // 遍历学生列表，打印每一行数据
        for (StudentNameClass student : students) {
            // 打印当前学生的信息行
            System.out.println("│" + center(student.getName(), NAME_WIDTH) + "│" + center(student.getClassName(), CLASS_WIDTH) + "│");
        }
        
        // 打印表格底部边框线
        System.out.println("└" + repeat("─", NAME_WIDTH) + "┴" + repeat("─", CLASS_WIDTH) + "┘");
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
    
    /**
     * 学生姓名和班级信息内部类
     * 用于封装单个学生的姓名和班级信息（不包含年龄）
     */
    private static class StudentNameClass {
        // 学生姓名
        private String name;
        // 学生班级
        private String className;
        
        /**
         * 构造方法，用于创建StudentNameClass对象
         * @param name 学生姓名
         * @param className 学生班级
         */
        public StudentNameClass(String name, String className) {
            this.name = name;
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
         * 获取学生班级
         * @return 班级
         */
        public String getClassName() {
            return className;
        }
    }
}
