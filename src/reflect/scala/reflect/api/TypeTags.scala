/*
 * Scala (https://www.scala-lang.org)
 *
 * Copyright EPFL and Lightbend, Inc.
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package scala
package reflect
package api

import java.io.ObjectStreamException

/**
 * A `TypeTag[T]` encapsulates the runtime type representation of some type `T`.
 * Like [[scala.reflect.Manifest]], the prime use case of `TypeTag`s is to give access
 * to erased types. However, `TypeTag`s should be considered to be a richer
 * replacement of the pre-2.10 notion of a [[scala.reflect.Manifest Manifest]], that
 * are, in addition, fully integrated with Scala reflection.
 *
 * There exist three different types of `TypeTags`:
 *
 *  <ul>
 *  <li>[[scala.reflect.api.TypeTags#TypeTag]]. <br/>A full type descriptor of a Scala type.
 *  For example, a `TypeTag[List[String]]` contains all type information,
 *  in this case, of type `scala.List[String]`.</li>
 *
 *  <li>[[scala.reflect.ClassTag]]. <br/>A partial type descriptor of a Scala type. For
 *  example, a `ClassTag[List[String]]` contains only the erased class
 *  type information, in this case, of type `scala.collection.immutable.List`.
 *  `ClassTag`s provide access only to the runtime class of a type.
 *  Analogous to [[scala.reflect.ClassManifest]]</li>
 *
 *  <li>[[scala.reflect.api.TypeTags#WeakTypeTag]]. <br/>A type descriptor for abstract
 *  types (see description below).</li>
 *  </ul>
 *
 * Like [[scala.reflect.Manifest Manifest]]s, `TypeTag`s are always generated by the
 * compiler, and can be obtained in three ways:
 *
 * === #1 Via the methods [[scala.reflect.api.TypeTags#typeTag typeTag]],
 * [[scala.reflect#classTag classTag]], or [[scala.reflect.api.TypeTags#weakTypeTag weakTypeTag]] ===
 *
 * For example:
 * {{{
 *   import scala.reflect.runtime.universe._
 *   val tt = typeTag[Int]
 *
 *   import scala.reflect._
 *   val ct = classTag[String]
 * }}}
 *
 * Each of these methods constructs a `TypeTag[T]` or `ClassTag[T]` for the given
 * type argument `T`.
 *
 * === #2 Using an implicit parameter of type `TypeTag[T]`, `ClassTag[T]`, or `WeakTypeTag[T]`
 *
 * For example:
 * {{{
 *   import scala.reflect.runtime.universe._
 *
 *   def paramInfo[T](x: T)(implicit tag: TypeTag[T]): Unit = {
 *     val targs = tag.tpe match { case TypeRef(_, _, args) => args }
 *     println(s"type of \$x has type arguments \$targs")
 *   }
 *
 *   scala> paramInfo(42)
 *   type of 42 has type arguments List()
 *
 *   scala> paramInfo(List(1, 2))
 *   type of List(1, 2) has type arguments List(Int)
 * }}}
 *
 * === #3 Context bound of a type parameter ===
 *
 * ...on methods or classes. The above example can be implemented as follows:
 *
 * {{{
 *   import scala.reflect.runtime.universe._
 *
 *   def paramInfo[T: TypeTag](x: T): Unit = {
 *     val targs = typeOf[T] match { case TypeRef(_, _, args) => args }
 *     println(s"type of \$x has type arguments \$targs")
 *   }
 *
 *   scala> paramInfo(42)
 *   type of 42 has type arguments List()
 *
 *   scala> paramInfo(List(1, 2))
 *   type of List(1, 2) has type arguments List(Int)
 * }}}
 *
 * === `WeakTypeTag`s ===
 *
 *`WeakTypeTag[T]` generalizes `TypeTag[T]`. Unlike a regular `TypeTag`, components of
 * its type representation can be references to type parameters or abstract types.
 * However, `WeakTypeTag[T]` tries to be as concrete as possible, i.e. if type tags
 * are available for the referenced type arguments or abstract types, they are used to
 * embed the concrete types into the `WeakTypeTag[T]`.
 *
 * Continuing the example above:
 * {{{
 * def weakParamInfo[T](x: T)(implicit tag: WeakTypeTag[T]): Unit = {
 *   val targs = tag.tpe match { case TypeRef(_, _, args) => args }
 *   println(s"type of \$x has type arguments \$targs")
 * }
 *
 * scala> def foo[T] = weakParamInfo(List[T]())
 * foo: [T]=> Unit
 *
 * scala> foo[Int]
 * type of List() has type arguments List(T)
 * }}}
 *
 * === TypeTags and Manifests ===
 *
 * `TypeTag`s correspond loosely to the pre-2.10 notion of
 * [[scala.reflect.Manifest]]s. While [[scala.reflect.ClassTag]] corresponds to
 * [[scala.reflect.ClassManifest]] and [[scala.reflect.api.TypeTags#TypeTag]] mostly
 * corresponds to [[scala.reflect.Manifest]], other pre-2.10 `Manifest` types do not
 * have a direct correspondence with a 2.10 "`Tag`" type.
 *
 * <ul>
 * <li>'''[[scala.reflect.OptManifest]] is not supported.''' <br/>This is because `Tag`s
 * can reify arbitrary types, so they are always available.<li>
 *
 * <li>'''There is no equivalent for [[scala.reflect.AnyValManifest]].''' <br/>Instead, one
 * can compare their `Tag` with one of the base `Tag`s (defined in the corresponding
 * companion objects) in order to find out whether or not it represents a primitive
 * value class. Additionally, it's possible to simply use
 * `<tag>.tpe.typeSymbol.isPrimitiveValueClass`.</li>
 *
 * <li>'''There are no replacement for factory methods defined in the `Manifest`
 * companion objects'''. <br/>Instead, one could generate corresponding types using the
 * reflection APIs provided by Java (for classes) and Scala (for types).</li>
 *
 * <li>'''Certain manifest operations(i.e., <:<, >:> and typeArguments) are not
 * supported.''' <br/>Instead, one could use the reflection APIs provided by Java (for
 * classes) and Scala (for types).</li>
 *</ul>
 *
 * In Scala 2.10, [[scala.reflect.ClassManifest]]s are deprecated, and it is planned
 * to deprecate [[scala.reflect.Manifest]] in favor of `TypeTag`s and `ClassTag`s in
 * an upcoming point release. Thus, it is advisable to migrate any `Manifest`-based
 * APIs to use `Tag`s.
 *
 * For more information about `TypeTag`s, see the
 * [[http://docs.scala-lang.org/overviews/reflection/typetags-manifests.html Reflection Guide: TypeTags]]
 *
 * @see [[scala.reflect.ClassTag]], [[scala.reflect.api.TypeTags#TypeTag]], [[scala.reflect.api.TypeTags#WeakTypeTag]]
 * @group ReflectionAPI
 */
trait TypeTags { self: Universe =>

  import definitions._

  /**
   * If an implicit value of type `WeakTypeTag[T]` is required, the compiler will create one,
   * and the reflective representation of `T` can be accessed via the `tpe` field.
   * Components of `T` can be references to type parameters or abstract types. Note that `WeakTypeTag`
   * makes an effort to be as concrete as possible, i.e. if `TypeTag`s are available for the referenced type arguments
   * or abstract types, they are used to embed the concrete types into the WeakTypeTag. Otherwise the WeakTypeTag will
   * contain a reference to an abstract type. This behavior can be useful, when one expects `T` to be perhaps be partially
   * abstract, but requires special care to handle this case. However, if `T` is expected to be fully known, use
   * [[scala.reflect.api.TypeTags#TypeTag]] instead, which statically guarantees this property.
   *
   * For more information about `TypeTag`s, see the
   * [[http://docs.scala-lang.org/overviews/reflection/typetags-manifests.html Reflection Guide: TypeTags]]
   *
   * @see [[scala.reflect.api.TypeTags]]
   * @group TypeTags
   */
  @annotation.implicitNotFound(msg = "No WeakTypeTag available for ${T}")
  trait WeakTypeTag[T] extends Equals with Serializable {
    /**
     * The underlying `Mirror` of this type tag.
     */
    val mirror: Mirror

    /**
     * Migrates the expression into another mirror, jumping into a different universe if necessary.
     *
     * Migration means that all symbolic references to classes/objects/packages in the expression
     * will be re-resolved within the new mirror (typically using that mirror's classloader).
     */
    def in[U <: Universe with Singleton](otherMirror: scala.reflect.api.Mirror[U]): U # WeakTypeTag[T]

    /**
     * Reflective representation of type T.
     */
    def tpe: Type

    override def canEqual(x: Any) = x.isInstanceOf[WeakTypeTag[_]]

    override def equals(x: Any) = x.isInstanceOf[WeakTypeTag[_]] && this.mirror == x.asInstanceOf[WeakTypeTag[_]].mirror && this.tpe == x.asInstanceOf[WeakTypeTag[_]].tpe

    override def hashCode = mirror.hashCode * 31 + tpe.hashCode

    override def toString = "WeakTypeTag[" + tpe + "]"
  }

  /**
   * Type tags corresponding to primitive types and constructor/extractor for WeakTypeTags.
   * @group TypeTags
   */
  object WeakTypeTag {
    val Byte    : WeakTypeTag[scala.Byte]       = TypeTag.Byte
    val Short   : WeakTypeTag[scala.Short]      = TypeTag.Short
    val Char    : WeakTypeTag[scala.Char]       = TypeTag.Char
    val Int     : WeakTypeTag[scala.Int]        = TypeTag.Int
    val Long    : WeakTypeTag[scala.Long]       = TypeTag.Long
    val Float   : WeakTypeTag[scala.Float]      = TypeTag.Float
    val Double  : WeakTypeTag[scala.Double]     = TypeTag.Double
    val Boolean : WeakTypeTag[scala.Boolean]    = TypeTag.Boolean
    val Unit    : WeakTypeTag[scala.Unit]       = TypeTag.Unit
    val Any     : WeakTypeTag[scala.Any]        = TypeTag.Any
    val AnyVal  : WeakTypeTag[scala.AnyVal]     = TypeTag.AnyVal
    val AnyRef  : WeakTypeTag[scala.AnyRef]     = TypeTag.AnyRef
    val Object  : WeakTypeTag[java.lang.Object] = TypeTag.Object
    val Nothing : WeakTypeTag[scala.Nothing]    = TypeTag.Nothing
    val Null    : WeakTypeTag[scala.Null]       = TypeTag.Null


    def apply[T](mirror1: scala.reflect.api.Mirror[self.type], tpec1: TypeCreator): WeakTypeTag[T] =
      new WeakTypeTagImpl[T](mirror1.asInstanceOf[Mirror], tpec1)

    def unapply[T](ttag: WeakTypeTag[T]): Option[Type] = Some(ttag.tpe)
  }

  /* @group TypeTags */
  private class WeakTypeTagImpl[T](val mirror: Mirror, val tpec: TypeCreator) extends WeakTypeTag[T] {
    lazy val tpe: Type = tpec(mirror)
    def in[U <: Universe with Singleton](otherMirror: scala.reflect.api.Mirror[U]): U # WeakTypeTag[T] = {
      val otherMirror1 = otherMirror.asInstanceOf[scala.reflect.api.Mirror[otherMirror.universe.type]]
      otherMirror.universe.WeakTypeTag[T](otherMirror1, tpec)
    }
    @throws(classOf[ObjectStreamException])
    private def writeReplace(): AnyRef = new SerializedTypeTag(tpec, concrete = false)
  }

  /**
   * A `TypeTag` is a [[scala.reflect.api.TypeTags#WeakTypeTag]] with the additional
   * static guarantee that all type references are concrete, i.e. it does <b>not</b> contain any references to
   * unresolved type parameters or abstract types.
   *
   * @see [[scala.reflect.api.TypeTags]]
   * @group TypeTags
   */
  @annotation.implicitNotFound(msg = "No TypeTag available for ${T}")
  trait TypeTag[T] extends WeakTypeTag[T] with Equals with Serializable {
    /**
     * @inheritdoc
     */
    override def in[U <: Universe with Singleton](otherMirror: scala.reflect.api.Mirror[U]): U # TypeTag[T]

    override def canEqual(x: Any) = x.isInstanceOf[TypeTag[_]]

    override def equals(x: Any) = x.isInstanceOf[TypeTag[_]] && this.mirror == x.asInstanceOf[TypeTag[_]].mirror && this.tpe == x.asInstanceOf[TypeTag[_]].tpe

    override def hashCode = mirror.hashCode * 31 + tpe.hashCode

    override def toString = "TypeTag[" + tpe + "]"
  }

  /**
   * Type tags corresponding to primitive types and constructor/extractor for WeakTypeTags.
   * @group TypeTags
   */
  object TypeTag {
    val Byte:    TypeTag[scala.Byte]       = new PredefTypeTag[scala.Byte]       (ByteTpe,    _.TypeTag.Byte)
    val Short:   TypeTag[scala.Short]      = new PredefTypeTag[scala.Short]      (ShortTpe,   _.TypeTag.Short)
    val Char:    TypeTag[scala.Char]       = new PredefTypeTag[scala.Char]       (CharTpe,    _.TypeTag.Char)
    val Int:     TypeTag[scala.Int]        = new PredefTypeTag[scala.Int]        (IntTpe,     _.TypeTag.Int)
    val Long:    TypeTag[scala.Long]       = new PredefTypeTag[scala.Long]       (LongTpe,    _.TypeTag.Long)
    val Float:   TypeTag[scala.Float]      = new PredefTypeTag[scala.Float]      (FloatTpe,   _.TypeTag.Float)
    val Double:  TypeTag[scala.Double]     = new PredefTypeTag[scala.Double]     (DoubleTpe,  _.TypeTag.Double)
    val Boolean: TypeTag[scala.Boolean]    = new PredefTypeTag[scala.Boolean]    (BooleanTpe, _.TypeTag.Boolean)
    val Unit:    TypeTag[scala.Unit]       = new PredefTypeTag[scala.Unit]       (UnitTpe,    _.TypeTag.Unit)
    val Any:     TypeTag[scala.Any]        = new PredefTypeTag[scala.Any]        (AnyTpe,     _.TypeTag.Any)
    val AnyVal:  TypeTag[scala.AnyVal]     = new PredefTypeTag[scala.AnyVal]     (AnyValTpe,  _.TypeTag.AnyVal)
    val AnyRef:  TypeTag[scala.AnyRef]     = new PredefTypeTag[scala.AnyRef]     (AnyRefTpe,  _.TypeTag.AnyRef)
    val Object:  TypeTag[java.lang.Object] = new PredefTypeTag[java.lang.Object] (ObjectTpe,  _.TypeTag.Object)
    val Nothing: TypeTag[scala.Nothing]    = new PredefTypeTag[scala.Nothing]    (NothingTpe, _.TypeTag.Nothing)
    val Null:    TypeTag[scala.Null]       = new PredefTypeTag[scala.Null]       (NullTpe,    _.TypeTag.Null)

    def apply[T](mirror1: scala.reflect.api.Mirror[self.type], tpec1: TypeCreator): TypeTag[T] = {
      (mirror1: AnyRef) match {
        case m: scala.reflect.runtime.JavaMirrors#JavaMirror
          if cacheMaterializedTypeTags && tpec1.getClass.getName.contains("$typecreator")
            && tpec1.getClass.getDeclaredFields.length == 0 => // excludes type creators that splice in bound types.

          m.typeTag(tpec1).asInstanceOf[TypeTag[T]]
        case _ =>
          new TypeTagImpl[T](mirror1.asInstanceOf[Mirror], tpec1)
      }
    }
    def unapply[T](ttag: TypeTag[T]): Option[Type] = Some(ttag.tpe)

    private val cacheMaterializedTypeTags = !java.lang.Boolean.getBoolean("scala.reflect.runtime.disable.typetag.cache")
  }
  private[reflect] def TypeTagImpl[T](mirror: Mirror, tpec: TypeCreator): TypeTag[T] = new TypeTagImpl[T](mirror, tpec)
  /* @group TypeTags */
  private class TypeTagImpl[T](mirror: Mirror, tpec: TypeCreator) extends WeakTypeTagImpl[T](mirror, tpec) with TypeTag[T] {
    override def in[U <: Universe with Singleton](otherMirror: scala.reflect.api.Mirror[U]): U # TypeTag[T] = {
      val otherMirror1 = otherMirror.asInstanceOf[scala.reflect.api.Mirror[otherMirror.universe.type]]
      otherMirror.universe.TypeTag[T](otherMirror1, tpec)
    }
    @throws(classOf[ObjectStreamException])
    private def writeReplace(): AnyRef = new SerializedTypeTag(tpec, concrete = true)
  }

  /* @group TypeTags */
  // This class only exists to silence MIMA complaining about a binary incompatibility.
  // Only the top-level class (api.PredefTypeCreator) should be used.
  @deprecated("This class only exists to silence MIMA complaining about a binary incompatibility.", since="forever")
  private class PredefTypeCreator[T](copyIn: Universe => Universe#TypeTag[T]) extends TypeCreator {
    def apply[U <: Universe with Singleton](m: scala.reflect.api.Mirror[U]): U # Type = {
      copyIn(m.universe).asInstanceOf[U # TypeTag[T]].tpe
    }
  }

  /* @group TypeTags */
  private class PredefTypeTag[T](_tpe: Type, copyIn: Universe => Universe#TypeTag[T]) extends TypeTagImpl[T](rootMirror, new api.PredefTypeCreator(copyIn)) {
    override lazy val tpe: Type = _tpe
    @throws(classOf[ObjectStreamException])
    private def writeReplace(): AnyRef = new SerializedTypeTag(tpec, concrete = true)
  }

  /**
   * Shortcut for `implicitly[WeakTypeTag[T]]`
   * @group TypeTags
   */
  def weakTypeTag[T](implicit attag: WeakTypeTag[T]) = attag

  /**
   * Shortcut for `implicitly[TypeTag[T]]`
   * @group TypeTags
   */
  def typeTag[T](implicit ttag: TypeTag[T]) = ttag

  // big thanks to Viktor Klang for this brilliant idea!
  /**
   * Shortcut for `implicitly[WeakTypeTag[T]].tpe`
   * @group TypeTags
   */
  def weakTypeOf[T](implicit attag: WeakTypeTag[T]): Type = attag.tpe

  /**
   * Shortcut for `implicitly[TypeTag[T]].tpe`
   * @group TypeTags
   */
  def typeOf[T](implicit ttag: TypeTag[T]): Type = ttag.tpe

  /**
   * Type symbol of `x` as derived from a type tag.
   * @group TypeTags
   */
  def symbolOf[T: WeakTypeTag]: TypeSymbol
}

// This class should be final, but we can't do that in Scala 2.11.x without breaking
// binary incompatibility.
@SerialVersionUID(1L)
private[scala] class SerializedTypeTag(var tpec: TypeCreator, var concrete: Boolean) extends Serializable {
  import scala.reflect.runtime.universe.{TypeTag, WeakTypeTag, runtimeMirror}
  @throws(classOf[ObjectStreamException])
  private def readResolve(): AnyRef = {
    val loader: ClassLoader = try {
      Thread.currentThread().getContextClassLoader()
    } catch {
      case se: SecurityException => null
    }
    val m = runtimeMirror(loader)
    if (concrete) TypeTag(m, tpec)
    else WeakTypeTag(m, tpec)
  }
}

/* @group TypeTags */
private class PredefTypeCreator[T](copyIn: Universe => Universe#TypeTag[T]) extends TypeCreator {
  def apply[U <: Universe with Singleton](m: scala.reflect.api.Mirror[U]): U # Type = {
    copyIn(m.universe).asInstanceOf[U # TypeTag[T]].tpe
  }
}
