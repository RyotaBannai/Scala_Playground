package bobsrocket {
  package navigation {
    class Navigation {
      val map = new StarMap
    }
    class StarMap {}
    package launch {
      class Booster1
    }

    class MissionControl {
      var booster1 = new launch.Booster1
      var booster2 = new bobsrocket.launch.Booster2
      var booster3 = new _root_.launch.Booster3 // my_package/launch.scala を参照
    }
  }

  class Ship {
    // bobsrockets.navigation.Navigator と書かなくて良い
    val nav = new navigation.Navigation
  }

  package fleets {
    class Fleet {
      // bobsrockets.Ship と書かなくて良い
      def addShip() = { new Ship }
    }
  }
  //

  package launch {
    class Booster2
  }
}
