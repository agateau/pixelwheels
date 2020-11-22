<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.4" tiledversion="1.4.1" name="snow" tilewidth="64" tileheight="64" spacing="4" margin="2" tilecount="210" columns="15">
 <image source="snow.png" width="1020" height="952"/>
 <terraintypes>
  <terrain name="ice" tile="-1"/>
  <terrain name="mountain1" tile="-1"/>
  <terrain name="forest" tile="-1"/>
 </terraintypes>
 <tile id="5">
  <properties>
   <property name="start" value="true"/>
  </properties>
 </tile>
 <tile id="6">
  <properties>
   <property name="material" value="SNOW"/>
  </properties>
 </tile>
 <tile id="7">
  <properties>
   <property name="material" value="SNOW"/>
  </properties>
 </tile>
 <tile id="8" terrain=",,,1"/>
 <tile id="9" terrain=",,1,1"/>
 <tile id="10" terrain=",,1,"/>
 <tile id="11" terrain="1,1,1,"/>
 <tile id="12" terrain="1,1,,1"/>
 <tile id="18">
  <properties>
   <property name="material" value="SNOW"/>
  </properties>
 </tile>
 <tile id="19">
  <properties>
   <property name="material" value="TURBO"/>
  </properties>
 </tile>
 <tile id="23" terrain=",1,,1"/>
 <tile id="24" terrain="1,1,1,1"/>
 <tile id="25" terrain="1,,1,"/>
 <tile id="26" terrain="1,,1,1"/>
 <tile id="27" terrain=",1,1,1"/>
 <tile id="30">
  <objectgroup draworder="index">
   <object id="0" x="9.63636" y="8.18182" width="54.5455" height="55.8182"/>
  </objectgroup>
 </tile>
 <tile id="31">
  <objectgroup draworder="index">
   <object id="0" x="-0.181818" y="8" width="55.6364" height="55.8182"/>
  </objectgroup>
 </tile>
 <tile id="33">
  <properties>
   <property name="material" value="SNOW"/>
  </properties>
  <objectgroup draworder="index">
   <object id="0" x="63.6364" y="42.5455">
    <polygon points="0.363636,-4.90909 -13.0909,-12.9091 -21.8182,20.9091 0,21.2727"/>
   </object>
   <object id="0" x="46.7273" y="43.6364">
    <polygon points="0,0 -14.7273,2.54545 -10.9091,20.3636 -4.72727,20"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="34">
  <objectgroup draworder="index">
   <object id="0" x="28.3636" y="63.6364">
    <polygon points="0,0 3.81818,-16 -29.0909,-18.3636 -28.3636,0.363636"/>
   </object>
   <object id="0" x="18.7273" y="46.3636">
    <polygon points="0,0 -4.18182,-15.6364 -20.1818,-8.72727 -19.4545,-0.545455"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="35">
  <objectgroup draworder="index">
   <object id="0" x="46" y="52.75">
    <polygon points="0,0 9.375,-14.5 8,-30 -1.625,-44 -20.875,-44 -34,-34.625 -39,-19.25 -31.75,-3 -17.875,4"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="36">
  <objectgroup draworder="index">
   <object id="0" x="0" y="24.1818" width="64" height="15.0909"/>
  </objectgroup>
 </tile>
 <tile id="37">
  <objectgroup draworder="index">
   <object id="0" x="1.09091" y="24" width="10" height="16"/>
   <object id="0" x="53.2727" y="24.1818" width="9.81818" height="15.6364"/>
   <object id="0" x="10.9091" y="28.9091" width="42.1818" height="5.63636"/>
  </objectgroup>
 </tile>
 <tile id="38" terrain=",1,,1"/>
 <tile id="39" terrain="1,1,1,1"/>
 <tile id="40" terrain="1,,1,"/>
 <tile id="45">
  <properties>
   <property name="material" value="SNOW"/>
  </properties>
  <objectgroup draworder="index">
   <object id="0" x="9.45455" y="0.181818" width="54.7273" height="63.6364"/>
  </objectgroup>
 </tile>
 <tile id="46">
  <properties>
   <property name="material" value="SNOW"/>
  </properties>
  <objectgroup draworder="index">
   <object id="0" x="-0.545455" y="0" width="56" height="64"/>
  </objectgroup>
 </tile>
 <tile id="47">
  <properties>
   <property name="material" value="SNOW"/>
  </properties>
 </tile>
 <tile id="48">
  <objectgroup draworder="index">
   <object id="0" x="62.9091" y="36.1818">
    <polygon points="0,0 1.27273,-4 0.909091,-36.3636 -17.4545,-36.3636"/>
   </object>
   <object id="0" x="39.8182" y="27.6364">
    <polygon points="0,0 17.2727,-3.45455 5.27273,-28 -1.09091,-27.6364"/>
   </object>
   <object id="0" x="39.2727" y="12.3636">
    <polygon points="0,0 -0.727273,-12.3636 -10.7273,-12.1818 -13.6364,-6.90909"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="49">
  <objectgroup draworder="index">
   <object id="0" x="0" y="33.0909">
    <polygon points="0,0 0.363636,1.81818 32.9091,-27.2727 29.8182,-33.0909 0,-33.0909"/>
   </object>
   <object id="0" x="10.3636" y="26.1818">
    <polygon points="0,0 10.9091,2.90909 10.7273,-10"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="50">
  <objectgroup draworder="index">
   <object id="0" x="38.3636" y="63.2727">
    <polygon points="0,0.545455 0,-39.4545 -9.81818,-38 -13.4545,-32.9091 -13.8182,0.363636"/>
   </object>
   <object id="0" x="38.3636" y="23.6364" width="25.2727" height="15.4545"/>
  </objectgroup>
 </tile>
 <tile id="51">
  <objectgroup draworder="index">
   <object id="0" x="0" y="24.9091" width="25.2727" height="14.1818"/>
   <object id="0" x="38.7273" y="63.2727">
    <polygon points="0,0.727273 -0.181818,-33.4545 -3.81818,-38.1818 -13.6364,-38.3636 -13.6364,0.727273"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="53" terrain=",1,,"/>
 <tile id="54" terrain="1,1,,"/>
 <tile id="55" terrain="1,,,"/>
 <tile id="60" terrain=",,,2">
  <objectgroup draworder="index">
   <object id="0" x="9.63636" y="0.181818" width="54.3636" height="55.8182"/>
  </objectgroup>
 </tile>
 <tile id="61" terrain=",,2,">
  <objectgroup draworder="index">
   <object id="0" x="-0.181818" y="-0.181818" width="55.6364" height="56.1818"/>
  </objectgroup>
 </tile>
 <tile id="62" terrain="0,0,0,">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
 </tile>
 <tile id="63" terrain="0,0,,0">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
  <objectgroup draworder="index"/>
 </tile>
 <tile id="64">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="65">
  <objectgroup draworder="index">
   <object id="0" x="24.1818" y="0" width="15.2727" height="25.2727"/>
   <object id="0" x="63.8182" y="37.4545">
    <polygon points="0,0 0,-12.1818 -39.6364,-12 -37.8182,-2 -31.4545,-0.363636"/>
   </object>
  </objectgroup>
 </tile>
 <tile id="66">
  <objectgroup draworder="index">
   <object id="0" x="0.181818" y="37.4545">
    <polygon points="0,0 32.9091,0.181818 37.2727,-6 37.8182,-13.8182 0,-12.9091"/>
   </object>
   <object id="0" x="24.5455" y="0" width="13.2727" height="23.8182"/>
  </objectgroup>
 </tile>
 <tile id="75" terrain=",2,,"/>
 <tile id="76" terrain="2,,,"/>
 <tile id="77" terrain="0,,0,0">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
 </tile>
 <tile id="78" terrain=",0,0,0">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
 </tile>
 <tile id="90" terrain=",2,2,2"/>
 <tile id="91" terrain="2,,2,2"/>
 <tile id="96">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="97">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="105" terrain="2,2,,2"/>
 <tile id="106" terrain="2,2,2,"/>
 <tile id="107" terrain="0,0,0,0">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
 </tile>
 <tile id="109">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="110">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="120" terrain=",,,0"/>
 <tile id="121" terrain=",,0,0"/>
 <tile id="122" terrain=",,0,">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="123">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="135" terrain=",0,,0"/>
 <tile id="136" terrain="0,0,0,0">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
 </tile>
 <tile id="137" terrain="0,,0,"/>
 <tile id="150" terrain=",0,,"/>
 <tile id="151" terrain="0,0,,"/>
 <tile id="152" terrain="0,,,"/>
</tileset>
