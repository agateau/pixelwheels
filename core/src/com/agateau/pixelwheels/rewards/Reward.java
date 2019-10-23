/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.rewards;

import com.agateau.pixelwheels.map.Championship;
import com.agateau.pixelwheels.vehicledef.VehicleDef;
import com.agateau.utils.Assert;
import java.util.HashMap;

/**
 * A POJO representing a reward
 *
 * <p>Instances cannot be directly created: get them through the get() static method. This ensures
 * there is only one instance of each reward.
 */
public class Reward {
    private static final HashMap<Object, Reward> sInstances = new HashMap<>();

    public final Object prize;

    private Reward(Object prize) {
        this.prize = prize;
    }

    public String toString() {
        return "reward(" + prize.toString() + ")";
    }

    private static Reward internalGet(Object object) {
        Assert.check(object != null, "Can't find or create a reward for a null object");
        Reward reward = sInstances.get(object);
        if (reward == null) {
            reward = new Reward(object);
            sInstances.put(object, reward);
        }
        return reward;
    }

    public static Reward get(Championship championship) {
        return internalGet(championship);
    }

    public static Reward get(VehicleDef vehicleDef) {
        return internalGet(vehicleDef);
    }
}
