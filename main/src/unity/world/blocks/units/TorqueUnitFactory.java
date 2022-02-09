package unity.world.blocks.units;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.units.*;
import unity.graphics.*;
import unity.world.graph.*;

import static arc.Core.atlas;

public class TorqueUnitFactory extends UnitFactory implements GraphBlock{
	public GraphBlockConfig config = new GraphBlockConfig(this);
	public float maxSpeed = 50;
	public float maxEfficiency = 2.5f;

	public TorqueUnitFactory(String name){
		super(name);
	}
	
	@Override
	public TextureRegion[] icons(){
		return new TextureRegion[]{atlas.find(name)};
	}
	
	@Override
	public void load(){
		super.load();
	}
	
	@Override
	public void setStats(){
		super.setStats();
	}
	
	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid){
		super.drawPlace(x, y, rotation, valid);
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
			super.draw();
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