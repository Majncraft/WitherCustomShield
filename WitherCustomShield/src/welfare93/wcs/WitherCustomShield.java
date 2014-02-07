package welfare93.wcs;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.Tree;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WitherCustomShield extends JavaPlugin  implements Listener{
	public static WitherCustomShield instance;
	private static List<String> disblocks=new ArrayList<String>();
	private static List<Integer> dislog1=new ArrayList<Integer>();
	private static List<Integer> dislog2=new ArrayList<Integer>();
	private static int witherspawn=255;
	private static boolean active=true;
	private class WitherSpawnProtect
	{
		//from UP to limit=true, from DOWN to limit=false
		private boolean updown;
		//Y coords for spawn limit, negative value = always
		private int coordY;
		private String dimension; 
		public WitherSpawnProtect()
		{
			
		}
		public boolean Protected(Location loc)
		{
			if(loc.getWorld().getName().equals(dimension))
			{
				if((loc.getY()>=coordY && updown)||(loc.getY()>=coordY && !updown))
					return true;
			}
			return false;
		}
	}
    @Override
    public void onLoad(){
    	instance=this;
        Init();
    	getLogger().info("Started");
    }
   
    @Override
    public void onEnable(){
    	getLogger().info("Enabled");
    	active=true;
        getServer().getPluginManager().registerEvents(this, this);
    }
 
    @Override
    public void onDisable() {
    	getLogger().info("Disabled");
    	active=false;
    }
    
    
    private void Init()
    {
    	firstrun();
        File conf = new File(this.getDataFolder() + "/config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(conf);
        
        disblocks=config.getStringList("WitherProtected.blocks");
        dislog1=config.getIntegerList("WitherProtected.log1");
        dislog2=config.getIntegerList("WitherProtected.log2");
        witherspawn=config.getInt("WitherSpawn.nether.upto");
        
        
    }
    private void firstrun()
    {
    	File f = new File(this.getDataFolder() + "/");
    	if(!f.exists())
    	    f.mkdir();
    	f = new File(this.getDataFolder() + "/config.yml");
    	if(!f.exists())
    	{
			try {
				f.createNewFile();
		        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		        List<String> a=new ArrayList<String>();
		        dislog1=new ArrayList<Integer>();
		        dislog1.add(0);
		        config.set("WitherProtected.blocks", a);
		        config.set("WitherProtected.log1", dislog1);
		        config.set("WitherSpawn.nether.upto", witherspawn);
		        List<Integer> b=new ArrayList<Integer>();
		        dislog2=new ArrayList<Integer>();
		        dislog2.add(0);
		        config.set("WitherProtected.log2", dislog2);
		     
		        config.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else
    	{
	        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
	        List<String> a=new ArrayList<String>();
	        dislog1=new ArrayList<Integer>();
	        dislog1.add(0);
	        config.set("WitherProtected.blocks", a);
	        config.set("WitherProtected.log1", dislog1);
	        config.set("WitherSpawn.nether.upto", witherspawn);
	        List<Integer> b=new ArrayList<Integer>();
    		
    	}
    }
    @EventHandler
    public void onStructureGrow(StructureGrowEvent event)
    {
    	if(event.getSpecies()==TreeType.DARK_OAK)
    	{
		for(BlockState a:event.getBlocks())
    	{
    		if(a.getBlock().getType()==Material.BEDROCK)
    			a.setType(Material.BEDROCK);
    	}
    	}
    }
    

    @EventHandler
    public void onExplosionWither(EntityExplodeEvent event1) { 
    	if (event1==null || event1.isCancelled() ||event1.getEntity()==null || event1.blockList()==null ||!active) return;
      EntityType type = event1.getEntity().getType();
      if (type == EntityType.WITHER|| type==EntityType.WITHER_SKULL) {
        List<Block> blocks = new ArrayList<Block>();
        for(Block a:event1.blockList())
        {
         if(disblocks.contains(a.getType().toString()))

        	 for(String b:disblocks)
        	 {
        		 if(b.equals(a.getType().toString()))
        		 {
        	       blocks.add(a);
        		 }
        	 }
         if(a.getType().equals(Material.LOG))
        	 for(int b:dislog1)
        	 {
        		 ((Tree) a).getSpecies().compareTo(TreeSpecies.BIRCH);
        		 if(a.getData()==b)
        			 blocks.add(a);
        	 }
         else if(a.getType().equals(Material.LOG_2))
        	 for(int b:dislog2)
        	 {
        		 if(a.getData()==b)
        			 blocks.add(a);
        	 }
         
        	 
        }
        event1.blockList().removeAll(blocks);
        for(Block b:blocks)
        {
        	b.setType(Material.AIR);
        }
      }
    }

    @EventHandler
    public void WitherProjectile(EntityExplodeEvent event1)
    {
    	if (event1==null || event1.isCancelled() ||event1.getEntity()==null || event1.blockList()==null ||!active) return;
        EntityType type = event1.getEntity().getType();
        if (type == EntityType.WITHER || type==EntityType.WITHER_SKULL) {
          List<Block> blocks = new ArrayList<Block>();
          for(Block a:event1.blockList())
          {
           if(disblocks.contains(a.getType().toString()))

          	 for(String b:disblocks)
          	 {
          		 if(b.equals(a.getType().toString()))
          	 blocks.add(a);
          	 }
           if(a.getType().equals(Material.LOG))
          	 for(int b:dislog1)
          	 {
          		 if(a.getData()==b)
          	 blocks.add(a);
          	 }
           else if(a.getType().equals(Material.LOG_2))
          	 for(int b:dislog2)
          	 {
          		 if(a.getData()==b)
          	 blocks.add(a);
          	 }
        }
          event1.blockList().removeAll(blocks);
          for(Block b:blocks)
          {
          	b.setType(Material.AIR);
          }
        }
    }
    @EventHandler 
    public void onCreatureSpawn(CreatureSpawnEvent event){
    	if(event.getSpawnReason()==SpawnReason.BUILD_WITHER && event.getLocation().getWorld().getEnvironment()==World.Environment.NETHER && event.getLocation().getY()>=witherspawn)
    		event.setCancelled(true);
    	
    	} 
    @EventHandler
    public void WitherEatBlocks(EntityChangeBlockEvent event1) { 
    	if (event1==null || event1.isCancelled() ||event1.getEntity()==null || event1.getBlock()==null ||!active) return;
    EntityType type = event1.getEntity().getType();
    if (type == EntityType.WITHER|| type==EntityType.WITHER_SKULL) {
    	
     	 for(String b:disblocks)
     	 {
     		 if(b.equals(event1.getBlock().getType().toString()))
     		 {
     			 event1.getBlock().setType(Material.AIR);
     	          event1.setCancelled(true);
     		 }
     	 }
      if(event1.getBlock().getType().equals(Material.LOG))
     	 for(int b:dislog1)
     	 {
     		 if(event1.getBlock().getData()==b)
     		 {
     			 event1.getBlock().setType(Material.AIR);
     	          event1.setCancelled(true);
     		 }
     	 }
      else if(event1.getBlock().getType().equals(Material.LOG_2))
     	 for(int b:dislog2)
     	 {
     		 if(event1.getBlock().getData()==b)
     		 {
     			 event1.getBlock().setType(Material.AIR);
     	          event1.setCancelled(true);
     	          
     		 }
     	 }
    }
    }
  }
