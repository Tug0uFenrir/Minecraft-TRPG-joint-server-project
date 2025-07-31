# DiceCore使用说明&文档

- `DicePlugin`：主插件的入口，用于处理命令和事件触发
- `DiceRollEvent`：自定义事件类
- `DiceResult`：封装完整的骰子结果
- `DiceRoll`：处理单个骰子组的投掷逻辑
- `DiceExpressionParser`：专门负责解析和计算骰子的表达式

## 事件钩子的使用例

``````java
public class XXXPlugin extends JavaPlugin implements Listener {
     @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onDiceRoll(DiceRollEvent event) {
        DiceResult result = event.getResult();
        Player player = (Player) event.getSender();
        
        getLogger().info(player.getName() + " 投掷了: " + 
                        event.getExpression() + " = " + result.getFinalResult());
        
        // 获取详细的骰子结果
        for (DiceRoll roll : result.getDiceRolls()) {
            getLogger().info("骰子: " + roll.getCount() + "d" + roll.getFaces() + 
                            " = " + roll.getTotal());
        }
    }
}
``````

