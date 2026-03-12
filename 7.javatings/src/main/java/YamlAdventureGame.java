import org.yaml.snakeyaml.Yaml;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Scanner;

// 编码：UTF-8
public class YamlAdventureGame {
    // 战斗方法
    private static boolean fightMonster(Map<String, Object> monster, int playerHealth, Scanner scanner) {
        int monsterHealth = (Integer) monster.get("health");
        int monsterAttack = (Integer) monster.get("attack");
        
        System.out.println("战斗开始！");
        System.out.println(monster.get("name") + "的生命值：" + monsterHealth);
        System.out.println("你的生命值：" + playerHealth);
        
        while (true) {
            // 玩家回合
            System.out.println("\n你的回合：");
            System.out.println("1. 攻击");
            System.out.println("2. 使用物品");
            System.out.println("3. 逃跑");
            System.out.print("请选择：");
            int choice = scanner.nextInt();
            
            if (choice == 1) {
                // 攻击
                int damage = (int) (Math.random() * 20) + 10; // 随机伤害10-30
                monsterHealth -= damage;
                System.out.println("你攻击了" + monster.get("name") + "，造成了" + damage + "点伤害！");
                System.out.println(monster.get("name") + "的生命值：" + monsterHealth);
                
                // 检查怪物是否死亡
                if (monsterHealth <= 0) {
                    return true; // 战斗胜利
                }
            } else if (choice == 2) {
                // 使用物品（这里简化处理，实际应该调用物品使用逻辑）
                System.out.println("你使用了物品，但在战斗中无法使用物品。");
            } else if (choice == 3) {
                // 逃跑
                int escapeChance = (int) (Math.random() * 100);
                if (escapeChance > 50) {
                    System.out.println("你成功逃跑了！");
                    return false; // 战斗失败，但玩家存活
                } else {
                    System.out.println("逃跑失败！");
                }
            } else {
                System.out.println("输入错误，请重新选择！");
                continue;
            }
            
            // 怪物回合
            System.out.println("\n" + monster.get("name") + "的回合：");
            int damage = (int) (Math.random() * monsterAttack) + (monsterAttack / 2); // 随机伤害
            playerHealth -= damage;
            System.out.println(monster.get("name") + "攻击了你，造成了" + damage + "点伤害！");
            System.out.println("你的生命值：" + playerHealth);
            
            // 检查玩家是否死亡
            if (playerHealth <= 0) {
                return false; // 战斗失败
            }
        }
    }

    public static void main(String[] args) {
        // 创建Scanner对象，用于接收玩家输入
        Scanner scanner = new Scanner(System.in);
        
        // 初始化生命值
        int health = 100;
        
        // 初始化物品库存
        List<String> inventory = new ArrayList<>();
        
        try {
            // 加载YAML配置文件，使用UTF-8编码
            Yaml yaml = new Yaml();
            InputStreamReader reader = new InputStreamReader(
                new FileInputStream("game.yaml"),
                StandardCharsets.UTF_8
            );
            Map<String, Object> gameConfig = yaml.load(reader);
            
            // 获取起始场景ID
            String currentSceneId = (String) gameConfig.get("startId");
            
            // 获取所有场景配置
            Map<String, Object> scenes = (Map<String, Object>) gameConfig.get("scenes");
            
            // 获取物品定义
            Map<String, Object> items = (Map<String, Object>) gameConfig.get("items");
            
            // 获取怪物定义
            Map<String, Object> monsters = (Map<String, Object>) gameConfig.get("monsters");
            
            // 游戏主循环
            while (true) {
                // 获取当前场景配置
                Map<String, Object> currentScene = (Map<String, Object>) scenes.get(currentSceneId);
                
                // 打印场景描述
                System.out.println("========================================");
                System.out.println((String) currentScene.get("description"));
                System.out.println("当前生命值：" + health);
                
                // 显示物品库存
                System.out.println("当前物品：" + (inventory.isEmpty() ? "无" : inventory));
                
                // 检查是否有选择，如果没有选择则结束游戏
                if (!currentScene.containsKey("choices")) {
                    break;
                }
                
                // 检查是否有奖励
                if (currentScene.containsKey("reward")) {
                    System.out.println("获得奖励：" + currentScene.get("reward"));
                }
                
                // 检查是否有物品奖励
                if (currentScene.containsKey("items")) {
                    List<String> sceneItems = (List<String>) currentScene.get("items");
                    for (String itemId : sceneItems) {
                        inventory.add(itemId);
                        Map<String, Object> item = (Map<String, Object>) items.get(itemId);
                        System.out.println("获得物品：" + item.get("name"));
                    }
                }
                
                // 检查生命值变化
                if (currentScene.containsKey("healthChange")) {
                    int healthChange = (Integer) currentScene.get("healthChange");
                    health += healthChange;
                    if (health < 0) health = 0;
                    if (health > 100) health = 100;
                    System.out.println("生命值变化：" + (healthChange >= 0 ? "+" : "") + healthChange);
                    System.out.println("当前生命值：" + health);
                    
                    // 检查生命值是否为0
                    if (health <= 0) {
                        System.out.println("你已经死亡，游戏结束！");
                        break;
                    }
                }
                
                // 检查是否有怪物
                if (currentScene.containsKey("monster")) {
                    String monsterId = (String) currentScene.get("monster");
                    Map<String, Object> monster = (Map<String, Object>) monsters.get(monsterId);
                    System.out.println("\n你遇到了一只" + monster.get("name") + "！");
                    System.out.println("怪物描述：" + monster.get("description"));
                    
                    // 开始战斗
                    boolean battleWon = fightMonster(monster, health, scanner);
                    
                    // 根据战斗结果处理
                    if (battleWon) {
                        System.out.println("你成功击败了" + monster.get("name") + "！");
                        // 战斗胜利后可能获得奖励
                        if (currentScene.containsKey("reward")) {
                            System.out.println("获得奖励：" + currentScene.get("reward"));
                        }
                        if (currentScene.containsKey("items")) {
                            List<String> sceneItems = (List<String>) currentScene.get("items");
                            for (String itemId : sceneItems) {
                                inventory.add(itemId);
                                Map<String, Object> item = (Map<String, Object>) items.get(itemId);
                                System.out.println("获得物品：" + item.get("name"));
                            }
                        }
                    } else {
                        System.out.println("你被" + monster.get("name") + "击败了！");
                        health = 0;
                        System.out.println("你已经死亡，游戏结束！");
                        break;
                    }
                }
                
                // 获取选择列表
                List<Map<String, Object>> choices = (List<Map<String, Object>>) currentScene.get("choices");
                
                // 添加使用物品的选项（如果有物品）
                if (!inventory.isEmpty()) {
                    Map<String, Object> useItemChoice = new java.util.HashMap<>();
                    useItemChoice.put("id", "use_item");
                    useItemChoice.put("text", "使用物品");
                    useItemChoice.put("nextScene", currentSceneId);
                    choices.add(useItemChoice);
                }
                
                // 打印选择
                System.out.println("\n请选择：");
                for (int i = 0; i < choices.size(); i++) {
                    Map<String, Object> choice = choices.get(i);
                    System.out.println((i + 1) + ". " + choice.get("text"));
                }
                
                // 接收玩家输入
                System.out.print("请输入你的选择（1-" + choices.size() + "）：");
                int choiceIndex = scanner.nextInt() - 1;
                
                // 检查输入是否有效
                if (choiceIndex >= 0 && choiceIndex < choices.size()) {
                    // 获取选择的下一个场景ID
                    Map<String, Object> selectedChoice = choices.get(choiceIndex);
                    String choiceId = (String) selectedChoice.get("id");
                    
                    // 处理使用物品的选项
                    if ("use_item".equals(choiceId)) {
                        // 显示物品列表
                        System.out.println("\n请选择要使用的物品：");
                        for (int i = 0; i < inventory.size(); i++) {
                            String itemId = inventory.get(i);
                            Map<String, Object> item = (Map<String, Object>) items.get(itemId);
                            System.out.println((i + 1) + ". " + item.get("name") + " - " + item.get("description"));
                        }
                        
                        // 接收物品选择
                        System.out.print("请输入物品编号（1-" + inventory.size() + "）：");
                        int itemIndex = scanner.nextInt() - 1;
                        
                        // 使用物品
                        if (itemIndex >= 0 && itemIndex < inventory.size()) {
                            String itemId = inventory.get(itemIndex);
                            Map<String, Object> item = (Map<String, Object>) items.get(itemId);
                            
                            // 处理不同物品的效果
                            if ("healing_potion".equals(itemId)) {
                                // 治疗药水：恢复30点生命值
                                health += 30;
                                if (health > 100) health = 100;
                                System.out.println("你使用了治疗药水，恢复了30点生命值！");
                                System.out.println("当前生命值：" + health);
                            } else if ("magic_scroll".equals(itemId)) {
                                // 魔法卷轴：随机效果
                                int random = (int) (Math.random() * 3);
                                if (random == 0) {
                                    health += 20;
                                    if (health > 100) health = 100;
                                    System.out.println("魔法卷轴释放了治愈魔法，你恢复了20点生命值！");
                                } else if (random == 1) {
                                    System.out.println("魔法卷轴释放了照明魔法，周围变得明亮起来！");
                                } else {
                                    System.out.println("魔法卷轴释放了防护魔法，你感到更加安全！");
                                }
                                System.out.println("当前生命值：" + health);
                            } else if ("torch".equals(itemId)) {
                                // 火把：照亮周围
                                System.out.println("你点燃了火把，周围变得明亮起来！");
                            } else if ("map".equals(itemId)) {
                                // 地图：显示当前位置
                                System.out.println("你查看了地图，确认了当前位置。");
                            } else if ("key".equals(itemId)) {
                                // 钥匙：提示可以打开某些门
                                System.out.println("你检查了钥匙，它看起来可以打开某些门。");
                            } else if ("strength_potion".equals(itemId)) {
                                // 力量药水：增加力量
                                System.out.println("你喝了力量药水，感到充满了力量！在战斗中会更有效。");
                            } else if ("speed_potion".equals(itemId)) {
                                // 速度药水：增加速度
                                System.out.println("你喝了速度药水，感到速度大增！逃跑时会更有效。");
                            } else if ("luck_potion".equals(itemId)) {
                                // 幸运药水：增加幸运
                                System.out.println("你喝了幸运药水，感到幸运女神在眷顾你！会获得更好的奖励。");
                            } else if ("shield".equals(itemId)) {
                                // 盾牌：提供防护
                                System.out.println("你装备了盾牌，感到更加安全！受到的伤害会减少。");
                            } else if ("sword".equals(itemId)) {
                                // 剑：增加战斗成功率
                                System.out.println("你装备了剑，感到战斗力大增！战斗的成功率会提高。");
                            } else if ("food".equals(itemId)) {
                                // 食物：恢复少量生命值
                                health += 10;
                                if (health > 100) health = 100;
                                System.out.println("你吃了食物，恢复了10点生命值！");
                                System.out.println("当前生命值：" + health);
                            } else if ("amulet".equals(itemId)) {
                                // 护身符：提供特殊能力
                                System.out.println("你戴上了护身符，感到一股神秘的力量环绕着你！");
                            } else if ("potion_of_invisibility".equals(itemId)) {
                                // 隐形药水：短暂隐形
                                System.out.println("你喝了隐形药水，身体变得透明！可以避开敌人的注意。");
                            }
                            
                            // 从库存中移除使用的物品
                            inventory.remove(itemIndex);
                            System.out.println("物品已使用。");
                        } else {
                            System.out.println("输入错误，返回选择。");
                        }
                    } else {
                        // 普通选择
                        currentSceneId = (String) selectedChoice.get("nextScene");
                    }
                } else {
                    System.out.println("输入错误，请重新选择！");
                }
            }
            
            // 关闭文件读取器
            reader.close();
        } catch (IOException e) {
            System.out.println("加载游戏配置失败：" + e.getMessage());
        }
        
        // 关闭Scanner
        scanner.close();
    }
}