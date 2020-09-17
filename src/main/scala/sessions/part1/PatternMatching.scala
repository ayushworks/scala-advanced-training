package sessions.part1

object PatternMatching extends App {

  //recap

  // pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  case class Human(name: String, age: Int)

  val jim = Human("Jim", 22)
  val greetings = jim match {
    case Human(n, _) => s"Hi, my name is $n"
  }


  // Whereas the apply method is like a constructor
  // which takes arguments and creates an object,
  // the unapply takes an object
  // and tries to give back the arguments
  class Person(val name: String, val age: Int)

  object Person {

    //def apply(name: String, age: Int): Person = new  Person(name, age)

    def unapply(person: Person): Option[(String, Int)] =
      if (person.age < 21) None
      else Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 20)

  val pair  = Person.unapply(new Person("Bob",21))

  // the type that you match for
  // is the input to the function
  // the function must be defined
  // in the class that you are matching against

  val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n and I am $a yo."
    // case Person(n, a) =>
    // is a  shorthand syntax for calling
    // Person.unapply(bob)
    // which returns the deconstructed values
    case _ => "no  match"

  }

  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
      // case Person(status) is a shorthand for
      // Person.unapply(age) which returns an Option[String]
  }

  println(legalStatus)


  // custom return types for unapply
  // isEmpty: Boolean, get: something.

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false
      def get= person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This person's name is $n"
    case _ => "An alien"
  })

  // we can also use booleans in return type

  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val n: Int = 8
  val mathProperty = n match {
    case singleDigit() => "single digit"
    case even() => "an even number"
    case _ => "no property"
  }

  // infix patterns
  case class Or[A, B](a: A, b: B)
  val either = Or(2, "two")
  val humanDescription = either match {
    case number Or string => s"$number is written as $string"
  }

}
