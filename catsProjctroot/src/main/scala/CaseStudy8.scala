import cats.Id
import scala.concurrent.Future

import cats.instances.future._ // for Applicative
import cats.instances.list._ // for Traverse
import cats.syntax.traverse._ // for traverse
import scala.concurrent.ExecutionContext.Implicits.global

import cats.Applicative
import cats.syntax.functor._ // for map

object CaseStudy8 {
  trait UptimeClient[F[_]] {
    def getUptime(hostname: String): F[Int]
  }

  // add context boundary for implicit to resolve the following problem:
  // <console>:28: error: could not find implicit value for
  //               evidence parameter of type cats.Applicative[F]
  //            hostnames.traverse(client.getUptime).map(_.sum)

  // Note that we need to import cats.syntax.functor as well as cats.Applicative.
  // This is because we’re switching from using future.map to the Cats’ generic extension method that requires an implicit Functor parameter.
  //
  class UptimeService[F[_]: Applicative](client: UptimeClient[F]) {
    def getTotalUptime(hostnames: List[String]): F[Int] =
      hostnames.traverse(client.getUptime).map(_.sum)
  }

  // an asynchronous one for use in production
  class RealUptimeClient(hosts: Map[String, Int]) extends UptimeClient[Future] {
    def getUptime(hostname: String): Future[Int] =
      Future.successful(hosts.getOrElse(hostname, 0))
  }

  // a synchronous one for use in our unit tests
  class TestUptimeClient(hosts: Map[String, Int]) extends UptimeClient[Id] {
    def getUptime(hostname: String): Int = hosts.getOrElse(hostname, 0)
  }

  def testTotalUptime() = {
    val hosts = Map("host1" -> 10, "host2" -> 6)
    val client = new TestUptimeClient(hosts)
    val service = new UptimeService(client)
    val actual = service.getTotalUptime(hosts.keys.toList)
    val expected = hosts.values.sum
    assert(actual == expected)
  }
}
