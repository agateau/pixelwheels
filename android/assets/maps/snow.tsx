<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.9" tiledversion="1.9.2" name="snow" tilewidth="64" tileheight="64" spacing="4" margin="2" tilecount="270" columns="15">
 <image source="snow.png" width="1020" height="1224"/>
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
 <tile id="8">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;circle&quot;,
  &quot;x&quot;: 1,
  &quot;y&quot;: 0,
  &quot;radius&quot;: 0.5
}</property>
  </properties>
 </tile>
 <tile id="9">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0,
  &quot;width&quot;: 1,
  &quot;height&quot;: 0.5
}</property>
  </properties>
 </tile>
 <tile id="10">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;circle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0,
  &quot;radius&quot;: 0.5
}</property>
  </properties>
 </tile>
 <tile id="11">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0.5,
      &quot;width&quot;: 1,
      &quot;height&quot;: 0.5
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 1
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="12">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0.5,
      &quot;width&quot;: 1,
      &quot;height&quot;: 0.5
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.5,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 1
    }
  ]
}
</property>
  </properties>
 </tile>
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
 <tile id="23">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.5,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.5,
  &quot;height&quot;: 1
}</property>
  </properties>
 </tile>
 <tile id="25">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.5,
  &quot;height&quot;: 1
}</property>
  </properties>
 </tile>
 <tile id="26">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0,
      &quot;width&quot;: 1,
      &quot;height&quot;: 0.5
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 1
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="27">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0,
      &quot;width&quot;: 1,
      &quot;height&quot;: 0.5
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.5,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 1
    }
  ]
}
</property>
  </properties>
 </tile>
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
 <tile id="38">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.5,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.5,
  &quot;height&quot;: 1
}</property>
  </properties>
 </tile>
 <tile id="40">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.5,
  &quot;height&quot;: 1
}</property>
  </properties>
 </tile>
 <tile id="41">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0.5,
      &quot;width&quot;: 1,
      &quot;height&quot;: 0.5
    }, {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.5
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="42">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 1
    }, {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.5,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.5
    }
  ]
}
</property>
  </properties>
 </tile>
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
 <tile id="53">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;circle&quot;,
  &quot;x&quot;: 1,
  &quot;y&quot;: 1,
  &quot;radius&quot;: 0.5
}</property>
  </properties>
 </tile>
 <tile id="54">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0.5,
  &quot;width&quot;: 1,
  &quot;height&quot;: 0.5
}</property>
  </properties>
 </tile>
 <tile id="55">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;circle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 1,
  &quot;radius&quot;: 0.5
}</property>
  </properties>
 </tile>
 <tile id="56">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0,
      &quot;width&quot;: 1,
      &quot;height&quot;: 0.5
    }, {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0.5,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.5
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="57">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.5,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 1
    }, {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.5
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="60">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;circle&quot;,
  &quot;radius&quot;: 0.45,
  &quot;x&quot;: 1,
  &quot;y&quot;: 0
}</property>
  </properties>
  <objectgroup draworder="index">
   <object id="0" x="9.63636" y="0.181818" width="54.3636" height="55.8182"/>
  </objectgroup>
 </tile>
 <tile id="61">
  <objectgroup draworder="index">
   <object id="0" x="-0.181818" y="-0.181818" width="55.6364" height="56.1818"/>
  </objectgroup>
 </tile>
 <tile id="62">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
 </tile>
 <tile id="63">
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
 <tile id="68">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0,
      &quot;width&quot;: 1,
      &quot;height&quot;: 0.5
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.5,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 1
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="69">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0,
      &quot;width&quot;: 1,
      &quot;height&quot;: 0.5
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 1
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="70">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.5,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.5,
  &quot;height&quot;: 1
}</property>
  </properties>
 </tile>
 <tile id="71">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.5,
  &quot;height&quot;: 1
}</property>
  </properties>
 </tile>
 <tile id="77">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
 </tile>
 <tile id="78">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
 </tile>
 <tile id="83">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0.5,
      &quot;width&quot;: 1,
      &quot;height&quot;: 0.5
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.5,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 1
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="84">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0.5,
      &quot;width&quot;: 1,
      &quot;height&quot;: 0.5
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 1
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="90">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;circle&quot;,
  &quot;radius&quot;: 0.45,
  &quot;x&quot;: 1,
  &quot;y&quot;: 0
}</property>
  </properties>
 </tile>
 <tile id="92">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;circle&quot;,
  &quot;radius&quot;: 0.4,
  &quot;x&quot;: 0.45,
  &quot;y&quot;: 0.5
}</property>
  </properties>
 </tile>
 <tile id="93">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.2,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.8,
  &quot;height&quot;: 0.8
}
</property>
  </properties>
 </tile>
 <tile id="94">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.8,
  &quot;height&quot;: 0.8
}
</property>
  </properties>
 </tile>
 <tile id="96">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="97">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="106">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;circle&quot;,
  &quot;radius&quot;: 0.45,
  &quot;x&quot;: 1,
  &quot;y&quot;: 0
}</property>
  </properties>
 </tile>
 <tile id="107">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
 </tile>
 <tile id="108">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.2,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.8,
  &quot;height&quot;: 1
}
</property>
  </properties>
 </tile>
 <tile id="109">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.8,
  &quot;height&quot;: 1
}
</property>
  </properties>
  <objectgroup draworder="index"/>
 </tile>
 <tile id="110">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="122">
  <objectgroup draworder="index"/>
 </tile>
 <tile id="123">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.2,
  &quot;y&quot;: 0.2,
  &quot;width&quot;: 0.8,
  &quot;height&quot;: 0.8
}
</property>
  </properties>
  <objectgroup draworder="index"/>
 </tile>
 <tile id="124">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0.2,
  &quot;width&quot;: 0.8,
  &quot;height&quot;: 0.8
}
</property>
  </properties>
 </tile>
 <tile id="136">
  <properties>
   <property name="material" value="ICE"/>
  </properties>
 </tile>
 <tile id="154">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.4,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.8,
      &quot;height&quot;: 0.5,
      &quot;angle&quot;: 45
    }, {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.6,
      &quot;height&quot;: 0.5
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="157">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.25,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.5,
  &quot;height&quot;: 1
}
</property>
  </properties>
 </tile>
 <tile id="165">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.05,
  &quot;y&quot;: 0.25,
  &quot;width&quot;: 0.95,
  &quot;height&quot;: 0.5
}
</property>
  </properties>
 </tile>
 <tile id="166">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0.25,
  &quot;width&quot;: 1,
  &quot;height&quot;: 0.5
}
</property>
  </properties>
 </tile>
 <tile id="167">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0.25,
  &quot;width&quot;: 1,
  &quot;height&quot;: 0.5
}
</property>
  </properties>
 </tile>
 <tile id="168">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0.25,
  &quot;width&quot;: 0.9,
  &quot;height&quot;: 0.5
}
</property>
  </properties>
 </tile>
 <tile id="169">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.4,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.8,
      &quot;height&quot;: 0.5,
      &quot;angle&quot;: 45
    }, {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.6
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="171">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0,
  &quot;y&quot;: 0.25,
  &quot;width&quot;: 1.214,
  &quot;height&quot;: 0.5,
  &quot;angle&quot;: 45
}
</property>
  </properties>
 </tile>
 <tile id="172">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0.65,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.35
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.45,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.1,
      &quot;height&quot;: 0.65
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="180">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.75
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.75,
      &quot;height&quot;: 0.5
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="181">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.75
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.75,
      &quot;height&quot;: 0.5
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="182">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.25,
  &quot;y&quot;: 0.6,
  &quot;width&quot;: 0.5,
  &quot;height&quot;: 0.4
}
</property>
  </properties>
 </tile>
 <tile id="183">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.25,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.5,
  &quot;height&quot;: 0.4
}
</property>
  </properties>
 </tile>
 <tile id="185">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: -0.207,
  &quot;y&quot;: 0.25,
  &quot;width&quot;: 1.414,
  &quot;height&quot;: 0.5,
  &quot;angle&quot;: 45
}
</property>
  </properties>
 </tile>
 <tile id="187">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.35
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.45,
      &quot;y&quot;: 0.35,
      &quot;width&quot;: 0.1,
      &quot;height&quot;: 0.65
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="195">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.75
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.75,
      &quot;height&quot;: 0.5
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="196">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;multi&quot;,
  &quot;obstacles&quot;: [
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.75,
      &quot;height&quot;: 0.5
    },
    {
      &quot;type&quot;: &quot;rectangle&quot;,
      &quot;x&quot;: 0.25,
      &quot;y&quot;: 0.25,
      &quot;width&quot;: 0.5,
      &quot;height&quot;: 0.75
    }
  ]
}
</property>
  </properties>
 </tile>
 <tile id="199">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.2,
  &quot;y&quot;: 0.25,
  &quot;width&quot;: 1.214,
  &quot;height&quot;: 0.5,
  &quot;angle&quot;: 45
}
</property>
  </properties>
 </tile>
 <tile id="202">
  <properties>
   <property name="obstacle">{
  &quot;type&quot;: &quot;rectangle&quot;,
  &quot;x&quot;: 0.25,
  &quot;y&quot;: 0,
  &quot;width&quot;: 0.5,
  &quot;height&quot;: 1
}
</property>
  </properties>
 </tile>
 <wangsets>
  <wangset name="Terrains" type="corner" tile="-1">
   <wangcolor name="ice" color="#ff0000" tile="-1" probability="1"/>
   <wangcolor name="mountain1" color="#00ff00" tile="-1" probability="1"/>
   <wangcolor name="forest" color="#0000ff" tile="-1" probability="1"/>
   <wangtile tileid="8" wangid="0,0,0,2,0,0,0,0"/>
   <wangtile tileid="9" wangid="0,0,0,2,0,2,0,0"/>
   <wangtile tileid="10" wangid="0,0,0,0,0,2,0,0"/>
   <wangtile tileid="11" wangid="0,2,0,0,0,2,0,2"/>
   <wangtile tileid="12" wangid="0,2,0,2,0,0,0,2"/>
   <wangtile tileid="23" wangid="0,2,0,2,0,0,0,0"/>
   <wangtile tileid="24" wangid="0,2,0,2,0,2,0,2"/>
   <wangtile tileid="25" wangid="0,0,0,0,0,2,0,2"/>
   <wangtile tileid="26" wangid="0,0,0,2,0,2,0,2"/>
   <wangtile tileid="27" wangid="0,2,0,2,0,2,0,0"/>
   <wangtile tileid="38" wangid="0,2,0,2,0,0,0,0"/>
   <wangtile tileid="39" wangid="0,2,0,2,0,2,0,2"/>
   <wangtile tileid="40" wangid="0,0,0,0,0,2,0,2"/>
   <wangtile tileid="53" wangid="0,2,0,0,0,0,0,0"/>
   <wangtile tileid="54" wangid="0,2,0,0,0,0,0,2"/>
   <wangtile tileid="55" wangid="0,0,0,0,0,0,0,2"/>
   <wangtile tileid="60" wangid="0,0,0,3,0,0,0,0"/>
   <wangtile tileid="61" wangid="0,0,0,0,0,3,0,0"/>
   <wangtile tileid="62" wangid="0,1,0,0,0,1,0,1"/>
   <wangtile tileid="63" wangid="0,1,0,1,0,0,0,1"/>
   <wangtile tileid="75" wangid="0,3,0,0,0,0,0,0"/>
   <wangtile tileid="76" wangid="0,0,0,0,0,0,0,3"/>
   <wangtile tileid="77" wangid="0,0,0,1,0,1,0,1"/>
   <wangtile tileid="78" wangid="0,1,0,1,0,1,0,0"/>
   <wangtile tileid="90" wangid="0,0,0,3,0,0,0,3"/>
   <wangtile tileid="91" wangid="0,3,0,0,0,3,0,0"/>
   <wangtile tileid="105" wangid="0,3,0,0,0,3,0,0"/>
   <wangtile tileid="106" wangid="0,0,0,3,0,0,0,3"/>
   <wangtile tileid="107" wangid="0,1,0,1,0,1,0,1"/>
   <wangtile tileid="120" wangid="0,0,0,1,0,0,0,0"/>
   <wangtile tileid="121" wangid="0,0,0,1,0,1,0,0"/>
   <wangtile tileid="122" wangid="0,0,0,0,0,1,0,0"/>
   <wangtile tileid="135" wangid="0,1,0,1,0,0,0,0"/>
   <wangtile tileid="136" wangid="0,1,0,1,0,1,0,1"/>
   <wangtile tileid="137" wangid="0,0,0,0,0,1,0,1"/>
   <wangtile tileid="150" wangid="0,1,0,0,0,0,0,0"/>
   <wangtile tileid="151" wangid="0,1,0,0,0,0,0,1"/>
   <wangtile tileid="152" wangid="0,0,0,0,0,0,0,1"/>
  </wangset>
 </wangsets>
</tileset>
