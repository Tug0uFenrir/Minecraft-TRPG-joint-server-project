# RPG核心插件-Dev-V0.0.1

本项目是一个基于 Bukkit API 的 Minecraft RPG 插件，旨在为服务器提供完整的 DND 风格角色扮演体验。核心功能包括角色创建系统、属性系统、职业系统、技能系统和装备系统，还未完全完成，现在只完成了GUI类的编程，有待继续。

## 功能架构

``````mermaid
graph TD
    A[RPG 核心插件] --> B[角色创建系统]
    A --> C[属性系统]
    A --> D[职业系统]
    A --> E[技能系统]
    A --> F[装备系统]
    A --> G[骰子插件集成]
    
    B --> B1[职业选择]
    B --> B2[出身选择]
    B --> B3[属性分配]
    
    C --> C1[六维属性]
    C --> C2[属性计算]
    C --> C3[升级系统]
    
    D --> D1[基础职业]
    D --> D2[进阶职业]
    D --> D3[职业特性]
    
    E --> E1[技能学习]
    E --> E2[技能执行]
    E --> E3[技能冷却]
    
    F --> F1[装备识别]
    F --> F2[套装效果]
    F --> F3[强化系统]
    
    G --> G1[随机判定]
    G --> G2[属性检定]
    G --> G3[战斗计算]
``````

## 核心系统设计

``````mermaid
sequenceDiagram
    participant Player
    participant Server
    participant RPGPlugin
    
    Player->>Server: 首次加入服务器
    Server->>RPGPlugin: 触发PlayerJoinEvent
    RPGPlugin->>Player: 打开职业选择GUI
    Player->>RPGPlugin: 选择职业
    RPGPlugin->>Player: 打开出身选择GUI
    Player->>RPGPlugin: 选择出身
    RPGPlugin->>Player: 打开属性分配GUI
    Player->>RPGPlugin: 分配属性点
    RPGPlugin->>RPGPlugin: 保存玩家数据
    RPGPlugin->>Player: 给予初始装备/技能
``````

### 属性系统

| 属性 | 作用     | 计算公式                                     |
| :--- | :------- | :------------------------------------------- |
| 力量 | 近战伤害 | `近战伤害 = 基础伤害 + (力量 * 0.5)`         |
| 敏捷 | 技能冷却 | `冷却时间 = max(1, 基础冷却 - (敏捷 * 0.1))` |
| 体质 | 生命值   | `最大生命值 = 20 + (体质 * 2)`               |
| 感知 | 闪避率   | `闪避率 = 感知 * 1%`                         |
| 智力 | 法力值   | `最大法力值 = 智力 * 5`                      |
| 魅力 | 暴击率   | `暴击率 = 魅力 * 1%`                         |

### 职业系统

采用yaml配置文件来自定义相关职业的技能、基础数值、物资等：

如：

``````yaml
warrior:
  name: "战士"
  primary-attribute: "strength"
  starting-skills:
    - "warrior_passive"
    - "warrior_q"
    - "warrior_f"
    - "warrior_c"
  starting-items:
    - "IRON_SWORD:1"
``````

`warrior`代表职业id

`primary-attribute`为核心属性

`starting-skills`为具有的初始技能

` starting-items`为该职业具有的基础物资

**进阶职业：**

基础职业达到指定等级后可以解锁相关的职业分支，可以学习新技能但不替换原有技能，玩家可以自由组合技能槽来实现不同的构筑

### 技能系统

技能同样可以通过yaml配置文件进行配置。

**技能配置示例**：

``````yaml
warrior_q:
  name: "利刃横扫"
  description: "对面前扇形范围敌人造成伤害"
  type: "active"
  level: 1
  cooldown: 6.0
  mana-cost: 0
  cast-time: 0
  effects:
    - "damage:1.2"
    - "area:cone:5"
``````



**技能执行流程**

``````mermaid
graph TD
    A[玩家按下技能键] --> B{技能是否可用?}
    B -->|是| C[检查法力值/冷却]
    C --> D{条件满足?}
    D -->|是| E[执行技能效果]
    D -->|否| F[提示玩家]
    E --> G[应用冷却]
    E --> H[消耗法力]
    B -->|否| I[提示冷却中]
``````

### 装备系统

| 强化等级 | 成功率 | 消耗灰 | 防具加成 | 武器加成 |
| :------- | :----- | :----- | :------- | :------- |
| +1       | 80%    | 10     | +1防御   | +1攻击   |
| +2       | 50%    | 20     | +2防御   | +2攻击   |
| +3       | 30%    | 40     | +3防御   | +3攻击   |
| +4       | 20%    | 80     | +4防御   | +4攻击   |
| +5       | 10%    | 120    | +5防御   | +5攻击   |
| +6       | 5%     | 180    | +6防御   | +6攻击   |

### 类结构设计

#### 核心类

``````mermaid
classDiagram
    class RPGPlugin {
        +onEnable()
        +onDisable()
        +getPlayerDataManager()
        +getConfigManager()
        +getSkillManager()
    }
    
    class PlayerData {
        -UUID playerId
        -String playerClass
        -String origin
        -Map attributes
        +getAttribute()
        +increaseAttribute()
        +getMaxHealth()
        +getMeleeDamage()
    }
    
    class PlayerDataManager {
        -Map playerDataMap
        +getPlayerData(UUID)
        +savePlayerData(UUID)
        +loadPlayerData(UUID)
    }
    
    class ConfigManager {
        -YamlConfiguration classesConfig
        -YamlConfiguration originsConfig
        -YamlConfiguration skillsConfig
        +loadConfigs()
        +getClassData(String)
        +getOriginData(String)
        +getSkillData(String)
    }
    
    class SkillManager {
        -Map skillExecutors
        +registerExecutors()
        +executeSkill(Player, skillId)
    }
    
    RPGPlugin --> PlayerDataManager
    RPGPlugin --> ConfigManager
    RPGPlugin --> SkillManager
    PlayerDataManager --> PlayerData
    ConfigManager --> ClassData
    ConfigManager --> OriginData
    ConfigManager --> SkillData
    SkillManager --> SkillExecutor
``````

#### GUI类

``````mermaid
classDiagram
    class CharacterCreationGUI {
        +open(Player)
    }
    
    class OriginSelectionGUI {
        +open(Player, className)
    }
    
    class AttributeAllocationGUI {
        +open(Player)
    }
    
    CharacterCreationGUI --|> GUIListener
    OriginSelectionGUI --|> GUIListener
    AttributeAllocationGUI --|> GUIListener
``````

#### 技能执行器接口

``````mermaid
classDiagram
    class SkillExecutor {
        <<interface>>
        +execute(Player, SkillData) boolean
    }
    
    class WarriorSkills {
        +BladeSweep
        +RageRecovery
        +ForwardSlash
    }
    
    class MageSkills {
        +Fireball
        +IceSpike
        +SwiftFeet
    }
    
    class BardSkills {
        +HarshWords
        +HeroicPoem
        +LegacyOfFire
    }
    
    WarriorSkills ..|> SkillExecutor
    MageSkills ..|> SkillExecutor
    BardSkills ..|> SkillExecutor
``````

#### 数据存储

由于思考到服务器没有自带数据库，为了节省运维成本与管理成本，所以设计为通过yaml文件存储玩家数据

**玩家数据文件示例（UUID.yaml）**

``````yaml
creationCompleted: true
playerClass: "warrior"
origin: "wise_warrior"
attributes:
  strength: 8
  dexterity: 6
  constitution: 10
  wisdom: 4
  intelligence: 2
  charisma: 0
level: 5
experience: 1200
skills:
  - "warrior_passive"
  - "warrior_q"
  - "warrior_f"
  - "warrior_c"
equipment:
  helmet: "WARRIOR_HELMET:1"
  chestplate: "WARRIOR_CHESTPLATE:1"
  leggings: "WARRIOR_LEGGINGS:1"
  boots: "WARRIOR_BOOTS:1"
  mainHand: "IRON_SWORD:1"
``````

### 命令列表

| 命令        | 描述             | 权限               |
| :---------- | :--------------- | :----------------- |
| /rpg        | 打开角色创建界面 | rpg.command.create |
| /rpg stats  | 查看角色属性     | rpg.command.stats  |
| /rpg skills | 打开技能界面     | rpg.command.skills |

### 权限节点

| 权限节点            | 描述         | 默认  |
| :------------------ | :----------- | :---- |
| rpg.command.create  | 使用/rpg命令 | true  |
| rpg.command.stats   | 查看角色属性 | true  |
| rpg.command.skills  | 管理技能     | op    |
| rpg.bypass.creation | 跳过角色创建 | false |

### 配置文件结构

```
plugins/
└── RPGPlugin/
    ├── config.yml          # 主配置文件
    ├── classes.yml         # 职业定义
    ├── origins.yml         # 出身定义
    ├── skills.yml          # 技能定义
    └── players/
        ├── uuid1.yml       # 玩家数据
        ├── uuid2.yml
        └── ...
```
