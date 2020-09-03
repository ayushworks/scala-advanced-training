package sessions.part1

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1: methods with single param
  def methodWithSingleArgument(arg: Int): String = s"$arg is the argument"

  //normal way to call
  val call1 = methodWithSingleArgument(29)

  // but with a single argument you can also call using {} braces
  val call2 = methodWithSingleArgument {
    // write some complex code
    42
  }

  println(call2)

  //plenty of examples in scala libraries for this

  //1. Try
  Try("this code is tried")

  Try{
    "this code is tried"
  }

  //2. map
  List(1,2,3).map(x => x+1)

  List(1,2,3).map {
    x =>
      x + 1
  }


  // syntax sugar #2: single abstract method
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val aFunkyInstance: Action = (x: Int) => x + 1

  // example: Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("hello, Scala")
  })

  val aSweeterThread = new Thread(() => println("sweet, Scala!"))

  // syntax sugar #4: multi-word method naming

  class Cricketer(name: String) {
    def `has a batting average of`(avg: Double) = println(s"$name averages $avg")
  }

  val sachin = new Cricketer("Sachin")
  sachin `has a batting average of` 55.0


  // syntax sugar #5: infix types
  class Union[A, B]
  //normal way
  val union: Union[Int,String] = ???

  // a better way
  val union1: Int Union String = ???

  class -->[A, B]
  val towards: Int --> String = ???

  // syntax sugar #6: update() is very special, much like apply()
  val anArray = Array(1,2,3)
  anArray(2) = 7  // rewritten to anArray.update(2, 7)
  // used in mutable collections
  // remember apply() AND update()!

  // syntax sugar #7: setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member = internalMember // "getter"
    def member_=(value: Int): Unit =
      internalMember = value // "setter"
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // rewrittern as aMutableContainer.member_=(42)
}
