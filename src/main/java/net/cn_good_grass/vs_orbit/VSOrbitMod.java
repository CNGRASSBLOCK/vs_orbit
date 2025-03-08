package net.cn_good_grass.vs_orbit;

import com.mojang.logging.LogUtils;
import net.cn_good_grass.vs_orbit.procedures.gravitation.core.event.OnWorldLoad;
//import net.cn_good_grass.vs_orbit.procedures.gravitation.event.ReadDataPack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(VSOrbitMod.MODID)
public class VSOrbitMod
{
    //模组名字
    public static final String MODID = "vs_orbit";
    //日志输出
    public static final Logger LOGGER = LogUtils.getLogger();

    public VSOrbitMod()
    {
        //注册事件
        MinecraftForge.EVENT_BUS.register(new OnWorldLoad()); //注册引力更新事件
        //MinecraftForge.EVENT_BUS.register(new ReadDataPack());
    }
}

