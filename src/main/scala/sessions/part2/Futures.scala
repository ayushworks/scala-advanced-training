package sessions.part2

import java.util.concurrent.Executors

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Random, Success}

object Futures extends App {

  import scala.concurrent.ExecutionContext.Implicits.global
  //learning concurrency through functional programming techniques


  // 1. Futures - functional way of computing something in parallel or in a different thread

  // lets take a method that does a very long computation

  def calculateValue: Int = {
    //lots of work
    Thread.sleep(1000)
    42
  }

  //the way to run this on a different thread by using a future would be

  val aFuture = Future.apply(calculateValue) //creates a future by calling the apply method of the companion object

  val aFuture1 = Future  {
    calculateValue
  }

  //the parameter passed to the method is the expression that i would like to evaluate in the different thread

  //now about the compiler error
  // the apply method also expects a second implicit parameter
  // we will talk about implicit parameter later in the course

  // so we need to provide a value of execution context
  // an execution context basically handles thread allocation

  // how do we get execution context
  // 1. we can import one value that is present in scala.concurrent package
  // 2. or you can also create an execution context of your own

  val executionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(100))



  //now the program compiles

  val aFuture2: Future[Int] = Future  {
    calculateValue
  }

  // So let's take a look at the value contained in a future.
  // So like we said a future is a computation that will hold a value which is computed by somebody that
  // is by some thread at some point in time.
  // That's why it's called a future.
  // The type of aFuture2 is a Future[Int] because at some point in future it will contain an Int value

  //now lets print something to the console when the future is complete

  println("waiting for future")
  //the onComplete method takes a Try[Int] because the future might have failed with an exeption inside

  //so we will pass the onComplete a try and lets pattern match it
   aFuture2.onComplete(t => t match {
     case Success(value) => println(s"success with value $value")
     case Failure(exception) => println(s"error with ${exception.getMessage}")
   })

  //now its possible that when we run this , the main jvm thread completes before our future thread completes
  // so lets introduce a delay in the finishing of main thread

  Thread.sleep(2000)

  // the return type of onComplete is Unit
  // the function inside may return a type U but type U is not very useful
  // so usually onComplete is used for side effeets

  // the onComplete method also takes an execution context as an implicit parameter .
  // that means the thread on which the callbacks are executed are unknown to us.
  // it could be the same thread in which the future ran or it could be different
  // no assumptions please

  //lets understand further functionality of Futures by taking an example




  //When you run something asynchronously it means it is non-blocking,
  // you execute it without waiting for it to complete and carry on with other things.
  // Parallelism means to run multiple things at the same time, in parallel.
  // Parallelism works well when you can separate tasks into independent pieces of work.
  //
  //Take for example rendering frames of a 3D animation.
  // To render the animation takes a long time so if you were to launch that render from within your animation editing software you would make sure
  // it was running asynchronously so it didn't lock up your UI and you could continue doing other things.
  // Now, each frame of that animation can also be considered as an individual task.
  // If we have multiple CPUs/Cores or multiple machines available, we can render multiple frames in parallel to speed up the overall workload.


  // suppose we are designing the backend of a social media website
  // billion of users, everything needs to by asynchronous and we want to use parallelism

  case class Person(id: String, name: String) {
    def ping(anotherPerson: Person) = {
      println(s"${name} pinged ${anotherPerson.name}")
    }
  }

  object SocialNetwork {
    val names = Map(
      "id1" -> "John",
      "id2" -> "Bob",
      "id3" -> "Sam"
    )

    val friends = Map(
      "id1" -> "id2",
      "id2" -> "id3",
      "id3" -> "id1"
    )

    val random = new Random()

    //API
    def fetchProfile(id: String): Future[Person] = Future {
      //fetching from database
      Thread.sleep(random.nextInt(300))
      Person(id, names(id))
    }

    def fetchFriend(person: Person): Future[Person] = Future {
      Thread.sleep(300)
      val friendId = friends(person.id)
      Person(friendId, names(friendId))
    }
  }

  //client application
  // John wants to ping Bob
  val johnF = SocialNetwork.fetchProfile("id1")
  johnF.onComplete {
    case Success(johnProfile) =>
      val bobF = SocialNetwork.fetchFriend(johnProfile)
      bobF.onComplete {
        case Success(bobProfile) => johnProfile.ping(bobProfile)
        case Failure(ex) => ex.printStackTrace()
      }
    case Failure(e) => e.printStackTrace()
  }

  Thread.sleep(1000)

  // the above code is correct but quite unreadable
  // with just 2 futures nested it becomes quite complex.
  // a more complex domain like banking would make it horrendous
  //more overe the only bussiness worthy code johnProfile.ping(bobProfile) is hidden deep within the nested futures

  //better ways to do it
  // Functional composition of Futures

  //1. map
  val nameF: Future[String] = johnF.map(person => person.name)
  // the above code converts the future[Person] to futuree[String]
  // if the original future fails with an exception then the mapped future will also fail with the same exception

  //2. flatMap
  val johnsFriend: Future[Person] = johnF.flatMap(person => SocialNetwork.fetchFriend(person))
  // transforms a Future[Person] which is john  to another Future[Person] which is Bob


  //3. filter
  val filteredFriend  = johnsFriend.filter(person => person.name.startsWith("M"))
  //If the current future contains a value which satisfies the predicate, the new future will also hold that value.
  // Otherwise, the resulting future will fail with a `NoSuchElementException`.


  //because we have map, flatMap and filter we can also use for comprehensions

  val operation: Future[Unit] = for {
    mark <- SocialNetwork.fetchProfile("id1")
    bill <- SocialNetwork.fetchFriend(mark)
  } yield mark.ping(bill)

  //how to read this
  //line 1 given mark person after completing this future
  //line2 given bills profile aftero completing this future
  //line 3 mark pings bill

  Thread.sleep(2000)

  // fallbacks

  // we want to return a dummy profile if there is an exception inside the future
  val forSureProfile: Future[Person] = SocialNetwork.fetchProfile("unknownId").recover{
    case ex: Throwable => Person("dummy","dummmy")
  }

  // we want to fetch another profile if there is an exception inside the future
  val forSureProfile1: Future[Person] = SocialNetwork.fetchProfile("unknowniD").recoverWith{
    case ex: Throwable => SocialNetwork.fetchProfile("id1")
  }


  val fallBackResult = SocialNetwork.fetchProfile("unknownId").fallbackTo(SocialNetwork.fetchProfile("id1"))
  // if the first future fails then the second future is evaulauted, if that also fails then the exception of the first future is retunred
  // in the result


  // Block on a future
  // sometimes we need to ensure that an action is completed before we move on to the next step
  //like in case of a transaction

  // exmaple : online banking app

  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankApp {

    //API
    def fetchUser(name: String): Future[User] = Future {
      Thread.sleep(100)
      User(name)
    }

    def transact(user: User, receiverName: String, amount: Double): Future[Transaction] = Future{
      Thread.sleep(100) //check receiver details , user profile eetc..
      Transaction(user.name, receiverName, amount, "success")
    }

    def purchase(userName: String, receiverName: String, cost: Double): String = {
      //fetch the user
      //create a transaction
      //WAIT for the transaction to finish
      val transactionStatus = for{
        user <- fetchUser(userName)
        transaction <- transact(user, receiverName, cost)
      } yield transaction.status
      Await.result(transactionStatus, 2.seconds) //implicit conversions for Duration
    }
  }

  println(BankApp.purchase("John", "Apple", 100.0))
  //no need for thread.sleep in the main thread because the await.result is called in the main thread and it
  //blocks until all the futures are completed

  //if the future does not complete within the specific duration then await will throw an timeout exception

  // Manual manipulation of Futures with promises

  val promise = Promise[Int]() //Think of a promise as some sort of a controller over a future

  //promise has a member called future
  val future = promise.future

  //Thread #1 consumer
  future.onComplete {
    case Success(num) => println(s"[Consumer] received the number $num")
  }

  //Thread #2 Producer
  val producer = new Thread(() => {
    println("[Producer] producing the number")
    Thread.sleep(100)
    //"fulfill" the promise
    promise.success(42)
    println("[Producer] done")
  })

  producer.start()
  Thread.sleep(1000)

}
