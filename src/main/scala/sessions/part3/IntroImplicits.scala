package sessions.part3

object IntroImplicits extends App {

  //introduction to implicits

  val pair: (String, Int) = "John" -> 55

  val intPair = 5 -> "five"

  // lets check the string class for this method called "->"
  // or the int class for this methid " -> "
  lazy val z: Int = ???
  lazy val x: String = ???
  //there is no such method

  //one more example. the toInt method
  lazy val v = x.toInt

  //the underscore below the method name
  //indicates that this is an implicit method
  //or a method in an implicit class

  case class Person(name: String) {
    def greet : String = s"Hello my name is $name"
  }

  implicit def strToPerson(name: String): Person = Person(name)

  println("John".greet)

  // even though greet is not a method of string class i am calling it like that
  // how does this work ?

  //when the compiler sees "John".greet is first looks for the normal code
  // i,e a method greet in string class
  // it does not find that
  // then it looks for implicit classes or methods that can help in compiling this code
  // it looks for a way to convert string into a type that has greet method
  // it does find something like that in the code above

  val xgreet = "John".greet // equivalent to strToPerson("John").greet

  //if there are mutiple implclits found then there will be error

  class A {
    def greet: Int = 42
  }
  //implicit def strToA(string: String): A = new A

  //Implicit parameters
  def add(x: Int)(implicit to: Int) : Int = x+to
  implicit val numb: Int = 5
  add(5) //the compiler will search for an implicit int in to scope and supply that automatically to the function
  // again if there are multiple found then there will be an error
  //implicit val aNumb: Int = 10
  add(15)

}
