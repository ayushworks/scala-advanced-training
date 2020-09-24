package exercises.part2

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration._

object FutExercise extends App {


  //1. create a future from a value
  def immediate[A](value: A): Future[A] = Future.successful(value)

  //2. run the  two futures in sequence, first followed by second
  def inSeq[A,B](first: Future[A], second: Future[B]) : Future[B]  = first.flatMap(_ =>second)

  //3. create a new Future with the value of the future that completes FIRST
  // hint : use promise, create a promise and fullfill it on completion of futures
  def first[A](one: Future[A], two: Future[A]) : Future[A] = {

    val promise = Promise[A]

    def tryComplete(promise: Promise[A], result: Try[A]) = result match {
      case Success(value) => Try(promise.success(value)).recover{
        case t: Throwable =>
      }
      case Failure(exception) =>   Try(promise.failure(exception)).recover{
        case t: Throwable =>
      }
    }
    /*one.onComplete{
      case Success(value) => Try(promise.success(value)).recover{
        case t: Throwable =>
      }
      case Failure(exception)  => Try(promise.failure(exception)).recover{
        case t: Throwable =>
      }
    }

    two.onComplete {
      case Success(value) => Try(promise.success(value)).recover{
        case t: Throwable =>
      }
      case Failure(exception)  => Try(promise.failure(exception)).recover{
        case t: Throwable =>
      }
    }*/
    /*one.onComplete(t => tryComplete(promise, t))
    two.onComplete(t => tryComplete(promise, t))*/

    one.onComplete(promise.tryComplete)
    two.onComplete(promise.tryComplete)

    promise.future
  }

  //4. create a new Future with the value of the future that completes LAST
  // hint : use 2 promises
  def last[A](one: Future[A], two: Future[A]) : Future[A] = ???


  //5. retry until
  // run the  action until the condition is satisfied
  // when the condition is met return the future as a result
  def retryUntil[A](action: () => Future[A], condition : A => Boolean): Future[A] = action().filter(condition).recoverWith{
    case _ => retryUntil(action, condition)
  }

  val random =new Random()
  val action = () => Future{
    Thread.sleep(1000)
    val value = random.nextInt(10)
    println(s"generated $value")
    value
  }

  val result = Await.result(retryUntil(action, (x: Int) => x > 8), 1.minute)
  println(result)

}
