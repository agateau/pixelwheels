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
 * Instances cannot be directly created: get them through the get() static method.
 * This ensures there is only one instance of each reward.
 */
public class Reward {
    private final static HashMap<Category, HashMap<String, Reward>> sInstances = new HashMap<Category, HashMap<String, Reward>>();

    public enum Category {
        VEHICLE,
        CHAMPIONSHIP
    }

    public final Category category;
    public final String id;

    private Reward(Category category, String id) {
        this.category = category;
        this.id = id;
    }

    public String toString() {
        return category.toString() + "." + id;
    }

    public static Reward get(Category category, String id) {
        HashMap<String, Reward> map = sInstances.get(category);
        if (map == null) {
            map = new HashMap<String, Reward>();
            sInstances.put(category, map);
        }
        Reward reward = map.get(id);
        if (reward == null) {
            reward = new Reward(category, id);
            map.put(id, reward);
        }
        return reward;
    }

    public static Reward get(Championship championship) {
        Assert.check(championship != null, "Can't find a reward for a null championship");
        return get(Category.CHAMPIONSHIP, championship.getId());
    }

    public static Reward get(VehicleDef vehicleDef) {
        Assert.check(vehicleDef != null, "Can't find a reward for a null vehicle");
        return get(Category.VEHICLE, vehicleDef.id);
    }
}
