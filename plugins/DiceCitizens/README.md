# DiceCitizensAddon使用文档

因为没有使用插件自动创建和配置yml配置文件，所以需要技术手动创建。

路径如下：`plugins/DiceCitizensAddon/config.yml`

创建格式如下：
``````yaml
# Citizens骰子行为控制配置

# 全局行为 - 对所有NPC生效
global-behaviors:
  behavior1:
    dice-range: "1-5"   #骰子范围
    type: "message"    #动作类型
    target: "player"   #作用的对象
    action: "§c运气不太好，掷出了{dice}点"   #骰点文本
    cooldown: 10    #冷却时间
  behavior2:
    dice-range: "16-20"
    type: "message"
    target: "player"
    action: "§a运气爆棚！掷出了{dice}点"
  behavior3:
    dice-value: 1     #骰点点数要求
    type: "sound"     #动作类型
    target: "npc"     #作用对象
    action: "entity.villager.no:1:0.5"    #可以指定为特定的生物或者怪物

# NPC特定行为
npcs:
  1: # NPC ID
    behavior1:
      dice-value: 20
      type: "command"      #command类型表示执行一段指令
      target: "console"    #执行指令前需要在此处标上console
      action: "effect give {player} minecraft:glowing 30"   #也可以执行指令
    behavior2:
      dice-range: "10-15"
      type: "animation"
      target: "npc"
      action: "cheer"
  202:
    behavior1:
      dice-range: "10-20"
      type: "command"
      target: "console"
      action: "quest progress {player} dice_quest 1"   #同样的可以与任务插件进行联动，发布或者提交任务
      
 
``````

**术语解释**

配置中支持多种行为类型，以下是列举出来的一些行为：

- 消息（message）：发送消息给玩家、NPC或广播
- 命令（command）：以玩家、NPC或控制台身份执行命令
- 动画（animation）：播放Citizens动画（由于API删除了此功能，所以暂时禁用）
- 音效（sound）：播放游戏音效
- 特效（effect）：播放视觉效果

**命令例子**

``````
/dicebehavior <骰值|骰值范围|global> <类型> <目标> [冷却] <行为>

示例:
/dicebehavior 1 message player 你掷出了1点！
/dicebehavior 1-3 animation npc angry
/dicebehavior global command console say {npc}: 玩家 {player} 掷出了 {dice} 点！
``````

**简单使用流程**

1. 创建npc

   - 使用Citizens的相关命令

2. 配置行为

   - 选择npc：`/npc select`

   - 添加行为：`/dicebehavior <骰点> <类型> <目标> <行为>`

     - ``````
       /dicebehavior 20 message npc "大成功！你说服我了，给，这是你要的效果"
       /dicebehavior 1 command console "effect give {player} minecraft:blindness 10"
       ``````

3. 测试NPC行为

   - 靠近NPC
   - 使用指令或者使用指令工具
   - 观察NPC行为