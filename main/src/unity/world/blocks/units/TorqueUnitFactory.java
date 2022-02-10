package unity.world.blocks.units;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.units.*;
import unity.graphics.*;
import unity.world.graph.*;

import static arc.Core.atlas;

public class TorqueUnitFactory extends UnitFactory implements GraphBlock{
	public GraphBlockConfig config = new GraphBlockConfig(this);
	public float maxSpeed = 50;
	public float maxEfficiency = 2.5f;
	
	public final TextureRegion[] bottomRegions = new TextureRegion[2];
	public TextureRegion rotateRegion, mbaseRegion, overlayRegion;

	public TorqueUnitFactory(String name){
		super(name);
		
		hasPower = false;
	}
	
	@Override
	public TextureRegion[] icons(){
		return new TextureRegion[]{atlas.find(name)};
	}
	
	@Override
	public void load(){
		super.load();
		
		rotateRegion = atlas.find(name + "-moving");
		mbaseRegion = atlas.find(name + "-mbase");
		overlayRegion = atlas.find(name + "-overlay");
		
		for(int i = 0; i < 2; i++){
            bottomRegions[i] = atlas.find(name + "-bottom" + (i + 1));
        }
	}
	
	@Override
	public void setStats(){
		super.setStats();
	}
	
	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid){
		draw.rect(region, x, y, rotation);
	}
	
	@Override 
	public Block getBuild(){
		return this;
	}
	
	@Override
	public GraphBlockConfig getConfig(){
		return config;
	}
	
	public class TorqueUnitFactoryBuild extends UnitFactoryBuild implements GraphBuild{
        OrderedMap<Class<? extends Graph>,GraphNode> graphNodes = new OrderedMap<>();
        int prevTileRotation = -1;
        boolean placed = false;

        @Override public void created(){ if(!placed){ init(); } }
        @Override public void displayBars(Table table){ super.displayBars(table); displayGraphBars(table); }
        @Override public void write(Writes write){ super.write(write);writeGraphs(write); }
        @Override public void read(Reads read, byte revision){ super.read(read, revision); readGraphs(read); }

        @Override
        public void placed(){
            super.placed();
            placed = true;
            connectToGraph();
        }

        @Override
        public void onRemoved(){
            disconnectFromGraph();
            super.onRemoved();
        }

        @Override
        public void updateTile(){
            if(!placed){
                placed = true;
                connectToGraph();
            }
            super.updateTile();
            updateGraphs();
        }

        @Override
        public float efficiency(){
            return super.efficiency() * Mathf.clamp(Mathf.map(getGraph(TorqueGraph.class).lastVelocity,0,maxSpeed,0,maxEfficiency),0,maxEfficiency);
        }

        @Override
        public void draw(){
			float rot = getGraph(TorqueGraph.class).rotation;
			float fixedRot = (rotdeg() + 90f) % 180f - 90f;
			
			int variant = rotation % 2;
			
			float deg = rotation == 0 || rotation == 3 ? rot : -rot;
			
			Draw.rect(bottomRegions[variant], x, y);
			Draw.rect(outRegion, x, y, rotdeg());
			
			//shaft
			Draw.rect(mbaseRegion, x, y, fixedRot);
			
			UnityDrawf.drawRotRect(rotateRegion, x, y, 24f, 3.5f, 3.5f, fixedRot + 90f, rot, rot + 180f);
			
			Draw.rect(overlayRegion, x, y, fixedRot);
			
			//unit
			if(currentPlan != -1){
                UnitPlan plan = plans.get(currentPlan);
                Draw.draw(Layer.blockOver, () -> Drawf.construct(this, plan.unit, rotdeg() - 90f, progress / plan.time, speedScl, time));
            }
			
            Draw.z(Layer.blockOver);
			
            payRotation = rotdeg();
            drawPayload();
			
            Draw.z(Layer.blockOver + 0.1f);
			
			if(topRegion.found()) Draw.rect(topRegion, x, y, fixedRot);
			drawTeamTop();
        }

        @Override public OrderedMap<Class<? extends Graph>, GraphNode> getNodes(){
                    return graphNodes;
                }

        @Override public Building getBuild(){
            return this;
        }
        @Override public int getPrevRotation(){
            return prevTileRotation;
        }

        @Override public void setPrevRotation(int t){
            prevTileRotation = t;
        }
    }
}
