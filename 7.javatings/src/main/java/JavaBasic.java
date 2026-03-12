// 编码：UTF-8
public class JavaBasic {
    public static void main(String[] args) {
        // ========================================
        // 练习1 - 变量
        // ========================================
        System.out.println("========================================");
        System.out.println("练习1 - 变量");
        System.out.println("========================================");
        
        // 定义变量存储三科成绩
        int chinese = 86; // 语文成绩
        int math = 93; // 数学成绩
        int english = 79; // 英语成绩
        int sport = 88; // 体育成绩
        
        // 计算总分
        int totalScore = chinese + math + english + sport;
        // 计算平均分
        double averageScore = totalScore / 4.0;
        
        // 输出结果
        System.out.println("语文成绩: " + chinese);
        System.out.println("数学成绩: " + math);
        System.out.println("英语成绩: " + english);
        System.out.println("体育成绩: " + sport);
        System.out.println("总分: " + totalScore);
        System.out.println("平均分: " + averageScore);
        
        // ========================================
        // 练习2 - 条件判断
        // ========================================
        System.out.println("\n========================================");
        System.out.println("练习2 - 条件判断");
        System.out.println("========================================");
        
        // 根据平均分评定等级
        String level;
        if (averageScore >= 90) {
            level = "优秀";
        } else if (averageScore >= 80) {
            level = "良好";
        } else if (averageScore >= 70) {
            level = "中等";
        } else if (averageScore >= 60) {
            level = "及格";
        } else {
            level = "需要加油";
        }
        
        // 输出等级
        System.out.println("学生等级: " + level);
        
        // ========================================
        // 练习3 - 循环
        // ========================================
        System.out.println("\n========================================");
        System.out.println("练习3 - 循环（九九乘法表）");
        System.out.println("========================================");
        
        // 用for循环打印九九乘法表
        for (int i = 1; i <= 5; i++) { // 外层循环控制行数
            for (int j = 1; j <= i; j++) { // 内层循环控制每行的列数
                // 打印乘法表达式，使用制表符保持格式整齐
                System.out.print(j + " × " + i + " = " + (j * i) + "\t");
            }
            // 每行结束后换行
            System.out.println();
        }
    }
}