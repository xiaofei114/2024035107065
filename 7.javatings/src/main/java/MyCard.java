import java.util.Scanner;

public class MyCard {
    public static void main(String[] args) {
        // 创建Scanner对象，用于获取用户输入
        Scanner scanner = new Scanner(System.in);
        
        // 提示用户输入名字
        System.out.print("请输入名字: ");
        // 获取用户输入的名字
        String name = scanner.nextLine();
        
        // 提示用户输入年龄
        System.out.print("请输入年龄: ");
        // 获取用户输入的年龄，并转换为整数
        int age = scanner.nextInt();
        // 消费掉输入年龄后的换行符
        scanner.nextLine();
        
        // 提示用户输入爱好
        System.out.print("请输入爱好: ");
        // 获取用户输入的爱好
        String hobby = scanner.nextLine();
        
        // 关闭Scanner对象
        scanner.close();
        
        // 生成个人名片
        generateCard(name, age, hobby);
    }
    
    // 生成个人名片的方法
    private static void generateCard(String name, int age, String hobby) {
        // 计算边框宽度，确保足够容纳内容
        int width = 40;
        
        // 打印上边框
        printBorder(width);
        
        // 打印标题行
        printCentered("个人名片", width);
        
        // 打印分隔线
        printBorder(width);
        
        // 打印名字信息
        printInfoLine("名字: " + name, width);
        
        // 打印年龄信息
        printInfoLine("年龄: " + age, width);
        
        // 打印爱好信息
        printInfoLine("爱好: " + hobby, width);
        
        // 打印分隔线
        printBorder(width);
        
        // 根据年龄打印不同的问候语
        String greeting = getGreeting(age);
        printCentered(greeting, width);
        
        // 打印下边框
        printBorder(width);
    }
    
    // 打印边框线
    private static void printBorder(int width) {
        // 循环打印指定宽度的横线
        for (int i = 0; i < width; i++) {
            System.out.print("-");
        }
        // 打印换行
        System.out.println();
    }
    
    // 打印居中的文本
    private static void printCentered(String text, int width) {
        // 计算左边的空格数
        int padding = (width - text.length() - 2) / 2;
        // 打印左边的竖线和空格
        System.out.print("|");
        for (int i = 0; i < padding; i++) {
            System.out.print(" ");
        }
        // 打印文本
        System.out.print(text);
        // 计算右边的空格数，确保总宽度一致
        int rightPadding = width - text.length() - padding - 2;
        for (int i = 0; i < rightPadding; i++) {
            System.out.print(" ");
        }
        // 打印右边的竖线和换行
        System.out.println("|");
    }
    
    // 打印信息行
    private static void printInfoLine(String info, int width) {
        // 打印左边的竖线
        System.out.print("|");
        // 打印信息文本
        System.out.print(" " + info);
        // 计算右边的空格数，确保总宽度一致
        int padding = width - info.length() - 3;
        for (int i = 0; i < padding; i++) {
            System.out.print(" ");
        }
        // 打印右边的竖线和换行
        System.out.println("|");
    }
    
    // 根据年龄获取问候语
    private static String getGreeting(int age) {
        // 根据年龄范围返回不同的问候语
        if (age < 18) {
            return "你好，少年！未来可期！";
        } else if (age >= 18 && age <= 25) {
            return "你好，青年！加油拼搏！";
        } else {
            return "你好，前辈！请多指教！";
        }
    }
}