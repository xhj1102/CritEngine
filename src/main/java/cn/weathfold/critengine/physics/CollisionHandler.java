/**
 * 
 */
package cn.weathfold.critengine.physics;

import java.util.Iterator;
import java.util.Set;

import cn.weathfold.critengine.IEntityProcessor;
import cn.weathfold.critengine.entity.Entity;
import cn.weathfold.critengine.entity.IEntityFilter;
import cn.weathfold.critengine.entity.attribute.AttrGeometry;
import cn.weathfold.critengine.physics.RayTraceResult.EnumEdgeSide;
import cn.weathfold.critengine.physics.attribute.AttrCollider;
import cn.weathfold.critengine.physics.attribute.AttrVelocity;
import cn.weathfold.critengine.util.Rect;
import cn.weathfold.critengine.util.Vector2d;

/**
 * 由VelocityUpdater或物理引擎本体进行调用，执行碰撞检查的工作。
 * @author WeAthFolD
 */
public class CollisionHandler implements IEntityProcessor {
	
	private static class CollidableEntityFilter implements IEntityFilter {

		@Override
		public boolean isEntityApplicable(Entity ent) {
			return ent.hasAttribute("collider");
		}
		
	}
	
	private static IEntityFilter filter = new CollidableEntityFilter();

	@Override
	public void processEntity(Entity e) {
		if(!filter.isEntityApplicable(e)) {
			return;
		}
		AttrCollider collider = (AttrCollider) e.getAttribute("collider");
		if(!collider.doesMove)
			return;
		
		Set<Entity> set = e.sceneObj.getEntitiesWithin(e.getGeomProps(), filter, e);
		if(set.isEmpty()) {
			return;
		}
		
		System.out.println(e.getGeomProps().getMinX() + ", " + e.getGeomProps().getMinY());
		Iterator<Entity> iter = set.iterator();
		Rect rect = new Rect(iter.next().getGeomProps());
		//比较鬼畜的算法，当然一般只会碰撞到一个实体所以问题不会太大……吧……
		while(iter.hasNext()) {
			Rect r2 = iter.next().getGeomProps();
			rect.expand(r2);
		}
		alignEntity(e, rect);
	}
	
	public void handlesVelUpdate(Entity e, AttrGeometry pre, Vector2d after) {
		RayTraceResult res = CEPhysicEngine.rayTrace(e.sceneObj, pre.pos, after, filter, e);
		//System.out.println(res.collided);
		if(!res.collided) {
			//Vector2d v1 = pre.pos.copy(), v2 = after.copy();
			//v1.addVector(pre.width, pre.height);
			//v2.addVector(pre.width, pre.height);
			//res = CEPhysicEngine.rayTrace(e.sceneObj, v1, v2, filter, e);
			if(!res.collided) {
				pre.pos = after;
				return;
			}
		}
		
		AttrCollider collider = (AttrCollider) e.getAttribute("collider");
		if(!collider.doesMove)
			return;
		
		AttrVelocity vel = (AttrVelocity) e.getAttribute("velocity");
		//愉快的反弹
		if(res.edge.dirX != 0)
			vel.vel.x = Math.abs(vel.vel.x) * res.edge.dirX * collider.attnRate;
		if(res.edge.dirY != 0)
			vel.vel.y = Math.abs(vel.vel.y) * res.edge.dirY * collider.attnRate;
		
		//System.out.println(e + " CH " + res.edge + " " + res.collidedEntity);
		
		alignEntity(e, res.collidedEntity.getGeomProps(), res.edge);
	}
	
	/**
	 * 将实体就近对齐到某个区域旁边
	 **/
	private void alignEntity(Entity targ, Rect bound) {
		AttrGeometry 
			geo0 = targ.getGeomProps();
		
		double min = Double.MAX_VALUE;
		
		Vector2d res = null;
		//EnumEdgeSide rSide = null;
		for(EnumEdgeSide side : EnumEdgeSide.values()) {
			if(side == EnumEdgeSide.NONE)
				continue;
			Vector2d vec = virtualAlign(targ, bound, side);
			double dist = Math.abs(vec.x - geo0.pos.x) + Math.abs(vec.y - geo0.pos.y);
			if(dist < min) {
				res = vec;
				min = dist;
				//rSide = side;
			}
		}
		//res.addVector(rSide.dirX * 0.001, rSide.dirY * 0.001);
		geo0.pos = res;
	}
	
	private void alignEntity(Entity targ, Rect bound, EnumEdgeSide side) {
		AttrGeometry geom = targ.getGeomProps();
		geom.pos = virtualAlign(targ, bound, side);
	}
	
	private Vector2d virtualAlign(Entity targ, Rect bound, EnumEdgeSide side) {
		AttrGeometry geom = targ.getGeomProps();
		Vector2d res = geom.pos.copy();
		switch(side) {
		case BOTTOM:
			res.y = bound.getMinY() - geom.height;
			break;
		case LEFT:
			res.x = bound.getMinX() - geom.width;
			break;
		case RIGHT:
			res.x = bound.getMaxX();
			break;
		case TOP:
			res.y = bound.getMaxY();
			break;
		default:
			break;
		}
		return res;
	}

}
