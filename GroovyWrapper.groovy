/**
* Copyright 2002-2007 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/*
* Original script at http://groovy.codehaus.org/WrappingGroovyScript
*/

/**
* Wrap a script and groovy jars to an executable jar
*/
def cli = new CliBuilder()
cli.h( longOpt: 'help', required: false, 'show usage information' )
cli.d( longOpt: 'destfile', argName: 'destfile', required: false, args: 1, 'jar destination filename, defaults to {mainclass}.jar' )
cli.m( longOpt: 'mainclass', argName: 'mainclass', required: true, args: 1, 'fully qualified main class, eg. HelloWorld' )
cli.c( longOpt: 'groovyc', required: false, 'Run groovyc' )
cli.i( longOpt: 'include', required: false, args: 1764, valueSeparator: ' ' as char, 'a list of jars to include into the destination jar' )
cli.x( longOpt: 'exclude', required: false, args: 1764, valueSeparator: ' ' as char, 'a list of file patterns to exclude from included jars' )
cli.g( longOpt: 'grab', required: false, 'Include ~/.groovy/grapes from @Grab statements' )
cli.l( longOpt: 'lib', required: false, 'Include all from ~/.groovy/lib' )
//--------------------------------------------------------------------------
def opt = cli.parse(args)
if (!opt) { return }
if (opt.h) {
    cli.usage();
    return
}

def mainClass = opt.m
def scriptBase = mainClass.replace( '.', '/' )
def scriptFile = new File( scriptBase + '.groovy' )
if (!scriptFile.canRead()) {
    println "Cannot read script file: '${scriptFile}'"
    return
}
def destFile = scriptBase + '.jar'
if (opt.d) {
    destFile = opt.d
}

//--------------------------------------------------------------------------
def ant = new AntBuilder()

if (opt.c) {
    ant.echo( "Compiling ${scriptFile}" )
    org.codehaus.groovy.tools.FileSystemCompiler.main( [ scriptFile ] as String[] )
}

def GROOVY_HOME = new File( System.getenv('GROOVY_HOME') )
if (!GROOVY_HOME.canRead() || GROOVY_HOME == "") {
    ant.echo( "Missing environment variable GROOVY_HOME: '${GROOVY_HOME}'" )
    return
}

def HOME = new File(System.getProperty("user.home"))
if (!HOME.canRead() || HOME == "") {
    ant.echo( "Missing home directory(lib and grapes): '${HOME}'" )
    return
}

def supJars = []

def grapes = []
def libs = []

ant.jar( destfile: destFile, compress: true, index: true ) {

    classes = ((scriptFile.getParent()) ? scriptFile.getParent() : "") + '/*.class'
    if ( classes.startsWith( '/' ) ) classes = '*.class'
    fileset( dir: '.', includes: classes )

    // Embedded Groovy jars
    zipgroupfileset( dir: GROOVY_HOME, includes: 'embeddable/groovy-all-*.jar' )
    zipgroupfileset( dir: GROOVY_HOME, includes: 'lib/commons*.jar' )
    if (opt.g) {
        // Selective Grap (doesn't do dependencies, but good enough). Generally the @Grab will be commented out when compiling to jar
        sfLines = scriptFile.readLines()
        for (a in sfLines) {
            if (a.contains("@Grab")) {
                s = a.indexOf("(")
                e = a.indexOf(")")
                c = a.substring(s, e + 1)
                c = c.replace("(", "").replace(")", "").replace('"', "").replace("'", "") // stripping
                g = c.split(":") // this is what I want to get from the grapes dir
                j = "${g[0]}/${g[1]}/jars/${g[1]}-${g[2]}.jar"
                grapes.add(j)
                zipgroupfileset(dir: new File(HOME, ".groovy/grapes"), includes: "${g[0]}/${g[1]}/jars/${g[1]}-${g[2]}.jar")

            }
        }
    }
    if (opt.l) {
        // All in lib
        zipgroupfileset(dir: HOME, includes: '.groovy/lib/*.jar')
        libs =  new File(HOME, '.groovy/lib').list()
    }


    // Other jars to include from -i option
    if (opt.is) {
        opt.is.each {
            if (it.endsWith('.jar')) {
                jarFile = new File(it)
                zipfileset(src: it) {
                    // exclude patterns from -x option
                    if (opt.xs) {
                        opt.xs.each {
                            exclude(name: it)
                        }
                    }
                }
                supJars << new File(it).getName()
            }
        }
    }

    manifest {
        attribute( name: 'Main-Class', value: mainClass )
    }
}

supJars.each {
    ant.echo( "Added supplemental jar: " + it )
}
grapes.each{
    ant.echo("Added jar from grapes: $it")
}

libs.each{
    ant.echo("Added jar from lib: $it")
}
ant.echo( "Run script using: \'java -jar ${destFile} ...\'" )