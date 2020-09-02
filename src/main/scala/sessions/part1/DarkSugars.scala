package sessions.part1

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
}
