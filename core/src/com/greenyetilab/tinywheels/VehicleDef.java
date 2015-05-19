package com.greenyetilab.tinywheels;

import com.badlogic.gdx.utils.Array;

/**
 * Definition of a vehicle
 */
public class VehicleDef {
    String id;
    String name;
    float speed;
    String mainImage;
    Array<AxleDef> axles = new Array<AxleDef>();
}
