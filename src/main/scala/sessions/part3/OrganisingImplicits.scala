package sessions.part3

import scala.Predef
object OrganisingImplicits extends App {

  //we will look at how compiler searches for implcits
  //and how we can optimize this search

  // we will understand the compiler search for implicits using an example - scala collections ordering

  println(List(1,4,2,9,6).sorted) //sorted function takes an implicit Ordering[A] type
  //the above statement works fine which means there is already an implicit Ordering[Int] defined somewhere and imported automatically by the compiler

  //that package is scala.Predef, this package is automatically imported when we write scala code

  implicit val lessThanOrdering: Ordering[Int] = Ordering.fromLessThan((a,b) => a > b)
  println(List(1,4,2,9,6).sorted)

  //also if i define one more implicit ordering then the code will not compile
  //implicit val greaterThanOrdering: Ordering[Int] = Ordering.fromLessThan((a,b) => a < b)

  /**
   * Possible implicit values
   *  - val/var
   *  - objects
   *  - defs with no parantheses
   */

  case class Person(name: String, age: Int)

  val personList = List(Person("John",30), Person("Sam",25), Person("Dave", 28))

  object Person {
    //implicit val personOrderingByAge: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.age < p2.age)
  }

  //println(personList.sorted)

  // where does compiler searches for implicit values

  /**
   * Implicit Scope (ordered by priority)
   * - normal scope = Local scope (where we write our code)
   * - imported scope =
   * - companion objects of all involved types in the method signature
   *   * List
   *   * Ordering
   *   * A or its any super type
   *
   */

  //def sorted[B >: A](implicit ord: Ordering[B]): List

  /**
   * Best practise for placing implicits
   *
   * 1. If you are defining an implicit val and there is only a single possible value and you can edit the code for that type
   *  - place it in the companion object
   *
   * 2. If there is one good implicit value (good meaning used 99% times) and some other values then
   *  - place the good one in companion object and the others in local scope
   *
   * 3. If there are multiple good implicit values
   *   - package them seperately in different object
   */

  object AlphabeticNameOrdering {
    implicit val personOrderingByName: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.name.compareTo(p2.name) < 0)
  }

  object AgeOrdering {
    implicit val personOrderingByAge: Ordering[Person] = Ordering.fromLessThan((p1, p2) => p1.name.compareTo(p2.name) < 0)
  }

  import AlphabeticNameOrdering.personOrderingByName
  println(personList.sorted)
}
