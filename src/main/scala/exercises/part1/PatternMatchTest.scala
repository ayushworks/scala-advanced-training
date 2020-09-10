package exercises.part1

object PatternMatchTest extends App {

  case class Person(name: String, age: Int)

  val john = Person("John", 25)

  /**
   * 1. Add match for a person, if found print "matched a person"
    */
  john match {
    case _ => println("default matched")
  }

  /**
   * 2. Add match for a person, if found print person's name and age
   */
  john match {
    case _ => println("default matched")
  }

  /**
   * 3. Add match for a person whose name starts with "J"  , if found print person's name and age
   */
  john match {
    case _ => println("default matched")
  }

  /**
   * 4. Add match for a List of int, if found print "matched a list"
   */
  List(1,2,3) match {
    case _ => println("default matched")
  }

  /**
   * 5. Add match for an empty list, if found print "found empty list"
   */
  List.empty[Int] match {
    case _ => println("default matched")
  }

  /**
   * 6. Add match for a List of int whose first element is 1, if found print "found list starting with 1"
   */
  List(1,2,3) match {
    case _ => println("default matched")
  }

  /**
   * 6. Add match for a List of int whose size is greater than 2, if found print "found list of size > 2"
   */
  List(1,2,3) match {
    case _ => println("default matched")
  }
}
