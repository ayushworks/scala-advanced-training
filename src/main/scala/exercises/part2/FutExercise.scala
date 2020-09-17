package exercises.part2

import scala.concurrent.Future

object FutExercise extends App {


  //1. create a future from a value
  def immediate[A](value: A): Future[A] = ???

  //2. run the  two futures in sequence, first followed by second
  def inSeq[A,B](first: Future[A], second: Future[B]) : Future[B]  = ???

  //3. create a new Future with the value of the future that completes FIRST
  // hint : use promise, create a promise and fullfill it on completion of futures
  def first[A](one: Future[A], two: Future[A]) : Future[A] = ???

  //4. create a new Future with the value of the future that completes LAST
  // hint : use 2 promises
  def last[A](one: Future[A], two: Future[A]) : Future[A] = ???


  //5. retry until
  // run the  action until the condition is satisfied
  // when the condition is met return the future as a result
  def retryUntil[A](action: () => Future[A], condition : A => Boolean): Future[A] = ???
}
