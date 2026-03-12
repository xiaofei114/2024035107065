import java.util.Scanner;

// 编码：UTF-8
public class AdventureGame {
    public static void main(String[] args) {
        // 创建Scanner对象，用于接收玩家输入
        Scanner scanner = new Scanner(System.in);

        // 初始化生命值
        int health = 100;

        // 游戏开始，介绍背景
        System.out.println("========================================");
        System.out.println("文字冒险小游戏");
        System.out.println("========================================");
        System.out.println("你是一名勇敢的探险者，来到了一个神秘的洞穴入口。");
        System.out.println("洞穴内传来奇怪的声音，你需要决定接下来的行动。");
        System.out.println("当前生命值：" + health);

        // 第一个选择：进入左边或右边通道
        System.out.println("\n第一个选择：");
        System.out.println("1. 进入左边的通道");
        System.out.println("2. 进入右边的通道");
        System.out.print("请输入你的选择（1或2）：");

        // 接收玩家输入
        int firstChoice = scanner.nextInt();

        // 根据第一个选择进入不同的分支
        if (firstChoice == 1) {
            // 左边通道分支
            leftPath(scanner, health);
        } else if (firstChoice == 2) {
            // 右边通道分支
            rightPath(scanner, health);
        } else {
            // 输入错误，游戏结束
            System.out.println("输入错误，游戏结束！");
        }

        // 关闭Scanner
        scanner.close();
    }

    // 左边通道分支
    private static void leftPath(Scanner scanner, int health) {
        System.out.println("\n你进入了左边的通道，通道越来越窄，光线越来越暗。");
        System.out.println("突然，你遇到了一只巨大的龙！它正趴在一堆金币上睡觉。");
        System.out.println("龙被你的脚步声惊醒，睁开了眼睛，露出锋利的牙齿。");
        System.out.println("当前生命值：" + health);

        // 选择：战斗或逃跑
        System.out.println("\n你会怎么做？");
        System.out.println("1. 战斗");
        System.out.println("2. 逃跑");
        System.out.print("请输入你的选择（1或2）：");

        // 接收玩家输入
        int choice = scanner.nextInt();

        if (choice == 1) {
            // 战斗过程
            System.out.println("\n你拔出武器，勇敢地向龙冲去！");
            System.out.println("龙向你喷出火焰，你受到了伤害！");
            // 扣血
            health -= 30;
            System.out.println("当前生命值：" + health);
            
            // 检查生命值
            if (health <= 0) {
                System.out.println("你被龙的火焰烧死了！");
                System.out.println("【坏结局】：你成为了龙的晚餐。");
                return;
            }
            
            System.out.println("你奋力反击，击中了龙的要害！");
            System.out.println("经过激烈的战斗，你成功击败了龙！");
            // 恢复生命值
            health += 50;
            if (health > 100) health = 100; // 生命值上限100
            System.out.println("你获得了龙的宝藏，恢复了生命值！");
            System.out.println("当前生命值：" + health);
            System.out.println("【好结局】：你成为了传说中的屠龙勇士！");
        } else if (choice == 2) {
            // 逃跑结局
            System.out.println("\n你转身就跑，龙在后面紧追不舍。");
            System.out.println("你在逃跑过程中被龙的尾巴扫中，受到了伤害！");
            // 扣血
            health -= 10;
            System.out.println("当前生命值：" + health);
            
            // 检查生命值
            if (health <= 0) {
                System.out.println("你在逃跑过程中因为伤势过重而死亡！");
                System.out.println("【坏结局】：你成为了龙的晚餐。");
                return;
            }
            
            System.out.println("你拼命奔跑，终于逃出了洞穴，但龙的恐怖记忆将伴随你一生。");
            System.out.println("【坏结局】：你虽然保住了性命，但成为了一个胆小的探险者。");
        } else {
            // 输入错误
            System.out.println("输入错误，你在犹豫中被龙吃掉了！");
            System.out.println("【坏结局】：你成为了龙的晚餐。");
        }
    }

    // 右边通道分支
    private static void rightPath(Scanner scanner, int health) {
        System.out.println("\n你进入了右边的通道，通道宽敞明亮，墙壁上镶嵌着发光的宝石。");
        System.out.println("在通道的尽头，你发现了一个闪闪发光的宝箱。");
        System.out.println("宝箱上有一个复杂的锁，似乎需要决定是否打开它。");
        System.out.println("当前生命值：" + health);

        // 选择：打开或不打开
        System.out.println("\n你会怎么做？");
        System.out.println("1. 打开宝箱");
        System.out.println("2. 不打开宝箱");
        System.out.print("请输入你的选择（1或2）：");

        // 接收玩家输入
        int choice = scanner.nextInt();

        if (choice == 1) {
            // 打开宝箱后
            System.out.println("\n你小心地打开了宝箱，里面装满了金币和珠宝！");
            System.out.println("在宝箱的最底层，你发现了一把闪亮的钥匙和几颗珍贵的宝石。");
            System.out.println("宝箱旁边有一扇神秘的门，看起来需要钥匙才能打开。");

            // 新的选择
            System.out.println("\n你会怎么做？");
            System.out.println("1. 用宝箱里的钥匙开门");
            System.out.println("2. 拿走宝石离开");
            System.out.print("请输入你的选择（1或2）：");

            // 接收玩家输入
            int keyChoice = scanner.nextInt();

            if (keyChoice == 1) {
                // 用钥匙开门结局
                System.out.println("\n你用钥匙打开了神秘的门，门后是一个更大的宝藏室！");
                System.out.println("里面有无数的金币、宝石和魔法物品，你成为了世界上最富有的人。");
                // 恢复生命值
                health += 30;
                if (health > 100) health = 100; // 生命值上限100
                System.out.println("你获得了宝藏，恢复了生命值！");
                System.out.println("当前生命值：" + health);
                System.out.println("【好结局】：你发现了更多宝藏，成为了传说中的寻宝大师！");
            } else if (keyChoice == 2) {
                // 拿走宝石离开结局
                System.out.println("\n你拿走了几颗宝石，决定离开洞穴。");
                System.out.println("但当你走到洞口时，触发了洞穴的陷阱，洞口崩塌了！");
                // 扣血
                health -= 50;
                System.out.println("当前生命值：" + health);
                
                // 检查生命值
                if (health <= 0) {
                    System.out.println("你被崩塌的石块砸中，不幸身亡！");
                    System.out.println("【坏结局】：你永远留在了洞穴中。");
                    return;
                }
                
                System.out.println("你勉强爬出了洞穴，但失去了所有的宝石。");
                System.out.println("【坏结局】：你虽然保住了性命，但一无所获。");
            } else {
                // 输入错误
                System.out.println("输入错误，你在犹豫中触发了宝箱的陷阱！");
                // 扣血
                health -= 40;
                System.out.println("当前生命值：" + health);
                
                // 检查生命值
                if (health <= 0) {
                    System.out.println("你被陷阱击中，不幸身亡！");
                    System.out.println("【坏结局】：你永远留在了洞穴中。");
                    return;
                }
                
                System.out.println("你勉强躲过了陷阱，但受了伤。");
                System.out.println("【坏结局】：你带着伤痛离开了洞穴。");
            }
        } else if (choice == 2) {
            // 不打开宝箱结局
            System.out.println("\n你决定不打开宝箱，转身离开了通道。");
            System.out.println("虽然你没有获得宝藏，但你保住了好奇心，这也许是件好事。");
            System.out.println("当前生命值：" + health);
            System.out.println("【坏结局】：你错过了改变命运的机会。");
        } else {
            // 输入错误
            System.out.println("输入错误，你在犹豫中触发了宝箱的陷阱！");
            // 扣血
            health -= 40;
            System.out.println("当前生命值：" + health);
            
            // 检查生命值
            if (health <= 0) {
                System.out.println("你被陷阱击中，不幸身亡！");
                System.out.println("【坏结局】：你永远留在了洞穴中。");
                return;
            }
            
            System.out.println("你勉强躲过了陷阱，但受了伤。");
            System.out.println("【坏结局】：你带着伤痛离开了洞穴。");
        }
    }
}