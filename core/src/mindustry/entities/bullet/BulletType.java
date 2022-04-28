package mindustry.entities.bullet;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.annotations.Annotations.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.Wall.*;

import static mindustry.Vars.*;

public class BulletType extends Content implements Cloneable{
    /** Lifetime in ticks. */
    public float lifetime = 40f;
    /** Speed in units/tick. */
    public float speed = 1f;
    /** Direct damage dealt on hit. */
    public float damage = 1f;
    /** Hitbox size. */
    public float hitSize = 4;
    /** Clipping hitbox. */
    public float drawSize = 40f;
    /** Drag as fraction of velocity. */
    public float drag = 0f;
    /** Whether to pierce units. */
    public boolean pierce;
    /** Whether to pierce buildings. */
    public boolean pierceBuilding;
    /** Maximum # of pierced objects. */
    public int pierceCap = -1;
    /** If false, this bullet isn't removed after pierceCap is exceeded. Expert usage only. */
    public boolean removeAfterPierce = true;
    /** For piercing lasers, setting this to true makes it get absorbed by plastanium walls. */
    public boolean laserAbsorb = true;
    /** Life fraction at which this bullet has the best range/damage/etc. Used for lasers and continuous turrets. */
    public float optimalLifeFract = 0f;
    /** Z layer to drawn on. */
    public float layer = Layer.bullet;
    /** Effect shown on direct hit. */
    public Effect hitEffect = Fx.hitBulletSmall;
    /** Effect shown when bullet despawns. */
    public Effect despawnEffect = Fx.hitBulletSmall;
    /** Effect created when shooting. */
    public Effect shootEffect = Fx.shootSmall;
    /** Effect created when charging starts; only usable in single-shot weapons with a firstShotDelay / shotDelay. */
    public Effect chargeEffect = Fx.none;
    /** Extra smoke effect created when shooting. */
    public Effect smokeEffect = Fx.shootSmallSmoke;
    /** Sound made when hitting something or getting removed.*/
    public Sound hitSound = Sounds.none;
    /** Sound made when hitting something or getting removed.*/
    public Sound despawnSound = Sounds.none;
    /** Pitch of the sound made when hitting something*/
    public float hitSoundPitch = 1;
    /** Volume of the sound made when hitting something*/
    public float hitSoundVolume = 1;
    /** Extra inaccuracy when firing. */
    public float inaccuracy = 0f;
    /** How many bullets get created per ammo item/liquid. */
    public float ammoMultiplier = 2f;
    /** Multiplied by turret reload speed to get final shoot speed. */
    public float reloadMultiplier = 1f;
    /** Multiplier of how much base damage is done to tiles. */
    public float buildingDamageMultiplier = 1f;
    /** Recoil from shooter entities. */
    public float recoil;
    /** Whether to kill the shooter when this is shot. For suicide bombers. */
    public boolean killShooter;
    /** Whether to instantly make the bullet disappear. */
    public boolean instantDisappear;
    /** Damage dealt in splash. 0 to disable.*/
    public float splashDamage = 0f;
    /** If true, splash damage is "correctly" affected by unit hitbox size. Used for projectiles that do not collide / have splash as their main source of damage. */
    public boolean scaledSplashDamage = false;
    /** Knockback in velocity. */
    public float knockback;
    /** Should knockback follow the bullet's direction */
    public boolean impact;
    /** Status effect applied on hit. */
    public StatusEffect status = StatusEffects.none;
    /** Intensity of applied status effect in terms of duration. */
    public float statusDuration = 60 * 8f;
    /** Whether this bullet type collides with tiles. */
    public boolean collidesTiles = true;
    /** Whether this bullet type collides with tiles that are of the same team. */
    public boolean collidesTeam = false;
    /** Whether this bullet type collides with air/ground units. */
    public boolean collidesAir = true, collidesGround = true;
    /** Whether this bullet types collides with anything at all. */
    public boolean collides = true;
    /** If true, this projectile collides with non-surface floors. */
    public boolean collideFloor = false;
    /** If true, this projectile collides with static walls */
    public boolean collideTerrain = false;
    /** Whether velocity is inherited from the shooter. */
    public boolean keepVelocity = true;
    /** Whether to scale lifetime (not actually velocity!) to disappear at the target position. Used for artillery. */
    public boolean scaleLife;
    /** Whether this bullet can be hit by point defense. */
    public boolean hittable = true;
    /** Whether this bullet can be reflected. */
    public boolean reflectable = true;
    /** Whether this projectile can be absorbed by shields. */
    public boolean absorbable = true;
    /** Whether to move the bullet back depending on delta to fix some delta-time related issues.
     * Do not change unless you know what you're doing. */
    public boolean backMove = true;
    /** Bullet range positive override. */
    public float maxRange = -1f;
    /** When > 0, overrides range even if smaller than base range. */
    public float rangeOverride = -1f;
    /** When used in a turret with multiple ammo types, this can be set to a non-zero value to influence range. */
    public float rangeChange = 0f;
    /** Range initialized in init(). */
    public float range = 0f;
    /** % of block health healed **/
    public float healPercent = 0f;
    /** flat amount of block health healed */
    public float healAmount = 0f;
    /** Whether to make fire on impact */
    public boolean makeFire = false;
    /** Whether to create hit effects on despawn. Forced to true if this bullet has any special effects like splash damage. */
    public boolean despawnHit = false;
    /** If true, unit armor is ignored in damage calculations. Ignored for building armor. */
    public boolean pierceArmor = false;

    //additional effects
    
    /** Whether status and despawnHit should automatically be set. */
    public boolean setDefaults = true;

    public float fragCone = 360f;
    public float fragAngle = 0f;
    public int fragBullets = 9;
    public float fragVelocityMin = 0.2f, fragVelocityMax = 1f, fragLifeMin = 1f, fragLifeMax = 1f;
    public @Nullable BulletType fragBullet = null;
    public Color hitColor = Color.white;
    public Color healColor = Pal.heal;
    public Effect healEffect = Fx.healBlockFull;
    /** Bullets spawned when this bullet is created. Rarely necessary, used for visuals. */
    public Seq<BulletType> spawnBullets = new Seq<>();
    /** Unit spawned _instead of_ this bullet. Useful for missiles. */
    public @Nullable UnitType spawnUnit;

    public Color trailColor = Pal.missileYellowBack;
    public float trailChance = -0.0001f;
    public float trailInterval = 0f;
    public Effect trailEffect = Fx.missileTrail;
    public float trailParam =  2f;
    public boolean trailRotation = false;
    public Interp trailInterp = Interp.one;
    /** Any value <= 0 disables the trail. */
    public int trailLength = -1;
    public float trailWidth = 2f;
    public float trailSinMag = 0f, trailSinScl = 3f;

    /** Use a negative value to disable splash damage. */
    public float splashDamageRadius = -1f;

    public int incendAmount = 0;
    public float incendSpread = 8f;
    public float incendChance = 1f;
    public float homingPower = 0f;
    public float homingRange = 50f;
    /** Use a negative value to disable homing delay. */
    public float homingDelay = -1f;

    public float suppressionRange = -1f, suppressionDuration = 60f * 8f, suppressionEffectChance = 50f;

    public Color lightningColor = Pal.surge;
    public int lightning;
    public int lightningLength = 5, lightningLengthRand = 0;
    /** Use a negative value to use default bullet damage. */
    public float lightningDamage = -1;
    public float lightningCone = 360f;
    public float lightningAngle = 0f;
    /** The bullet created at lightning points. */
    public @Nullable BulletType lightningType = null;

    public float weaveScale = 1f;
    public float weaveMag = 0f;
    public boolean weaveRandom = true;
    public float hitShake = 0f, despawnShake = 0f;

    public int puddles;
    public float puddleRange;
    public float puddleAmount = 5f;
    public Liquid puddleLiquid = Liquids.water;

    public boolean displayAmmoMultiplier = true;

    public float lightRadius = -1f;
    public float lightOpacity = 0.3f;
    public Color lightColor = Pal.powerLight;

    public BulletType(float speed, float damage){
        this.speed = speed;
        this.damage = damage;
    }

    public BulletType(){
    }

    public BulletType copy(){
        try{
            BulletType copy = (BulletType)clone();
            copy.id = (short)Vars.content.getBy(getContentType()).size;
            Vars.content.handleContent(copy);
            return copy;
        }catch(Exception e){
            throw new RuntimeException("death to checked exceptions", e);
        }
    }

    /** @return estimated damage per shot. this can be very inaccurate. */
    public float estimateDPS(){
        float sum = damage + splashDamage*0.75f;
        if(fragBullet != null && fragBullet != this){
            sum += fragBullet.estimateDPS() * fragBullets / 2f;
        }
        return sum;
    }

    /** @return maximum distance the bullet this bullet type has can travel. */
    protected float calculateRange(){
        if(rangeOverride > 0) return rangeOverride;
        if(spawnUnit != null) return spawnUnit.lifetime * spawnUnit.speed;
        return Math.max(Mathf.zero(drag) ? speed * lifetime : speed * (1f - Mathf.pow(1f - drag, lifetime)) / drag, maxRange);
    }

    /** @return continuous damage in damage/sec, or -1 if not continuous. */
    public float continuousDamage(){
        return -1f;
    }

    public boolean heals(){
        return healPercent > 0 || healAmount > 0;
    }

    public boolean testCollision(Bullet bullet, Building tile){
        return !heals() || tile.team != bullet.team || tile.healthf() < 1f;
    }

    /** If direct is false, this is an indirect hit and the tile was already damaged.
     * TODO this is a mess. */
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct){
        if(makeFire && build.team != b.team){
            Fires.create(build.tile);
        }

        if(heals() && build.team == b.team && !(build.block instanceof ConstructBlock)){
            healEffect.at(build.x, build.y, 0f, healColor, build.block);
            build.heal(healPercent / 100f * build.maxHealth + healAmount);
        }else if(build.team != b.team && direct){
            hit(b);
        }
    }

    public void hitEntity(Bullet b, Hitboxc entity, float health){
        if(entity instanceof Healthc h){
            if(pierceArmor){
                h.damagePierce(b.damage);
            }else{
                h.damage(b.damage);
            }
        }

        if(entity instanceof Unit unit){
            Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
            if(impact) Tmp.v3.setAngle(b.rotation() + (knockback < 0 ? 180f : 0f));
            unit.impulse(Tmp.v3);
            unit.apply(status, statusDuration);
        }

        //for achievements
        if(b.owner instanceof WallBuild && player != null && b.team == player.team() && entity instanceof Unit unit && unit.dead){
            Events.fire(Trigger.phaseDeflectHit);
        }
    }

    public void hit(Bullet b){
        hit(b, b.x, b.y);
    }

    public void hit(Bullet b, float x, float y){
        hitEffect.at(x, y, b.rotation(), hitColor);
        hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

        Effect.shake(hitShake, hitShake, b);

        createFrags(b, x, y);

        if(puddleLiquid != null && puddles > 0){
            for(int i = 0; i < puddles; i++){
                Tile tile = world.tileWorld(x + Mathf.range(puddleRange), y + Mathf.range(puddleRange));
                Puddles.deposit(tile, puddleLiquid, puddleAmount);
            }
        }

        if(incendChance > 0 && Mathf.chance(incendChance)){
            Damage.createIncend(x, y, incendSpread, incendAmount);
        }

        if(suppressionRange > 0){
            //bullets are pooled, require separate Vec2 instance
            Damage.applySuppression(b.team, b.x, b.y, suppressionRange, suppressionDuration, 0f, suppressionEffectChance, new Vec2(b.x, b.y));
        }

        createSplashDamage(b, x, y);

        for(int i = 0; i < lightning; i++){
            Lightning.create(b, lightningColor, lightningDamage < 0 ? damage : lightningDamage, b.x, b.y, b.rotation() + Mathf.range(lightningCone/2) + lightningAngle, lightningLength + Mathf.random(lightningLengthRand));
        }
    }

    public void createSplashDamage(Bullet b, float x, float y){
        if(splashDamageRadius > 0 && !b.absorbed){
            Damage.damage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(), false, collidesAir, collidesGround, scaledSplashDamage, b);

            if(status != StatusEffects.none){
                Damage.status(b.team, x, y, splashDamageRadius, status, statusDuration, collidesAir, collidesGround);
            }

            if(heals()){
                indexer.eachBlock(b.team, x, y, splashDamageRadius, Building::damaged, other -> {
                    healEffect.at(other.x, other.y, 0f, healColor, other.block);
                    other.heal(healPercent / 100f * other.maxHealth() + healAmount);
                });
            }

            if(makeFire){
                indexer.eachBlock(null, x, y, splashDamageRadius, other -> other.team != b.team, other -> Fires.create(other.tile));
            }
        }
    }

    public void createFrags(Bullet b, float x, float y){
        if(fragBullet != null){
            for(int i = 0; i < fragBullets; i++){
                float len = Mathf.random(1f, 7f);
                float a = b.rotation() + Mathf.range(fragCone / 2) + fragAngle;
                fragBullet.create(b, x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a, Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax));
            }
        }
    }


    /** Called when the bullet reaches the end of its lifetime or is destroyed by something external. */
    public void despawned(Bullet b){
        if(despawnHit){
            hit(b);
        }
        despawnEffect.at(b.x, b.y, b.rotation(), hitColor);
        despawnSound.at(b);

        Effect.shake(despawnShake, despawnShake, b);
    }

    /** Called when the bullet is removed for any reason. */
    public void removed(Bullet b){
        if(trailLength > 0 && b.trail != null && b.trail.size() > 0){
            Fx.trailFade.at(b.x, b.y, trailWidth, trailColor, b.trail.copy());
        }
    }

    public void draw(Bullet b){
        drawTrail(b);
    }

    public void drawTrail(Bullet b){
        if(trailLength > 0 && b.trail != null){
            //draw below bullets? TODO
            float z = Draw.z();
            Draw.z(z - 0.0001f);
            b.trail.draw(trailColor, trailWidth);
            Draw.z(z);
        }
    }

    public void drawLight(Bullet b){
        if(lightOpacity <= 0f || lightRadius <= 0f) return;
        Drawf.light(b, lightRadius, lightColor, lightOpacity);
    }

    public void init(Bullet b){

        if(killShooter && b.owner() instanceof Healthc h){
            h.kill();
        }

        if(instantDisappear){
            b.time = lifetime + 1f;
        }

        if(spawnBullets.size > 0){
            for(var bullet : spawnBullets){
                bullet.create(b, b.x, b.y, b.rotation());
            }
        }
    }

    public void update(Bullet b){
        updateTrail(b);
        updateHoming(b);
        updateWeaving(b);
        updateTrailEffects(b);
    }

    public void updateHoming(Bullet b){
        if(homingPower > 0.0001f && b.time >= homingDelay){
            float realAimX = b.aimX < 0 ? b.x : b.aimX;
            float realAimY = b.aimY < 0 ? b.y : b.aimY;

            Teamc target;
            //home in on allies if possible
            if(heals()){
                target = Units.closestTarget(null, realAimX, realAimY, homingRange,
                e -> e.checkTarget(collidesAir, collidesGround) && e.team != b.team && !b.hasCollided(e.id),
                t -> collidesGround && (t.team != b.team || t.damaged()) && !b.hasCollided(t.id)
                );
            }else{
                target = Units.closestTarget(b.team, realAimX, realAimY, homingRange, e -> e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id), t -> collidesGround && !b.hasCollided(t.id));
            }

            if(target != null){
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
            }
        }
    }

    public void updateWeaving(Bullet b){
        if(weaveMag != 0){
            b.vel.rotateRadExact((float)Math.sin((b.time + Math.PI * weaveScale/2f) / weaveScale) * weaveMag * (weaveRandom ? (Mathf.randomSeed(b.id, 0, 1) == 1 ? -1 : 1) : 1f) * Time.delta * Mathf.degRad);
        }
    }

    public void updateTrailEffects(Bullet b){
        if(trailChance > 0){
            if(Mathf.chanceDelta(trailChance)){
                trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, trailColor);
            }
        }

        if(trailInterval > 0f){
            if(b.timer(0, trailInterval)){
                trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, trailColor);
            }
        }
    }
    
    public void updateTrail(Bullet b){
        if(!headless && trailLength > 0){
            if(b.trail == null){
                b.trail = new Trail(trailLength);
            }
            b.trail.length = trailLength;
            b.trail.update(b.x, b.y, trailInterp.apply(b.fin()) * (1f + (trailSinMag > 0 ? Mathf.absin(Time.time, trailSinScl, trailSinMag) : 0f)));
        }
    }

    @Override
    public void init(){
        if(pierceCap >= 1){
            pierce = true;
            //pierceBuilding is not enabled by default, because a bullet may want to *not* pierce buildings
        }

        if(setDefaults){
            if(lightning > 0){
                if(status == StatusEffects.none){
                    status = StatusEffects.shocked;
                }
            }

            if(fragBullet != null || splashDamageRadius > 0 || lightning > 0){
                despawnHit = true;
            }
        }

        if(fragBullet != null){
            fragBullet.keepVelocity = false;
        }

        if(lightningType == null){
            lightningType = !collidesAir ? Bullets.damageLightningGround : Bullets.damageLightning;
        }

        if(lightRadius <= -1){
            lightRadius = Math.max(18, hitSize * 5f);
        }
        
        drawSize = Math.max(drawSize, trailLength * speed * 2f);
        range = calculateRange();
    }

    @Override
    public ContentType getContentType(){
        return ContentType.bullet;
    }

    public @Nullable Bullet create(Teamc owner, float x, float y, float angle){
        return create(owner, owner.team(), x, y, angle);
    }

    public @Nullable Bullet create(Entityc owner, Team team, float x, float y, float angle){
        return create(owner, team, x, y, angle, 1f);
    }

    public @Nullable Bullet create(Entityc owner, Team team, float x, float y, float angle, float velocityScl){
        return create(owner, team, x, y, angle, -1, velocityScl, 1f, null);
    }

    public @Nullable Bullet create(Entityc owner, Team team, float x, float y, float angle, float velocityScl, float lifetimeScl){
        return create(owner, team, x, y, angle, -1, velocityScl, lifetimeScl, null);
    }


    public @Nullable Bullet create(Entityc owner, Team team, float x, float y, float angle, float velocityScl, float lifetimeScl, Mover mover){
        return create(owner, team, x, y, angle, -1, velocityScl, lifetimeScl, null, mover);
    }

    public @Nullable Bullet create(Bullet parent, float x, float y, float angle){
        return create(parent.owner, parent.team, x, y, angle);
    }

    public @Nullable Bullet create(Bullet parent, float x, float y, float angle, float velocityScl, float lifeScale){
        return create(parent.owner, parent.team, x, y, angle, velocityScl, lifeScale);
    }

    public @Nullable Bullet create(Bullet parent, float x, float y, float angle, float velocityScl){
        return create(parent.owner(), parent.team, x, y, angle, velocityScl);
    }

    public @Nullable Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data){
        return create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, null);
    }

    public @Nullable Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, @Nullable Mover mover){
        return create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, -1f, -1f);
    }

    public @Nullable Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY){
        if(spawnUnit != null){
            //don't spawn units clientside!
            if(!net.client()){
                Unit spawned = spawnUnit.create(team);
                spawned.set(x, y);
                spawned.rotation = angle;
                //immediately spawn at top speed, since it was launched
                spawned.vel.trns(angle, spawnUnit.speed);
                //assign unit owner
                if(spawned.controller() instanceof MissileAI ai && owner instanceof Unit unit){
                    ai.shooter = unit;
                }
                spawned.add();
            }

            //no bullet returned
            return null;
        }

        Bullet bullet = Bullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.originX = x;
        bullet.originY = y;
        bullet.aimTile = world.tileWorld(aimX, aimY);
        bullet.aimX = aimX;
        bullet.aimY = aimY;
        bullet.initVel(angle, speed * velocityScl);
        if(backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        bullet.lifetime = lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = drag;
        bullet.hitSize = hitSize;
        bullet.mover = mover;
        bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
        //reset trail
        if(bullet.trail != null){
            bullet.trail.clear();
        }
        bullet.add();

        if(keepVelocity && owner instanceof Velc v) bullet.vel.add(v.vel());
        return bullet;
    }

    public void createNet(Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl){
        Call.createBullet(this, team, x, y, angle, damage, velocityScl, lifetimeScl);
    }

    @Remote(called = Loc.server, unreliable = true)
    public static void createBullet(BulletType type, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl){
        if(type == null) return;
        type.create(null, team, x, y, angle, damage, velocityScl, lifetimeScl, null);
    }
}
