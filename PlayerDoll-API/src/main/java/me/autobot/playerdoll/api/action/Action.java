package me.autobot.playerdoll.api.action;


import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.action.type.AbsActionType;
import org.bukkit.GameMode;

public final class Action {

    public boolean done = false;
    public final int limit;
    public final int interval;
    public final int offset;
    private int count;
    private int next;
    public final boolean isContinuous;
    public final boolean isPerTick;
    private final int loop;

    private Action(int limit, int interval, int offset, boolean continuous, boolean perTick)
    {
        this.limit = limit;
        this.interval = interval;
        this.offset = offset;
        isPerTick = perTick;
        // interval -> perTick counts
        next = perTick ? offset : interval + offset;
        isContinuous = continuous;
        loop = perTick ? interval : 1;
    }

    public static Action once()
    {
        return new Action(1, 1, 0, false, false);
    }

    public static Action continuous()
    {
        return new Action(-1, 1, 0, true, false);
    }

    public static Action interval(int interval)
    {
        return new Action(-1, interval, 0, false, false);
    }

    public static Action interval(int interval, int offset)
    {
        return new Action(-1, interval, offset, false, false);
    }

    public static Action perTick(int interval)
    {
        return new Action(-1, interval, 0, false, true);
    }

    public static Action perTick(int interval, int offset)
    {
        return new Action(-1, interval, offset, false, true);
    }

    public Boolean tick(ActionPack actionPack, AbsActionType type)
    {
        next--;
        Boolean cancel = null;
        if (next <= 0)
        {
            for (int perTick = 0; perTick < loop; perTick++) {
                if (interval == 1 && !isContinuous) {
                    // need to allow entity to tick, otherwise won't have effect (bow)
                    // actions are 20 tps, so need to clear status mid tick, allowing entities process it till next time
                    if (!type.preventSpectator || actionPack.bukkitPlayer().getGameMode() != GameMode.SPECTATOR) {
                        type.inactiveTick(actionPack.baseEntity, this);
                    }
                }

                if (!type.preventSpectator || actionPack.bukkitPlayer().getGameMode() != GameMode.SPECTATOR) {
                    cancel = type.execute(actionPack.baseEntity, this);
                }
            }
            count++;
            if (count == limit) {
                type.stop(actionPack.baseEntity, null);
                done = true;
                return cancel;
            }
            next = isPerTick ? 1 : interval;
        }
        else
        {
            if (!type.preventSpectator || actionPack.bukkitPlayer().getGameMode() != GameMode.SPECTATOR)
            {
                type.inactiveTick(actionPack.baseEntity, this);
            }
        }
        return cancel;
    }

    public void retry(ActionPack actionPack, AbsActionType type)
    {
        //assuming action run but was unsuccesful that tick, but opportunity emerged to retry it, lets retry it.
        if (!type.preventSpectator || actionPack.bukkitPlayer().getGameMode() != GameMode.SPECTATOR)
        {
            type.execute(actionPack.baseEntity, this);
        }
        count++;
        if (count == limit)
        {
            type.stop(actionPack.baseEntity, null);
            done = true;
        }
    }

}
