package com.hanhuy.sbt

import sbt._
import Keys._

/**
  * @author pfnguyen
  */
object UnusedPlugin extends AutoPlugin {

  override def trigger = allRequirements

  override def requires = plugins.CorePlugin

  def displayScopedKey(k: Def.ScopedKey[_]) = {
    val display = Def.displayFull(k)
    if (display(0) == '{')
      Def.displayFull(k).dropWhile(_ != '}').drop(1)
    else
      display
  }


  def displayPosition(p: SourcePosition): String = p match {
    case LinePosition(path, _) => path
    case RangePosition(path, _) => path
    case _ => "<unknown>"
  }

  override def globalSettings = Seq(onLoad := onLoad.value andThen { state =>
    val e = Project.extract(state)
    val allSettings = e.session.original ++ e.session.append.flatMap { case (k,v) => v.map(_._1) }
    val localSettings = allSettings.filter { _.pos match {
      case LinePosition(_, _)   => false
      case RangePosition(_, _)  => true
    }}
    val nonLocalSettings = allSettings.filterNot(localSettings.toSet)

    val reftree = nonLocalSettings.flatMap { s =>
      val refs = references(s)
      if (refs.nonEmpty)
        List(s.key -> refs)
      else List.empty[(ScopedKey[_],List[ScopedKey[_]])]
    }

    val referenced = reftree.flatMap(_._2).toList
    val referencedSet = referenced.toSet
    val referenceMap = referenced.groupBy(_.key)
    val unused = localSettings.filterNot(l => referencedSet(l.key))
    unused.foreach { u =>
      state.log.warn("Unused " + displayScopedKey(u.key) + " defined at " + displayPosition(u.pos))
      referenceMap.get(u.key.key) foreach { rs =>
        state.log.warn("  did you mean to use:\n    " + rs.distinct.map(displayScopedKey).mkString("\n    "))
      }
    }

    state
  })

  def references(s: Setting[_]): List[ScopedKey[_]] = {
    var refs = List.empty[ScopedKey[_]]
    s.mapReferenced(
      new Def.MapScoped {
        override def apply[T](a: Def.ScopedKey[T]) = {
          if (s.key != a) {
            refs = a :: refs
          }
          a
        }
      }
    )
    refs
  }
}

