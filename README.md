groovy-wrapper
==============

A modified version of the [GroovyWrapper script](http://groovy.codehaus.org/WrappingGroovyScript "Original GroovyWrapper script") 
allowing passing multiple jars to be embedded in the final jar (thus creating
an uberjar). 

Further modified to support files in lib and grapes directory, following specific conventions

Syntax is the same as the original script, plus two more options:

* `-i` / `--include`: a list of jars to include in the destination jar
* `-x`/ `--exclude`: a list of patterns matching files to exclude from the
  excluded jars
* `-l`/ `--lib`: Include all from ~/.groovy/lib
* `-g`/ `--grab`:  Include ~/.groovy/grapes from @Grab statements

**Example:** `groovy GroovyWrapper.groovy -m Hello -c -i pretty-print.jar
colors.jar -x 'META-INF/*.DSA'` will produce a `Hello.jar` including all the
content of `pretty-print.jar` and `colors.jar` except for files matching the
pattern `META-INF/*.DSA` (ie. `.DSA` files in the `META-INF` directory of those
jars)
