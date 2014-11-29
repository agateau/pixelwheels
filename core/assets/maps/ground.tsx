<?xml version="1.0" encoding="UTF-8"?>
<tileset name="ground" tilewidth="64" tileheight="64">
 <image source="tileset.png" width="384" height="320"/>
 <terraintypes>
  <terrain name="Water" tile="10"/>
  <terrain name="Road" tile="8"/>
  <terrain name="Ground" tile="9"/>
 </terraintypes>
 <tile id="0" terrain="1,1,2,2"/>
 <tile id="1" terrain="1,2,1,2"/>
 <tile id="2" terrain="2,2,1,1"/>
 <tile id="3" terrain="2,2,2,1"/>
 <tile id="4" terrain="2,2,1,2"/>
 <tile id="5" terrain="2,1,2,1"/>
 <tile id="6" terrain="2,1,2,2"/>
 <tile id="7" terrain="1,2,2,2"/>
 <tile id="8" terrain="1,1,1,1"/>
 <tile id="9" terrain="2,2,2,2">
  <properties>
   <property name="max_speed" value="0.5"/>
  </properties>
 </tile>
 <tile id="10" terrain="0,0,0,0">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="11" terrain="2,0,2,2">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="12" terrain="0,2,2,2">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="13" terrain="2,2,2,0">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="14" terrain="2,2,0,2">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="15" terrain="2,1,1,1"/>
 <tile id="16" terrain="1,2,1,1"/>
 <tile id="17" terrain="1,1,2,1"/>
 <tile id="18" terrain="1,1,1,2"/>
 <tile id="19">
  <properties>
   <property name="finish" value="True"/>
  </properties>
 </tile>
 <tile id="20" terrain="2,0,2,0">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="21" terrain="0,2,0,2">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="22" terrain="2,2,0,0">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="23" terrain="0,0,2,2">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="24" terrain="0,0,0,2">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="25" terrain="0,0,2,0">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="26" terrain="0,2,0,0">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="27" terrain="2,0,0,0">
  <properties>
   <property name="max_speed" value="0"/>
  </properties>
 </tile>
 <tile id="28">
  <properties>
   <property name="start" value="True"/>
  </properties>
 </tile>
</tileset>
