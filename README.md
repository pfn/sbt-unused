# sbt-unused

An SBT plugin to let you know when you mess up

## Usage

`addSbtPlugin("com.hanhuy.sbt" % "sbt-unused" % "0.1")`

## Why?

I believe setting scope errors are the number 1 reason why SBT is
considered hard to use and mystical (barring the old issues with the
various operators having less than obvious meanings)

## What it does

Inspect all locally defined settings, that is every setting that is not
defined within plugins and `sbt.Build` (which you **should no longer be
using**!), for usage. If any settings are unused, warnings will be
emitted.

If warnings are present, a list of possible alternatives will be
presented if a different scoping of the setting is used.

## Example output

Given a `build.sbt` that looks like this:

```
javacOptions += "-deprecation"

UnusedSampleBuild.settings

name := version.value + scalaVersion.value
```

And a `build.scala` that contains the `UnusedSampleBuild` definition:

```
import sbt._, Keys._
object UnusedSampleBuild {
    val settings = Seq(
        scalacOptions += "-feature",
        fork in run := true
    )
}
```

Running this command:

```
unused-sample $ sbt 'set javacOptions += "uggh"' exit
```

Produces this output:

```
[info] Loading global plugins from $HOME\.sbt\0.13\plugins
[info] Updating {file:/$HOME/.sbt/0.13/plugins/}global-plugins...
[info] Resolving ...
[info] Done updating.
[info] Loading project definition from $HOME\src\unused-sample\project
[info] Updating default:unused-sample-build:0.1-SNAPSHOT
[info] Resolved default:unused-sample-build:0.1-SNAPSHOT dependencies
[info] Fetching artifacts of default:unused-sample-build:0.1-SNAPSHOT
[info] Fetched artifacts of default:unused-sample-build:0.1-SNAPSHOT
[info] Set current project to 0.1-SNAPSHOT2.10.4 (in build file:/$HOME/src/unused-sample/)
[warn] Unused unused-sample/*:sonatypeProfileName defined at $HOME\.sbt\0.13\sonatype.sbt
[warn] Unused unused-sample/*:javacOptions defined at $HOME\src\unused-sample\build.sbt
[warn]   did you mean to use:
[warn]     unused-sample/compile:doc::javacOptions
[warn]     unused-sample/compile:compile::javacOptions
[warn]     unused-sample/test:doc::javacOptions
[warn]     unused-sample/test:compile::javacOptions
[warn] Unused unused-sample/*:scalacOptions defined at $HOME\src\unused-sample\build.sbt
[warn]   did you mean to use:
[warn]     unused-sample/compile:doc::scalacOptions
[warn]     unused-sample/compile:compile::scalacOptions
[warn]     unused-sample/compile:console::scalacOptions
[warn]     unused-sample/compile:consoleQuick::scalacOptions
[warn]     unused-sample/test:doc::scalacOptions
[warn]     unused-sample/test:compile::scalacOptions
[warn]     unused-sample/test:console::scalacOptions
[warn]     unused-sample/test:consoleQuick::scalacOptions
[warn]     unused-sample/compile:scalacOptions
[warn] Unused unused-sample/*:run::fork defined at $HOME\src\unused-sample\build.sbt
[warn]   did you mean to use:
[warn]     unused-sample/*:fork
[warn]     unused-sample/compile:run::fork
[warn]     unused-sample/test:run::fork
[warn]     */*:fork
[info] Defining *:javacOptions
[info] The new value will be used by compile:compile::compileInputs, compile:doc and 1 others.
[info]  Run `last` for details.
[info] Reapplying settings...
[info] Set current project to 0.1-SNAPSHOT2.10.4 (in build file:/$HOME/src/unused-sample/)
[warn] Unused unused-sample/*:sonatypeProfileName defined at $HOME\.sbt\0.13\sonatype.sbt
[warn] Unused unused-sample/*:javacOptions defined at $HOME\src\unused-sample\build.sbt
[warn]   did you mean to use:
[warn]     unused-sample/compile:doc::javacOptions
[warn]     unused-sample/compile:compile::javacOptions
[warn]     unused-sample/test:doc::javacOptions
[warn]     unused-sample/test:compile::javacOptions
[warn] Unused unused-sample/*:scalacOptions defined at $HOME\src\unused-sample\build.sbt
[warn]   did you mean to use:
[warn]     unused-sample/compile:doc::scalacOptions
[warn]     unused-sample/compile:compile::scalacOptions
[warn]     unused-sample/compile:console::scalacOptions
[warn]     unused-sample/compile:consoleQuick::scalacOptions
[warn]     unused-sample/test:doc::scalacOptions
[warn]     unused-sample/test:compile::scalacOptions
[warn]     unused-sample/test:console::scalacOptions
[warn]     unused-sample/test:consoleQuick::scalacOptions
[warn]     unused-sample/compile:scalacOptions
[warn] Unused unused-sample/*:run::fork defined at $HOME\src\unused-sample\build.sbt
[warn]   did you mean to use:
[warn]     unused-sample/*:fork
[warn]     unused-sample/compile:run::fork
[warn]     unused-sample/test:run::fork
[warn]     */*:fork
[warn] Unused unused-sample/*:javacOptions defined at <set>
[warn]   did you mean to use:
[warn]     unused-sample/compile:doc::javacOptions
[warn]     unused-sample/compile:compile::javacOptions
[warn]     unused-sample/test:doc::javacOptions
[warn]     unused-sample/test:compile::javacOptions
```
