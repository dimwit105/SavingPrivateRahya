package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.shield;

public record
ShieldModifier(String id, double value, Operation op, Target target)
{
    public enum Operation {
        ADD_NUMBER,
        ADD_SCALAR
    }
    public enum Target {
        RECHARGE_DELAY,
        RECHARGE_QUANTITY,
        SHIELD_CAPACITY
    }
}
