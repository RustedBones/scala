class A { def a = 0 }
trait B extends A { println(super[A].a) }
object Test extends Application {
  new B {}
}

