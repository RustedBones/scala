/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2009, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |                                         **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id$

package scala

/** A trait for representing total orderings.  It is important to
 * distinguish between a type that has a total order and a representation
 * of total  ordering on some type.  This trait is for representing the
 * latter.
 *
 * A <a href="http://en.wikipedia.org/wiki/Total_order">total ordering</a>
 * is a binary relation on a type <code>T</code> that is also an equivalence relation
 * and partial ordering on values of type <code>T</code>.  This relation is exposed as
 * the <code>compare</code> method of the <code>Ordering</code> trait.
 * This relation must be:
 * <ul>
 * <li>reflexive: <code>compare(x, x) == 0</code>, for any <code>x</code> of
 * type <code>T</code>.</li>
 * <li>symmetry: <code>compare(x, y) == z</code> and <code>compare(y, x) == w</code>
 * then <code>Math.signum(z) == -Math.signum(w)</code>, for any <code>x</code> and <code>y</code> of
 * type <code>T</code> and <code>z</code> and <code>w</code> of type <code>Int</code>.</li>
 * <li>transitive: if <code>compare(x, y) == z</code> and <code>compare(y, w) == v</code>
 * and <code>Math.signum(z) &gt;= 0</code> and <code>Math.signum(v) &gt;= 0</code> then
 * <code>compare(x, w) == u</code> and <code>Math.signum(z + v) == Math.signum(u)</code>,
 * for any <code>x</code>, <code>y</code>,
 * and <code>w</code> of type <code>T</code> and <code>z</code>, <code>v</code>, and <code>u</code>
 * of type <code>Int</code>.</li>
 * </ul>
 *
 * @author Geoffrey Washburn
 * @version 0.9.5, 2008-04-15
 */

trait Ordering[T] extends PartialOrdering[T] {
 /** Returns a negative integer iff <code>x</code> comes before
   * <code>y</code> in the ordering, returns 0 iff <code>x</code>
   * is the same in the ordering as <code>y</code>, and returns a
   * positive number iff <code>x</code> comes after
   * <code>y</code> in the ordering.
   */
  def compare(x: T, y: T): Int

 /** Returns <code>true</code> iff <code>x</code> comes before
   *  <code>y</code> in the ordering.
   */
  override def lteq(x: T, y: T): Boolean = compare(x, y) <= 0

  /** Returns <code>true</code> iff <code>y</code> comes before
   *  <code>x</code> in the ordering.
   */
  override def gteq(x: T, y: T): Boolean = compare(x, y) >= 0

  /** Returns <code>true</code> iff <code>x</code> comes before
   *  <code>y</code> in the ordering and is not the same as <code>y</code>.
   */
  override def lt(x: T, y: T): Boolean = compare(x, y) < 0

  /** Returns <code>true</code> iff <code>y</code> comes before
   *  <code>x</code> in the ordering and is not the same as <code>x</code>.
   */
  override def gt(x: T, y: T): Boolean = compare(x, y) > 0

  /** Returns <code>true</code> iff <code>x</code> is equivalent to
   *  <code>y</code> in the ordering.
   */
  override def equiv(x: T, y: T): Boolean = compare(x, y) == 0

  class Ops(lhs: T) {
    def <(rhs: T) = lt(lhs, rhs)
    def <=(rhs: T) = lteq(lhs, rhs)
    def >(rhs: T) = gt(lhs, rhs)
    def >=(rhs: T) = gteq(lhs, rhs)
    def ===(rhs: T) = equiv(lhs, rhs)
    def !==(rhs: T) = !equiv(lhs, rhs)
  }
  implicit def mkOrderingOps(lhs: T): Ops = new Ops(lhs)
}

object Ordering
{
  def apply[T](implicit ord : Ordering[T]) = ord

  implicit val Unit : Ordering[Unit] = new Ordering[Unit] {
    def compare(x : Unit, y : Unit) = 0;
  }

  implicit val Boolean : Ordering[Boolean] = new Ordering[Boolean] {
    def compare(x : Boolean, y : Boolean) = (x, y) match {
      case (false, true) => -1;
      case (true, false) => 1;
      case _ => 0;
    }
  }

  implicit val Byte : Ordering[Byte] = new Ordering[Byte] {
    def compare(x : Byte, y : Byte) = x.toInt - y.toInt;
  }

  implicit val Char : Ordering[Char] = new Ordering[Char] {
    def compare(x : Char, y : Char) = x.toInt - y.toInt;
  }

  implicit val Short : Ordering[Short] = new Ordering[Short] {
    def compare(x : Short, y : Short) = x.toInt - y.toInt;
  }

  implicit val Int : Ordering[Int] = new Ordering[Int] {
    def compare(x : Int, y : Int) =
      if(x < y) -1;
      else if (x == y) 0;
      else 1
  }

  implicit val Long : Ordering[Long] = new Ordering[Long] {
    def compare(x : Long, y : Long) =
      if(x < y) -1;
      else if (x == y) 0;
      else 1
  }

  implicit val Float : Ordering[Float] = new Ordering[Float] {
    def compare(x : Float, y : Float) =
      if(x < y) -1;
      else if (x == y) 0;
      else 1
  }

  implicit val Double : Ordering[Double] = new Ordering[Double] {
    def compare(x : Double, y : Double) =
      if(x < y) -1;
      else if (x == y) 0;
      else 1
  }

//  implicit val BigInt : Ordering[BigInt] = new Ordering[BigInt] {
//    def compare(x : BigInt, y : BigInt) = x.compare(y);
//  }

  implicit val String : Ordering[String] = new Ordering[String] {
    def compare(x : String, y : String) = x.compareTo(y);
  }

  implicit def Option[T](implicit ord : Ordering[T]) : Ordering[Option[T]] =
    new Ordering[Option[T]] {
      def compare(x : Option[T], y : Option[T]) = (x, y) match {
        case (None, None) => 0;
        case (None, _) => -1;
        case (_, None) => 1
        case (Some(x), Some(y)) => ord.compare(x, y);
      }
    }

  implicit def Iterable[T](implicit ord : Ordering[T]) : Ordering[Iterable[T]] =
  new Ordering[Iterable[T]] {
    def compare(x : Iterable[T], y : Iterable[T]) : Int = {
      val xe = x.iterator;
      val ye = y.iterator;

      while (xe.hasNext && ye.hasNext){
        val res = ord.compare(xe.next, ye.next);
        if (res != 0) return res;
      }

      Boolean.compare(xe.hasNext, ye.hasNext);
    }
  }

  implicit def Tuple2[T1, T2](implicit ord1 : Ordering[T1], ord2 : Ordering[T2]) : Ordering[(T1, T2)] =
    new Ordering[(T1, T2)]{
      def compare(x : Tuple2[T1, T2], y : Tuple2[T1, T2]) : Int = {
        val compare1 = ord1.compare(x._1, y._1);
        if (compare1 != 0) return compare1;
        val compare2 = ord2.compare(x._2, y._2);
        if (compare2 != 0) return compare2;
        0;
      }
    }

  implicit def Tuple3[T1, T2, T3](implicit ord1 : Ordering[T1], ord2 : Ordering[T2], ord3 : Ordering[T3]) : Ordering[(T1, T2, T3)] =
    new Ordering[(T1, T2, T3)]{
      def compare(x : Tuple3[T1, T2, T3], y : Tuple3[T1, T2, T3]) : Int = {
        val compare1 = ord1.compare(x._1, y._1);
        if (compare1 != 0) return compare1;
        val compare2 = ord2.compare(x._2, y._2);
        if (compare2 != 0) return compare2;
        val compare3 = ord3.compare(x._3, y._3);
        if (compare3 != 0) return compare3;
        0;
      }
    }

  implicit def Tuple4[T1, T2, T3, T4](implicit ord1 : Ordering[T1], ord2 : Ordering[T2], ord3 : Ordering[T3], ord4 : Ordering[T4]) : Ordering[(T1, T2, T3, T4)] =
    new Ordering[(T1, T2, T3, T4)]{
      def compare(x : Tuple4[T1, T2, T3, T4], y : Tuple4[T1, T2, T3, T4]) : Int = {
        val compare1 = ord1.compare(x._1, y._1);
        if (compare1 != 0) return compare1;
        val compare2 = ord2.compare(x._2, y._2);
        if (compare2 != 0) return compare2;
        val compare3 = ord3.compare(x._3, y._3);
        if (compare3 != 0) return compare3;
        val compare4 = ord4.compare(x._4, y._4);
        if (compare4 != 0) return compare4;
        0;
      }
    }

  implicit def Tuple5[T1, T2, T3, T4, T5](implicit ord1 : Ordering[T1], ord2 : Ordering[T2], ord3 : Ordering[T3], ord4 : Ordering[T4], ord5 : Ordering[T5]) : Ordering[(T1, T2, T3, T4, T5)] =
    new Ordering[(T1, T2, T3, T4, T5)]{
      def compare(x : Tuple5[T1, T2, T3, T4, T5], y : Tuple5[T1, T2, T3, T4, T5]) : Int = {
        val compare1 = ord1.compare(x._1, y._1);
        if (compare1 != 0) return compare1;
        val compare2 = ord2.compare(x._2, y._2);
        if (compare2 != 0) return compare2;
        val compare3 = ord3.compare(x._3, y._3);
        if (compare3 != 0) return compare3;
        val compare4 = ord4.compare(x._4, y._4);
        if (compare4 != 0) return compare4;
        val compare5 = ord5.compare(x._5, y._5);
        if (compare5 != 0) return compare5;
        0;
      }
    }

  implicit def Tuple6[T1, T2, T3, T4, T5, T6](implicit ord1 : Ordering[T1], ord2 : Ordering[T2], ord3 : Ordering[T3], ord4 : Ordering[T4], ord5 : Ordering[T5], ord6 : Ordering[T6]) : Ordering[(T1, T2, T3, T4, T5, T6)] =
    new Ordering[(T1, T2, T3, T4, T5, T6)]{
      def compare(x : Tuple6[T1, T2, T3, T4, T5, T6], y : Tuple6[T1, T2, T3, T4, T5, T6]) : Int = {
        val compare1 = ord1.compare(x._1, y._1);
        if (compare1 != 0) return compare1;
        val compare2 = ord2.compare(x._2, y._2);
        if (compare2 != 0) return compare2;
        val compare3 = ord3.compare(x._3, y._3);
        if (compare3 != 0) return compare3;
        val compare4 = ord4.compare(x._4, y._4);
        if (compare4 != 0) return compare4;
        val compare5 = ord5.compare(x._5, y._5);
        if (compare5 != 0) return compare5;
        val compare6 = ord6.compare(x._6, y._6);
        if (compare6 != 0) return compare6;
        0;
      }
    }

  implicit def Tuple7[T1, T2, T3, T4, T5, T6, T7](implicit ord1 : Ordering[T1], ord2 : Ordering[T2], ord3 : Ordering[T3], ord4 : Ordering[T4], ord5 : Ordering[T5], ord6 : Ordering[T6], ord7 : Ordering[T7]) : Ordering[(T1, T2, T3, T4, T5, T6, T7)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7)]{
      def compare(x : Tuple7[T1, T2, T3, T4, T5, T6, T7], y : Tuple7[T1, T2, T3, T4, T5, T6, T7]) : Int = {
        val compare1 = ord1.compare(x._1, y._1);
        if (compare1 != 0) return compare1;
        val compare2 = ord2.compare(x._2, y._2);
        if (compare2 != 0) return compare2;
        val compare3 = ord3.compare(x._3, y._3);
        if (compare3 != 0) return compare3;
        val compare4 = ord4.compare(x._4, y._4);
        if (compare4 != 0) return compare4;
        val compare5 = ord5.compare(x._5, y._5);
        if (compare5 != 0) return compare5;
        val compare6 = ord6.compare(x._6, y._6);
        if (compare6 != 0) return compare6;
        val compare7 = ord7.compare(x._7, y._7);
        if (compare7 != 0) return compare7;
        0;
      }
    }

  implicit def Tuple8[T1, T2, T3, T4, T5, T6, T7, T8](implicit ord1 : Ordering[T1], ord2 : Ordering[T2], ord3 : Ordering[T3], ord4 : Ordering[T4], ord5 : Ordering[T5], ord6 : Ordering[T6], ord7 : Ordering[T7], ord8 : Ordering[T8]) : Ordering[(T1, T2, T3, T4, T5, T6, T7, T8)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8)]{
      def compare(x : Tuple8[T1, T2, T3, T4, T5, T6, T7, T8], y : Tuple8[T1, T2, T3, T4, T5, T6, T7, T8]) : Int = {
        val compare1 = ord1.compare(x._1, y._1);
        if (compare1 != 0) return compare1;
        val compare2 = ord2.compare(x._2, y._2);
        if (compare2 != 0) return compare2;
        val compare3 = ord3.compare(x._3, y._3);
        if (compare3 != 0) return compare3;
        val compare4 = ord4.compare(x._4, y._4);
        if (compare4 != 0) return compare4;
        val compare5 = ord5.compare(x._5, y._5);
        if (compare5 != 0) return compare5;
        val compare6 = ord6.compare(x._6, y._6);
        if (compare6 != 0) return compare6;
        val compare7 = ord7.compare(x._7, y._7);
        if (compare7 != 0) return compare7;
        val compare8 = ord8.compare(x._8, y._8);
        if (compare8 != 0) return compare8;
        0;
      }
    }

  implicit def Tuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9](implicit ord1 : Ordering[T1], ord2 : Ordering[T2], ord3 : Ordering[T3], ord4 : Ordering[T4], ord5 : Ordering[T5], ord6 : Ordering[T6], ord7 : Ordering[T7], ord8 : Ordering[T8], ord9 : Ordering[T9]) : Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9)] =
    new Ordering[(T1, T2, T3, T4, T5, T6, T7, T8, T9)]{
      def compare(x : Tuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9], y : Tuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9]) : Int = {
        val compare1 = ord1.compare(x._1, y._1);
        if (compare1 != 0) return compare1;
        val compare2 = ord2.compare(x._2, y._2);
        if (compare2 != 0) return compare2;
        val compare3 = ord3.compare(x._3, y._3);
        if (compare3 != 0) return compare3;
        val compare4 = ord4.compare(x._4, y._4);
        if (compare4 != 0) return compare4;
        val compare5 = ord5.compare(x._5, y._5);
        if (compare5 != 0) return compare5;
        val compare6 = ord6.compare(x._6, y._6);
        if (compare6 != 0) return compare6;
        val compare7 = ord7.compare(x._7, y._7);
        if (compare7 != 0) return compare7;
        val compare8 = ord8.compare(x._8, y._8);
        if (compare8 != 0) return compare8;
        val compare9 = ord9.compare(x._9, y._9);
        if (compare9 != 0) return compare9;
        0;
      }
    }

}
